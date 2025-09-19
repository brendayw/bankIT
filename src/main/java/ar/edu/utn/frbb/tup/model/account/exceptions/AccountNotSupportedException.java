package ar.edu.utn.frbb.tup.model.account.exceptions;

public class AccountNotSupportedException extends RuntimeException  {
    public AccountNotSupportedException(String message) {
        super(message);
    }
}