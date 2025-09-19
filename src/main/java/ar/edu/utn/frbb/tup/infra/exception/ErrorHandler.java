package ar.edu.utn.frbb.tup.infra.exception;

import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountTypeAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.users.exceptions.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    //not found
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity handleError404() {
        return ResponseEntity.notFound().build();
    }

    //no content
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream().map(DatosErorValidacion::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleValidationError(ValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler({ClientNotFoundException.class, AccountNotFoundException.class, LoanNotFoundException.class})
    public ResponseEntity<String> hanldeClientOrLoanOrAccountNotFound(RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler({ClientAlreadyExistsException.class, AccountTypeAlreadyExistsException.class, AccountAlreadyExistsException.class})
    public ResponseEntity<String> handleAlreadyExists(RuntimeException e) {
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