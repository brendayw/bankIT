package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.model.enums.LoanStatus;

import java.util.List;

public class PrestamoDetalle {
    private LoanStatus estado;
    private String mensaje;
    List<PlanPago> planPagos;

    public PrestamoDetalle() {

    }

    public PrestamoDetalle(LoanStatus estado, String mensaje, List<PlanPago> planPagos) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.planPagos = planPagos;
    }

    //getters y setters
    public LoanStatus getEstado() {
        return estado;
    }

    public void setEstado(LoanStatus estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<PlanPago> getPlanPagos() {
        return planPagos;
    }

    public void setPlanPagos(List<PlanPago> planPagos) {
        this.planPagos = planPagos;
    }

    @Override
    public String toString() {
        return "PrestamoDetalle{" +
                "estado='" + estado + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", planPagos=" + planPagos +
                '}';
    }
}
