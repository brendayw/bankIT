package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CreditScoreService {
    private static final int MIN_SCORE = 500;
    private static final int MAX_SCORE = 1000;
    private static final int SCORE_MINIMO = 600;

    public int calculateScore(Set<Loan> prestamos) {
        Random random = new Random();
        int score = MIN_SCORE + random.nextInt(150);
        for (Loan prestamo : prestamos) {
            if (prestamo.getLoanStatus().equals(LoanStatus.APROBADO)) {
                score += 10;
            }
        }
        return Math.max(MIN_SCORE, Math.min(score, MAX_SCORE));
    }

//    public void validateScore(Client cliente) throws CreditScoreException {
//        Set<Loan> prestamosList = new HashSet<>(cliente.getPrestamos());
//        int score = calculateScore(prestamosList);
//        if (score < SCORE_MINIMO) {
//            throw new CreditScoreException("El cliente no tiene puntaje suficiente para solicitar el préstamo.
//            Puntaje: " + score);
//        }
//    }

}


