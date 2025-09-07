package co.com.crediya.loan.api.mapper;

import co.com.crediya.loan.api.dto.LoanApplicationRequestDto;
import co.com.crediya.loan.model.loanapplication.LoanApplication;

public final class LoanApplicationMapper {

    private LoanApplicationMapper() {}

    public static LoanApplication toDomain(LoanApplicationRequestDto loanApplicationRequestDto) {
        return LoanApplication.builder()
                .amount(loanApplicationRequestDto.amount())
                .term(loanApplicationRequestDto.term())
                .email(loanApplicationRequestDto.email())
                .type(loanApplicationRequestDto.type())
                .documentId(loanApplicationRequestDto.documentId())
                .build();
    }

}
