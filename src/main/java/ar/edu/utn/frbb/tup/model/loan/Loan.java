package ar.edu.utn.frbb.tup.model.loan;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
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
    private Double requestedAmount;

    @Column(name = "monto_total")
    private Double totalAmount; //con intereses calculados

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "cuenta")
    private AccountType accountType;

    @Column(name = "plazo_meses")
    private int termInMonths;

    private double interes;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @Column(name = "fecha_alta")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> cuotas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "plan_pagos_id")
    private PaymentPlan paymentPlan;

    public Loan(LoanRequestDto dto, Client client) {
        this.id = null;
        this.client = client;
        this.requestedAmount = dto.requestedAmount();
        this.currencyType = dto.currencyType();
        this.accountType = dto.accountType();
        this.termInMonths = dto.termInMonths();
    }

    @Override
    public String toString() {
        return "Loan: " +
                "\nId: " + id +
                "\nCurrency Type: " + currencyType +
                "\nAccount Type: " + accountType +
                "\nTerm In Months: " + termInMonths;
    }

    public void close() {
        this.loanStatus = LoanStatus.CERRADO;
    }
}