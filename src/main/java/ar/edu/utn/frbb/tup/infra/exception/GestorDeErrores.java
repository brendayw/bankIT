package ar.edu.utn.frbb.tup.infra.exception;

import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.account.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.account.exceptions.CuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.account.exceptions.TipoCuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.PrestamoNoExisteException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestorDeErrores {

    //not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity gestionarError404() {
        return ResponseEntity.notFound().build();
    }

    //no content
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity gestionarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream().map(DatosErorValidacion::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity gestionarErrorDeValidacion(ValidacionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler({ClientNoExisteException.class, CuentaNoExisteException.class, PrestamoNoExisteException.class})
    public ResponseEntity<String> gestionarClienteOPrestamoNoEncontrado(RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler({ClienteAlreadyExistsException.class, TipoCuentaYaExisteException.class, CuentaYaExisteException.class})
    public ResponseEntity<String> gestionarErrorPorYaExistente(RuntimeException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }



    public record DatosErorValidacion(
            String campo,
            String mensaje
    ) {
        public DatosErorValidacion(FieldError error) {
            this(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }
    }
}