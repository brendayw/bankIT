package ar.edu.utn.frbb.tup.controller.handler;

import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cliente.TipoPersonaNoSoportada;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TupResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    //BAD_REQUEST (400)
    //datos de entrada inválidos o no soportados
    @ExceptionHandler({
            TipoMonedaNoSoportada.class,
            TipoPersonaNoSoportada.class,
            CampoIncorrecto.class,
            CuentaNoSoportadaException.class})
    protected ResponseEntity<Object> handleUnsupportedOrInvalidInputs(Exception ex, WebRequest request) {
        CustomApiError error = new CustomApiError();// el json que devuelve el error
        error.setErrorCode(400);
        error.setErrorMessage(ex.getMessage());// settea el mensaje que le claves en el json
        return handleExceptionInternal(ex, error,//aca hace sus chiches
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);// aca ponele el status code
    }

    //BAD_REQUEST (400)
    //errores relacionados a las reglas de negocio (cliente menor de edad, saldo insuficiente)
    @ExceptionHandler({IllegalArgumentException.class, ClienteMayorDeEdadException.class, /*NoAlcanzaException.class*/})
    protected ResponseEntity<Object> handleInvalidBusinessRules(Exception ex, WebRequest request) {
        CustomApiError error = new CustomApiError();
        error.setErrorCode(400);
        error.setErrorMessage(ex.getMessage());
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    //CONFLICT (409)
    //conflictos por recursos ya existentes
    @ExceptionHandler({TipoCuentaYaExisteException.class, CuentaYaExisteException.class, ClienteAlreadyExistsException.class})
    protected ResponseEntity<Object> handleResourceAlreadyExists(Exception ex, WebRequest request) {
        CustomApiError error = new CustomApiError();
        error.setErrorCode(409);
        error.setErrorMessage(ex.getMessage());
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    //NOT_FOUND (404)
    //recursos no encontrados (inexistentes)
    @ExceptionHandler({PrestamoNoExisteException.class, ClientNoExisteException.class, CuentaNoExisteException.class, })
    protected ResponseEntity<Object> handleResourceNotFound(Exception ex, WebRequest request) {
        CustomApiError error = new CustomApiError();
        error.setErrorCode(404);
        error.setErrorMessage(ex.getMessage());
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    //NOT_FOUND (404)
    //errores de estado ilegal
    @ExceptionHandler({IllegalStateException.class})
    protected ResponseEntity<Object> handleIllegalState(Exception ex, WebRequest request) {
        CustomApiError error = new CustomApiError();
        error.setErrorCode(404); // Código específico para identificar este error
        error.setErrorMessage(ex.getMessage());
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (body == null) {
            CustomApiError error = new CustomApiError();
            error.setErrorMessage(ex.getMessage());
            body = error;
        }
        return new ResponseEntity(body, headers, status);
    }

}