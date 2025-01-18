package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;

import java.util.Set;

public interface CreditScoreService {
    int calcularScore(Set<Prestamo> prestamo);
    void validarScore(Cliente cliente) throws CreditScoreException;
}
