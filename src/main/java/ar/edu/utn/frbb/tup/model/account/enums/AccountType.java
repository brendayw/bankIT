package ar.edu.utn.frbb.tup.model.account.enums;

public enum AccountType {
    CUENTA_CORRIENTE("Corriente"),
    CAJA_AHORRO("Ahorro");

    private final String descripcion;

    AccountType(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static AccountType fromString(String text) {
        for (AccountType tipo : AccountType.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoCuenta con la descripción: " + text);
    }
}