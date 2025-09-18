package ar.edu.utn.frbb.tup.model.cuenta;

public enum TipoMoneda {
    PESOS("Pesos"),
    DOLARES("Dolares");

    private final String descripcion;

    TipoMoneda(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoMoneda fromString(String text) {
        for (TipoMoneda tipo : TipoMoneda.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoMoneda con la descripción: " + text);
    }

}
