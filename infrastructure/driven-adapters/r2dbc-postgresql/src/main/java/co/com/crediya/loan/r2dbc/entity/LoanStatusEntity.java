package co.com.crediya.loan.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("loan_statuses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanStatusEntity {

    @Id
    @Column("loan_status_id")
    private String id;

    private String name;

    private String description;

}
