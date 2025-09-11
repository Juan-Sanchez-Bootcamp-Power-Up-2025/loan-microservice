package co.com.crediya.loan.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("loan_applications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationEntity {

    @Id
    @Column("id")
    private UUID id;

    private String clientName;

    private String email;

    private String documentId;

    private BigDecimal baseSalary;

    private String status;

    private String type;

    private BigDecimal amount;

    private BigDecimal term;

    private BigDecimal monthlyDebt;

}
