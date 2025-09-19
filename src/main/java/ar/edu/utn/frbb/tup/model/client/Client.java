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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo")
    private boolean active;

    @Embedded
    private Person person;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "tipo_persona")
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @Column(name = "fecha_alta")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Loan> loans = new HashSet<>();

    public Client(ClientDto dto) {
        this.id = null;
        this.active = true;
        this.personType = dto.personType();
        this.registrationDate = LocalDate.now();
        this.person = new Person(dto.person());
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