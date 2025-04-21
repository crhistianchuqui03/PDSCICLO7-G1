package pe.edu.upeu.sysalmacen.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    // Tiempo de expiración del JWT (5 horas)
    private static final long JWT_TOKEN_VALIDITY = 5L * 60 * 60 * 1000; // 5 horas en milisegundos

    // Clave secreta utilizada para firmar el JWT (es importante mantenerla segura)
    @Value("${jwt.secret}") // La clave secreta debe ser configurada en application.properties
    private String secret;

    /**
     * Genera un JWT para el usuario detallado en base a sus autoridades.
     *
     * @param userDetails Detalles del usuario para incluir en el JWT.
     * @return El token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Añadimos los roles del usuario al token
        claims.put("role", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))); // Ejemplo: ADMIN,USER,DBA

        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Método privado que realiza la generación del token JWT.
     *
     * @param claims   Las reclamaciones (datos) que se incluirán en el token.
     * @param username El nombre de usuario que será el sujeto del token.
     * @return El JWT generado.
     */
    private String doGenerateToken(Map<String, Object> claims, String username) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());

        return Jwts.builder()
                .claims(claims)                // Añadimos las reclamaciones al token
                .subject(username)             // Establecemos el nombre de usuario como el sujeto del token
                .issuedAt(new Date(System.currentTimeMillis()))  // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Fecha de expiración
                .signWith(key)                 // Firmamos el token con la clave secreta
                .compact();
    }

    /**
     * Obtiene todas las reclamaciones del token JWT.
     *
     * @param token El token JWT.
     * @return Las reclamaciones del token.
     */
    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());

        // Usamos la nueva API para evitar el uso de métodos obsoletos.
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene una reclamación específica del token JWT.
     *
     * @param token          El token JWT.
     * @param claimsResolver La función para resolver la reclamación.
     * @param <T>            El tipo de la reclamación.
     * @return La reclamación solicitada.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene el nombre de usuario del token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario (sujeto) del token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Obtiene la fecha de expiración del token JWT.
     *
     * @param token El token JWT.
     * @return La fecha de expiración del token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token El token JWT.
     * @return Verdadero si el token ha expirado, de lo contrario falso.
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Valida si el token es válido comparando el nombre de usuario y verificando la expiración.
     *
     * @param token       El token JWT.
     * @param userDetails Los detalles del usuario.
     * @return Verdadero si el token es válido, de lo contrario falso.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
