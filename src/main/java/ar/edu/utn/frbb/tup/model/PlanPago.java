package ar.edu.utn.frbb.tup.model;

import java.util.HashMap;
import java.util.Map;

public class PlanPago {
    private int cuotaNro;
    private double monto;

    // Constructor
    public PlanPago(int cuotaNro, double monto) {
        this.cuotaNro = cuotaNro;
        this.monto = monto;
    }

    // Getters y setters
    public int getCuotaNro() {
        return cuotaNro;
    }
    public void setCuotaNro(int cuotaNro) {
        this.cuotaNro = cuotaNro;
    }

    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

    //metodos
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("cuotaNro", cuotaNro);
        map.put("monto", monto);
        return map;
    }
}