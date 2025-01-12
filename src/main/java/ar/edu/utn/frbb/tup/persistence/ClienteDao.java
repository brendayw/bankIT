package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.persistence.entity.ClienteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteDao extends AbstractBaseDao {

    @Autowired
    CuentaDao cuentaDao;
    @Autowired
    PrestamoDao prestamoDao;

    @Override
    protected String getEntityName() {
        return "CLIENTE";
    }

    public Cliente find(long dni, boolean loadComplete) {
        if (getInMemoryDatabase().get(dni) == null)
            return null;
        Cliente cliente = ((ClienteEntity) getInMemoryDatabase().get(dni)).toCliente();
        if (loadComplete) {
            for (Cuenta cuenta :
                    cuentaDao.buscarCuentasByCliente(dni)) {
                cliente.addCuenta(cuenta);
            }
            for (Prestamo prestamo :
            prestamoDao.buscarPrestamoPorCliente(dni)) {
                cliente.addPrestamo(prestamo);
            }
        }
        return cliente;
    }

    public void save(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity(cliente);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public Cliente update(Cliente cliente) throws ClientNoExisteException {
        Cliente actualizado = find(cliente.getDni(), true);
        if(actualizado  != null) {
            if (cliente.getEmail() != null) {
                actualizado.setEmail(cliente.getEmail());
            }
            if (cliente.getTelefono() != null) {
                actualizado.setTelefono(cliente.getTelefono());
            }
            System.out.println("Datos actualizados con exito.");
        } else {
            throw new ClientNoExisteException("Cliente no encontrado.");
        }
        return actualizado;
    }
}
