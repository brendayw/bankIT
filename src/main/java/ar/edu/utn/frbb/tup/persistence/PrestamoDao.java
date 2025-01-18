package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;

import java.util.List;

public interface PrestamoDao {
    void savePrestamo(Prestamo prestamo);
    Prestamo findPrestamo(long id);
    List<Prestamo> findAll();
    List<Prestamo> buscarPrestamoPorCliente(long dni);
    Prestamo update(Prestamo prestamo);
}
