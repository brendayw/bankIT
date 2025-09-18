package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByActiveTrue(Pageable pagination);

    boolean existsByClientPersonaDniAndTipoCuentaAndTipoMoneda(Long dni, TipoCuenta tipoCuenta, TipoMoneda tipoMoneda);

    Page<Account> findAccountsByClientPersonaDniAndActiveTrue(Long dni, Pageable pageable);

}
