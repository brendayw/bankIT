package ar.edu.utn.frbb.tup.model.payment;

import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "cuotas")
@Entity(name = "Cuota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number")
    private Integer paymentNumber;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    private Boolean paid = false;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
}
