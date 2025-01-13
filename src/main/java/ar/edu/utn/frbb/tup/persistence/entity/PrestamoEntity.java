package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

public class PrestamoEntity extends BaseEntity {
    private final long id;
    private final long dniTitular;
    private final double monto;
    private final String tipoMoneda;
    private final int plazoMeses;
    private String estado;
    private String mensaje;

    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId());
        this.id = prestamo.getId();
        this.dniTitular = prestamo.getDniTitular();
        this.monto = prestamo.getMonto();
        this.tipoMoneda = prestamo.getMoneda() != null ? prestamo.getMoneda().getDescripcion() : null;
        this.plazoMeses = prestamo.getPlazoMeses();
        this.estado = prestamo.getLoanStatus() != null ? prestamo.getLoanStatus().getDescripcion() : null;
    }

    public Prestamo toPrestamo() {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(this.id);
        prestamo.setDniTitular(this.dniTitular);
        prestamo.setMonto(this.monto);
        prestamo.setMoneda(TipoMoneda.fromString(this.tipoMoneda));
        prestamo.setPlazoMeses(this.plazoMeses);
        prestamo.setLoanStatus(LoanStatus.fromString(this.estado));
        return prestamo;
    }

}
