package pe.edu.upeu.sysalmacen.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {

    private Long idUsuario;

    @NotNull
    private String user;

    @NotNull
    private String estado;

    private String token;

    // Record para almacenar las credenciales de usuario (considera personalizar equals y hashCode para arrays)
    public record CredencialesDto(String user, char[] clave) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CredencialesDto that = (CredencialesDto) o;
            return user.equals(that.user) && java.util.Arrays.equals(clave, that.clave);
        }

        @Override
        public int hashCode() {
            int result = user.hashCode();
            result = 31 * result + java.util.Arrays.hashCode(clave);
            return result;
        }

        @Override
        public String toString() {
            return "CredencialesDto{" +
                    "user='" + user + '\'' +
                    ", clave=" + java.util.Arrays.toString(clave) +
                    '}';
        }
    }

    // Record para crear un nuevo usuario
    public record UsuarioCrearDto(String user, char[] clave, String rol, String estado) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioCrearDto that = (UsuarioCrearDto) o;
            return user.equals(that.user) && java.util.Arrays.equals(clave, that.clave);
        }

        @Override
        public int hashCode() {
            int result = user.hashCode();
            result = 31 * result + java.util.Arrays.hashCode(clave);
            return result;
        }

        @Override
        public String toString() {
            return "UsuarioCrearDto{" +
                    "user='" + user + '\'' +
                    ", clave=" + java.util.Arrays.toString(clave) +
                    ", rol='" + rol + '\'' +
                    ", estado='" + estado + '\'' +
                    '}';
        }
    }
}
