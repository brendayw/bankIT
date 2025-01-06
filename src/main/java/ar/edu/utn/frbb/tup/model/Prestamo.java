package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;

public class Prestamo {
    private long id_loan;
    Cliente numeroCliente;
    private double amount;  // Monto del préstamo
    private double interestRate; // Tasa de interés anual
    private int termMonths;// Plazo en meses (12 meses, 24 meses, etc.)
    private String requestDate;
    private String approvalDate;
    private LoanStatus loanStatus;
    private String moneda;
    private double cuotaMensual;
    private int cuotasPagadas;
    private int cuotasRestantes;

    //constructores
    public Prestamo(PrestamoDto prestamoDto, Cliente cliente) {
        this.numeroCliente = cliente;
        this.amount = prestamoDto.getMontoPrestamo();
        this.termMonths = prestamoDto.getPlazoMeses();
        this.moneda = prestamoDto.getMoneda();
    }
    public Prestamo() {

    }
    public Prestamo(long id_loan, double amount, double interestRate, int termMonths, String requestDate, String approvalDate, LoanStatus loanStatus, String moneda) {
        this.id_loan = id_loan;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.requestDate = requestDate;
        this.approvalDate = approvalDate;
        this.loanStatus = loanStatus;
        this.moneda = moneda;
        this.cuotaMensual = cuotaMensual;
        this.cuotasPagadas = cuotasPagadas;
        this.cuotasRestantes = cuotasRestantes;
    }

    /*// Métodos de validación
    private boolean isValidLoanId(long id_loan) {
        return id_loan != 0;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    private boolean isValidInterestRate(double interestRate) {
        return interestRate > 0 && interestRate <= 100;
    }

    private boolean isValidTermMonths(int termMonths) {
        return termMonths > 0 && termMonths <= 120; // de 1 a 10 años
    }*/

    // getters & setters
    public long getId_loan() {
        return id_loan;
    }
    public void setId_loan(long id_loan) {
        this.id_loan = id_loan;
    }

    public Cliente getNumeroCliente() {
        return numeroCliente;
    }
    public void setNumeroCliente(Cliente numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }
    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }
    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getRequestDate() {
        return requestDate;
    }
    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getApprovalDate() {
        return approvalDate;
    }
    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getMoneda() {
        return moneda;
    }
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }
    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public int getCuotasPagadas() {
        return cuotasPagadas;
    }
    public void setCuotasPagadas(int cuotasPagadas) {
        this.cuotasPagadas = cuotasPagadas;
    }

    public int getCuotasRestantes() {
        return cuotasRestantes;
    }
    public void setCuotasRestantes(int cuotasRestantes) {
        this.cuotasRestantes = cuotasRestantes;
    }

    @Override
    public String toString() {
        return "Prestamo: " +
                "\nId del prestamo: " + id_loan +
                "\nNumero de cliente: " + numeroCliente +
                "\nMonto total: " + amount +
                "\nMoneda: " + moneda +
                "\nTasa de interes: " + interestRate +
                "\nPlazo en meses: " + termMonths +
                "\nFecha de solicitud: " +requestDate +
                "\nFecha de aprovacion: " + approvalDate +
                "\nEstado: " + loanStatus +
                "\nMonto cuota mensual: " + cuotaMensual +
                "\nCuotas pagadas: " + cuotasPagadas +
                "\nCuotas restantes: " + cuotasRestantes;
    }
}