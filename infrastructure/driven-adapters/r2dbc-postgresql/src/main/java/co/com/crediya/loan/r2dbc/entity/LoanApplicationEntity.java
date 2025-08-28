package co.com.crediya.loan.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("loan_applications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationEntity {

    @Id
    @Column("id")
    private String id;

    private BigDecimal amount;

    private int term;

    private String email;

    private String status;

    private String type;

    private String documentId;

}
