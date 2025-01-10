package ar.edu.utn.frbb.tup.controller.dto;


public class ClienteDto extends PersonaDto {
    private String tipoPersona;
    private String banco;
    private int score;

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
