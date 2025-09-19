package ar.edu.utn.frbb.tup.model.loan.exceptions;

public class CreditScoreException extends RuntimeException {
    public CreditScoreException(String message) {
        super(message);
    }
}