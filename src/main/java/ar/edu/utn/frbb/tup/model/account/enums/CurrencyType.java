package ar.edu.utn.frbb.tup.model.account.enums;

public enum CurrencyType {
    PESOS("Pesos"),
    DOLARES("Dolares");

    private final String descripcion;

    CurrencyType(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static CurrencyType fromString(String text) {
        for (CurrencyType tipo : CurrencyType.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoMoneda con la descripción: " + text);
    }
}