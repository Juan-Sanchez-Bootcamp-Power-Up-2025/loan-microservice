package co.com.crediya.authentication.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loantype.LoanType;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.model.user.User;
import co.com.crediya.loan.model.user.gateways.UserRepository;
import co.com.crediya.loan.usecase.loanapplication.LoanApplicationUseCase;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private UserRepository userRepository;

    private LoanApplicationUseCase loanApplicationUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        loanApplicationUseCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, userRepository);
    }

    private LoanType sampleLoanType() {
        return LoanType.builder()
                .name("LOW")
                .minAmount(BigDecimal.valueOf(1))
                .maxAmount(BigDecimal.valueOf(12345))
                .interestRate(BigDecimal.valueOf(9))
                .build();
    }

    private LoanApplication sampleLoanApplication() {
        return LoanApplication.builder()
                .email("name@email.com")
                .documentId("12354678")
                .baseSalary(BigDecimal.valueOf(1234))
                .status("PENDING")
                .type("LOW")
                .amount(BigDecimal.valueOf(876))
                .term(BigDecimal.valueOf(9))
                .monthlyDebt(BigDecimal.valueOf(878))
                .build();
    }

    private User sampleUser() {
        return User.builder()
                .name("Name")
                .email("name@email.com")
                .documentId("12345678")
                .baseSalary(BigDecimal.valueOf(1234))
                .build();
    }

    @Test
    void shouldFailSavingLoanWhenTypeNotExists() {
        LoanApplication loanApplication = sampleLoanApplication();
        when(loanTypeRepository.findByTypeId(loanApplication.getType())).thenReturn(Mono.empty());
        when(userRepository.validateUserByDocumentId(loanApplication.getEmail(),
                loanApplication.getDocumentId()))
                .thenReturn(Mono.just(sampleUser()));

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectErrorMatches(ex ->
                        ex instanceof LoanTypeNotFoundException && ex.getMessage()
                                .contains("not found in data base. Please check the type."))
                .verify();

        verify(loanTypeRepository, times(1)).findByTypeId(loanApplication.getType());
        verify(userRepository, times(1)).validateUserByDocumentId(loanApplication.getEmail(),
                loanApplication.getDocumentId());
        verify(loanApplicationRepository, never()).saveLoanApplication(any());
    }

    @Test
    void shouldFailSavingLoanWhenUserWithEmailAndDocumentIdNotExists() {
        LoanApplication loanApplication = sampleLoanApplication();
        when(loanTypeRepository.findByTypeId(loanApplication.getType())).thenReturn(Mono.just(sampleLoanType()));
        when(userRepository.validateUserByDocumentId(loanApplication.getEmail(), loanApplication.getDocumentId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectErrorMatches(ex ->
                        ex instanceof UserNotFoundException && ex.getMessage()
                                .contains(" was not found."))
                .verify();

        verify(userRepository, times(1)).validateUserByDocumentId(loanApplication.getEmail(),
                loanApplication.getDocumentId());
        verify(loanApplicationRepository, never()).saveLoanApplication(any());
    }

    @Test
    void shouldSaveLoan() {
        LoanApplication loanApplication = sampleLoanApplication();
        LoanApplication loanApplicationSaved = loanApplication.toBuilder().build();

        when(loanTypeRepository.findByTypeId(loanApplication.getType())).thenReturn(Mono.just(sampleLoanType()));
        when(userRepository.validateUserByDocumentId(loanApplication.getEmail(), loanApplication.getDocumentId()))
                .thenReturn(Mono.just(sampleUser()));
        when(loanApplicationRepository.saveLoanApplication(loanApplication)).thenReturn(Mono.just(loanApplicationSaved));

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectNextMatches(out ->
                        loanApplicationSaved.getStatus().equals(out.getStatus()) &&
                                loanApplicationSaved.getMonthlyDebt().equals(out.getMonthlyDebt()))
                .expectComplete()
                .verify();

        verify(loanTypeRepository, times(1)).findByTypeId(loanApplication.getType());
        verify(userRepository, times(1)).validateUserByDocumentId(loanApplication.getEmail(),
                loanApplication.getDocumentId());
    }

    @Test
    void shouldListLoanApplications() {
        LoanApplication la1 = sampleLoanApplication();
        LoanApplication la2 = sampleLoanApplication();
        LoanApplication la3 = sampleLoanApplication();
        LoanApplication la4 = sampleLoanApplication();
        LoanApplication la5 = sampleLoanApplication();

        when(loanApplicationRepository.getLoanApplicationsWhereStatusNotApproved())
                .thenReturn((Flux.just(la1, la2, la3, la4, la5)));
        when(loanTypeRepository.findByTypeId(la1.getType())).thenReturn(Mono.just(sampleLoanType()));

        StepVerifier.create(loanApplicationUseCase.listLoanApplicationsForConsultant())
                .expectNextCount(5)
                .expectComplete()
                .verify();
        verify(loanApplicationRepository, times(1)).getLoanApplicationsWhereStatusNotApproved();
        verify(loanTypeRepository, times(5)).findByTypeId(la1.getType());
    }

    @Test
    void shouldListLoanApplicationsPaginate() {
        LoanApplication la1 = sampleLoanApplication();
        LoanApplication la2 = sampleLoanApplication();
        LoanApplication la3 = sampleLoanApplication();
        LoanApplication la4 = sampleLoanApplication();
        LoanApplication la5 = sampleLoanApplication();

        when(loanApplicationRepository.getLoanApplicationsWhereStatusNotApproved())
                .thenReturn((Flux.just(la1, la2, la3, la4, la5)));
        when(loanTypeRepository.findByTypeId(la1.getType())).thenReturn(Mono.just(sampleLoanType()));

        StepVerifier.create(loanApplicationUseCase.listLoanApplicationsForConsultantPaginate(1,3))
                .expectNextCount(1)
                .expectComplete()
                .verify();
        verify(loanApplicationRepository, times(1))
                .getLoanApplicationsWhereStatusNotApproved();
        verify(loanTypeRepository, times(5)).findByTypeId(la1.getType());
    }

}
