package ar.edu.utn.frbb.tup.model.account;

import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "cuentas")
@Entity(name = "Cuenta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "fecha_creacion")
    private LocalDate creationDate;

    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta")
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_moneda")
    private CurrencyType currencyType;

    @Column(name = "estado")
    private boolean active;

    public Account(AccountDto dto) {
        this.id = null;
        this.active = true;
        this.creationDate = LocalDate.now();
        this.balance = dto.balance();
        this.accountType = dto.accountType();
        this.currencyType = dto.currencyType();
    }

    @Override
    public String toString() {
        return "Account: " +
                "\nId =" + id +
                "\nCreation Date=" + creationDate +
                "\nBalance=" + balance +
                "\nAccount Type=" + accountType +
                "\nCurrency Type=" + currencyType +
                "\nStatus=" + active;
    }

    public void deactivate() {
        this.active = false;
    }
}