package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.cuenta.Account;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private List<Client> clientes = new ArrayList<>();
    private List<Account> cuentas = new ArrayList<>();


    //getters y setters

    public List<Client> getClientes() {
        return clientes;
    }

    public void setClientes(List<Client> clientes) {
        this.clientes = clientes;
    }

    public List<Account> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Account> cuentas) {
        this.cuentas = cuentas;
    }
}
