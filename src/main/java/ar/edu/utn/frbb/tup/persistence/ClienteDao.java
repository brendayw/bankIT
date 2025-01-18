package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;

import java.util.List;

public interface ClienteDao {
    Cliente find(long dni, boolean loadComplete);
    List<Cliente> findAll();
    void save(Cliente cliente);
    Cliente update(Cliente cliente) throws ClientNoExisteException;

}
