package co.com.crediya.loan.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("loan_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanTypeEntity {

    @Id
    @Column("loan_type_id")
    private String id;

    private String name;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal interestRate;

    private Boolean validation;

}
