package ar.edu.utn.frbb.tup.model.cuenta;

public enum TipoCuenta {
    CUENTA_CORRIENTE("Corriente"),
    CAJA_AHORRO("Ahorro");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoCuenta fromString(String text) {
        for (TipoCuenta tipo : TipoCuenta.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoCuenta con la descripción: " + text);
    }
}

