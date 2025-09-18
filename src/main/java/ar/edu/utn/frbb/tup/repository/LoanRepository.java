package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.loan.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findByClientPersonaDni(Long dni, Pageable pagination);
}
