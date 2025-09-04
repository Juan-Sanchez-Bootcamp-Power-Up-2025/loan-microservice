package co.com.crediya.loan.model.loanapplication.pagination;

import co.com.crediya.loan.model.loanapplication.LoanApplicationReview;

import java.util.List;

public record PageResult(
        List<LoanApplicationReview> loanApplicationReviews,
        int total,
        int page,
        int size
) {}
