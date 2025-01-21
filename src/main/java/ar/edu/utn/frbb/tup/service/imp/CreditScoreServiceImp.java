package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.service.CreditScoreService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CreditScoreServiceImp implements CreditScoreService {
    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 1000;
    private static final int SCORE_MINIMO = 600;

    public int calcularScore(Set<Prestamo> prestamos) {
        Random random = new Random();
        int score = SCORE_MINIMO + random.nextInt(150);
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLoanStatus().equals(LoanStatus.APROBADO)) {
                score += 10;
            }
        }
        return Math.max(MIN_SCORE, Math.min(score, MAX_SCORE));
    }

    public void validarScore(Cliente cliente) throws CreditScoreException {
        Set<Prestamo> prestamosList = new HashSet<>(cliente.getPrestamos());
        int score = calcularScore(prestamosList);
        if (score < SCORE_MINIMO) {
            throw new CreditScoreException("El cliente no tiene puntaje suficiente para solicitar el préstamo. Puntaje: " + score);
        }
    }

}

