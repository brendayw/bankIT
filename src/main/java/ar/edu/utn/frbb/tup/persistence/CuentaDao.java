package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cuenta;

import java.util.List;

public interface CuentaDao {
    Cuenta find(long id);
    List<Cuenta> findAll();
    Cuenta findByClienteYTipoMoneda(long dni, String tipoMoneda);
    void save(Cuenta cuenta);
    List<Cuenta> buscarCuentasByCliente(long dni);
    Cuenta update(Cuenta cuenta);

}
