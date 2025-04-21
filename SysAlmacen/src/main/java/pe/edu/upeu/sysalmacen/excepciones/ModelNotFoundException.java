package pe.edu.upeu.sysalmacen.excepciones;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ModelNotFoundException extends RuntimeException {
    private final HttpStatus status;  // Hacemos que 'status' sea final

    // Constructor con solo el mensaje
    public ModelNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;  // Asignamos un valor predeterminado si no se pasa un estado
    }

    // Constructor con el mensaje y el estado HTTP
    public ModelNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
