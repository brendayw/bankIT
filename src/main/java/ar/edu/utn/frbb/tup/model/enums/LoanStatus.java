package ar.edu.utn.frbb.tup.model.enums;

public enum LoanStatus {
    PENDIENTE("P"),
    APROBADO("A"),
    RECHAZADO("R"),
    DESEMBOLSADO("D"),
    CERRADO("C");

    private final String type1;

    LoanStatus(String type1) {
        this.type1 = type1;
    }

    public static LoanStatus fromString(String typeStr) {
        for (LoanStatus type1 : LoanStatus.values()) {
            if (type1.type1.equals(typeStr)) {
                return type1;
            }
        }
        throw new IllegalArgumentException("Estado del prestamo no valido: " + typeStr);
    }
}
