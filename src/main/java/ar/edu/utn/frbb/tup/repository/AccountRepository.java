package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByClientPersonDniAndAccountTypeAndCurrencyType(Long dni, AccountType accountType, CurrencyType currencyType);
    Page<Account> findAccountsByClientPersonDniAndActiveTrue(Long dni, Pageable pageable);
}