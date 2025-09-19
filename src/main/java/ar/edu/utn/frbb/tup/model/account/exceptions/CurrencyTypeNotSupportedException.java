package ar.edu.utn.frbb.tup.model.account.exceptions;

public class CurrencyTypeNotSupportedException extends RuntimeException {
    public CurrencyTypeNotSupportedException(String message) {
        super(message);
    }
}
