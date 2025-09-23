package ar.edu.utn.frbb.tup.model.payment;

import ar.edu.utn.frbb.tup.model.loan.Loan;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "planes_pago")
@Entity(name = "Plan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PaymentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer installments; //cantidad de cuotas
    private Double interestRate; //taza de interes
    private Boolean fixedAmount; //cuotas fijas o variables

    @OneToMany(mappedBy = "paymentPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
}