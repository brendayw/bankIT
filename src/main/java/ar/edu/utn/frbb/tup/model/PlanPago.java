package ar.edu.utn.frbb.tup.model;

public class PlanPago {
    private int cuotaNum;
    private double monto;

    //constructor
    public PlanPago(int cuotaNum, double monto) {
        this.cuotaNum = cuotaNum;
        this.monto = monto;
    }

    //getters y setters
    public int getCuotaNum() {
        return cuotaNum;
    }

    public void setCuotaNum(int cuotaNum) {
        this.cuotaNum = cuotaNum;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
