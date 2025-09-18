package ar.edu.utn.frbb.tup.model.prestamo;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.payment.Payment;
import ar.edu.utn.frbb.tup.model.payment.PaymentPlan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "prestamos")
@Entity(name = "Prestamo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client client; //del cliente

    @Column(name = "monto_solicitado")
    private Double montoSolicitado;

    @Column(name = "monto_total")
    private Double montoTotal; //con intereses calculados

    @Enumerated(EnumType.STRING)
    private TipoMoneda moneda;

    @Enumerated(EnumType.STRING)
    private TipoCuenta cuenta;

    @Column(name = "plazo_meses")
    private int plazoMeses;

    private double interes;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> cuotas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "plan_pagos_id")
    private PaymentPlan paymentPlan;

    public Loan(LoanRequestDto dto, Client client) {
        this.id = null;
        this.client = client;
        this.montoSolicitado = dto.montoSolicitado();
        this.moneda = dto.tipoMoneda();
        this.cuenta = dto.tipoCuenta();
        this.plazoMeses = dto.plazoMeses();
    }

    @Override
    public String toString() {
        return "Prestamo: " +
                "\nId del prestamo: " + id +
                "\nMoneda: " + moneda +
                "\nCuenta: " + cuenta +
                "\nPlazo en meses: " + plazoMeses;
    }

    public void close() {
        this.loanStatus = LoanStatus.CERRADO;
    }
}