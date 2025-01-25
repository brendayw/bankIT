package ar.edu.utn.frbb.tup.controller.dto;

import ar.edu.utn.frbb.tup.model.Cliente;

public class ClienteDto extends PersonaDto {
    private String tipoPersona;
    private String banco;
    private int score;

    public ClienteDto() {

    }

    public ClienteDto(Cliente cliente) {
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.dni = cliente.getDni();
        this.fechaNacimiento = cliente.getFechaNacimiento().toString();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
    }

    public String getTipoPersona() {
        return tipoPersona;
    }
    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
