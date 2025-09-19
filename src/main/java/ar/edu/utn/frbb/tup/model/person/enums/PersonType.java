package ar.edu.utn.frbb.tup.model.person.enums;

public enum PersonType {
    PERSONA_FISICA("Fisica"),
    PERSONA_JURIDICA("Juridica");

    private final String descripcion;

    PersonType(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static PersonType fromString(String text) {
        for (PersonType tipo : PersonType.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoPersona con la descripción: " + text);
    }
}