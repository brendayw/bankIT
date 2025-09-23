package ar.edu.utn.frbb.tup.model.client;

import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "clientes")
@Entity(name = "Cliente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo")
    @Builder.Default
    private boolean active = true;

    @Embedded
    private Person person;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "tipo_persona")
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @Column(name = "fecha_alta")
    @Builder.Default
    private LocalDate registrationDate = LocalDate.now();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Loan> loans = new HashSet<>();

    public Client(ClientDto dto) {
        this.id = null;
        this.active = true;
        this.personType = dto.personType();
        this.registrationDate = LocalDate.now();
        this.person = new Person(dto.person());
    }

    //factory method
    public static Client createFromDto(ClientDto dto) {
        return Client.builder()
                .person(new Person(dto.person()))
                .personType(dto.personType())
                .build();
    }

    public void associateWithUser(User user) {
        if (this.user != null && this.user.equals(user)) {
            return; // Ya está asociado
        }
        if (this.user != null) {
            User previousUser = this.user;
            this.user = null;
            if (previousUser.getClient() == this) {
                previousUser.removeClient();
            }
        }
        this.user = user;
        if (user != null && user.getClient() != this) {
            user.associateWithClient(this);
        }
    }

    public void removeUser() {
        if (this.user != null) {
            User currentUser = this.user;
            this.user = null;
            if (currentUser.getClient() == this) {
                currentUser.removeClient();
            }
        }
    }

    public void addAccount(Account account) {
        if (this.accounts == null) {
            this.accounts = new HashSet<>();
        }
        if (account != null && !this.accounts.contains(account)) {
            this.accounts.add(account);
            if (account.getClient() != this) {
                account.setClient(this);
            }
        }
    }

    public void addLoan(Loan loan) {
        if (this.loans == null) {
            this.loans = new HashSet<>();
        }
        if (loan != null && !this.loans.contains(loan)) {
            this.loans.add(loan);
            if (loan.getClient() != this) {
                loan.setClient(this);
            }
        }
    }

    @Override
    public String toString() {
        return "Client: " +
                "\nPerson Type=" + personType +
                "\nRegistration Date=" + registrationDate +
                "\nActive =" + active +
                "\nAccounts=" + accounts.size() +
                "\nLoans=" + loans.size();
    }

    public void deactivate() {
        this.active = false;
    }
}