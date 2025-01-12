package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;

public class PrestamoEntity extends BaseEntity {
    private final long id;
    private final long dniTitular;
    private final double monto;
    private final String tipoMoneda;
    private final int plazoMeses;
    private String estado;
    private String mensaje;
    //private final double cuotaMensual;
    //private final int cuotasPagadas;
    //private final int cuotasRestantes;
    //private final LocalDate solicitudFecha;
    //private final LocalDate aprobacionFecha;


    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId());
        this.id = prestamo.getId();
        this.dniTitular = prestamo.getDniTitular();
        this.monto = prestamo.getMonto();
        this.tipoMoneda = prestamo.getMoneda() != null ? prestamo.getMoneda().getDescripcion() : null;
        //this.cuotaMensual = prestamo.getCuotaMensual();
        this.plazoMeses = prestamo.getPlazoMeses();
        //this.cuotasPagadas = prestamo.getCuotasPagadas();
        //this.cuotasRestantes = prestamo.getCuotasRestantes();
        //this.solicitudFecha = prestamo.getSolicitudFecha();
        //this.aprobacionFecha = prestamo.getAprovacionFecha();
        this.estado = prestamo.getLoanStatus() != null ? prestamo.getLoanStatus().getDescripcion() : null;
    }

    public Prestamo toPrestamo() {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(this.id);
        prestamo.setDniTitular(this.dniTitular);
        prestamo.setMonto(this.monto);
        prestamo.setMoneda(TipoMoneda.fromString(this.tipoMoneda));
        //prestamo.setCuotaMensual(this.cuotaMensual);
        prestamo.setPlazoMeses(this.plazoMeses);
       // prestamo.setCuotasPagadas(this.cuotasPagadas);
        //prestamo.setCuotasRestantes(this.cuotasRestantes);
        //prestamo.setSolicitudFecha(this.solicitudFecha);
        //prestamo.setAprovacionFecha(this.aprobacionFecha);
        prestamo.setLoanStatus(LoanStatus.fromString(this.estado));
        return prestamo;
    }

}
