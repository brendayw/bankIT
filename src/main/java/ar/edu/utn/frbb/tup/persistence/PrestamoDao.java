package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrestamoDao extends AbstractBaseDao {

    @Override
    protected String getEntityName() {
        return "PRESTAMO";
    }

    public void savePrestamo(PrestamoEntity prestamoEntity) {
        getInMemoryDatabase().put(prestamoEntity.getId(), prestamoEntity);
    }

    //busca prestamo por id del prestamo
    public Prestamo findPrestamo(long id) {
        if (getInMemoryDatabase().get(id) == null) {
            return null;
        }
        return ((PrestamoEntity) getInMemoryDatabase().get(id)).toPrestamo();
    }

    //obtiene prestamos por numero de cliente
    public List<Prestamo> getPrestamoByCliente(long dni) {
        List<Prestamo> prestamosDelCliente = new ArrayList<>();
        for (Object object: getInMemoryDatabase().values()) {
            PrestamoEntity prestamo = ((PrestamoEntity) object);
            if (prestamo.getNumeroCliente() == dni) {
                prestamosDelCliente.add(prestamo.toPrestamo());
            }
        }
        return prestamosDelCliente;
    }

    public Prestamo update(long id, LoanStatus estado) {
        PrestamoEntity prestamoEntity = (PrestamoEntity) getInMemoryDatabase().get(id);
        if (prestamoEntity == null) {
            throw new IllegalArgumentException("El prestamo con ID: " + id + " no existe.");
        }
        prestamoEntity.setEstado(estado);
        getInMemoryDatabase().put(id, prestamoEntity);
        return prestamoEntity.toPrestamo();
    }
}
