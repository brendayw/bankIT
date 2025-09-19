package ar.edu.utn.frbb.tup.model.loan.enums;

public enum LoanStatus {
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    CERRADO("Cerrado");

    private final String descripcion;

    LoanStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public static LoanStatus fromString(String text) {
        for (LoanStatus tipo : LoanStatus.values()) {
            if (tipo.descripcion.equals(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Estado del prestamo no valido: " + text);
    }
}