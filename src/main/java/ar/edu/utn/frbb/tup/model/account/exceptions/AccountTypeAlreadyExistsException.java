package ar.edu.utn.frbb.tup.model.account.exceptions;

public class AccountTypeAlreadyExistsException extends RuntimeException {
    public AccountTypeAlreadyExistsException(String message) {
        super(message);
    }
}
