package ar.edu.utn.frbb.tup.model;

import java.util.HashMap;
import java.util.Map;

public class PlanPago {
    private int cuotaNro;
    private double montoCuota;

    // Constructor
    public PlanPago(int cuotaNro, double montoCuota) {
        this.cuotaNro = cuotaNro;
        this.montoCuota = montoCuota;
    }

    // Getters y setters
    public int getCuotaNro() {
        return cuotaNro;
    }
    public void setCuotaNro(int cuotaNro) {
        this.cuotaNro = cuotaNro;
    }

    public double getMontoCuota() {
        return montoCuota;
    }
    public void setMontoCuota(double montoCuota) {
        this.montoCuota = montoCuota;
    }

    //metodos
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("cuotaNro", cuotaNro);
        map.put("monto", montoCuota);
        return map;
    }

    public String toString() {
        return "Plan de pago: " +
                "\ncuota numero: " + cuotaNro +
                "\nmonto cuota: " + montoCuota;
    }
}