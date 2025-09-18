package ar.edu.utn.frbb.tup.model.cuenta;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "cuentas")
@Entity(name = "Cuenta")
@Getter
@Setter
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
    private LocalDate fechaCreacion;

    private Double balance;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    @Enumerated(EnumType.STRING)
    private TipoMoneda tipoMoneda;

    @Column(name = "estado")
    private boolean active;

    public Account(AccountDto dto) {
        this.id = null;
        this.active = true;
        this.fechaCreacion = LocalDate.now();
        this.balance = dto.balance();
        this.tipoCuenta = dto.tipoCuenta();
        this.tipoMoneda = dto.tipoMoneda();
    }

    @Override
    public String toString() {
        return "Cuenta: " +
                "\nId =" + id +
                "\nfechaCreacion=" + fechaCreacion +
                "\nbalance=" + balance +
                "\ntipoCuenta=" + tipoCuenta +
                "\ntipoMoneda=" + tipoMoneda +
                "\nestado=" + active;
    }

    public void deactivate() {
        this.active = false;
    }
}