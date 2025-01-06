package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import org.springframework.stereotype.Service;

@Service
public class CreditScoreService {

    //validar el creditscore
    public void validarScore(Cliente cliente) {
        if (cliente.getScore() < 600) {
            throw new IllegalArgumentException("El cliente no tiene puntaje suficiente para solicitar prestamo.");
        }
    }
}
