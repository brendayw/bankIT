package ar.edu.utn.frbb.tup.model.cliente;

import ar.edu.utn.frbb.tup.model.cliente.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.persona.TipoPersona;
import ar.edu.utn.frbb.tup.model.persona.Person;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
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
    private Person persona;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "tipo_persona")
    @Enumerated(EnumType.STRING)
    private TipoPersona tipoPersona;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Account> cuentas = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Loan> prestamos = new HashSet<>();

    public Client(ClientDto dto) {
        this.id = null;
        this.active = true;
        this.tipoPersona = dto.tipoPersona();
        this.fechaAlta = LocalDate.now();
        this.persona = new Person(dto.persona());
    }

    @Override
    public String toString() {
        return "Cliente: " +
                "\ntipoPersona=" + tipoPersona +
                "\nfechaAlta=" + fechaAlta +
                "\nactivo=" + active +
                "\ncantidadCuentas=" + cuentas.size() +
                "\ncantidadPrestamos=" + prestamos.size();
    }

    public void deactivate() {
        this.active = false;
    }
}