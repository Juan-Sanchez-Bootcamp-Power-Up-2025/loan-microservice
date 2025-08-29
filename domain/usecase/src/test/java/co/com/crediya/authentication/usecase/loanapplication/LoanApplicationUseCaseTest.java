package co.com.crediya.authentication.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.IdentityVerificationGateway;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.usecase.loanapplication.LoanApplicationUseCase;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
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
    private IdentityVerificationGateway identityVerificationGateway;

    private LoanApplicationUseCase loanApplicationUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        loanApplicationUseCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, identityVerificationGateway);
    }

    private LoanApplication sampleLoanApplication() {
        return LoanApplication.builder()
                .amount(new BigDecimal("1200"))
                .term(5)
                .email("name@email.com")
                .status("PENDING")
                .type("LOW")
                .documentId("12354678")
                .build();
    }

    @Test
    void shouldFailWhenTypeNotExists() {
        LoanApplication loanApplication = sampleLoanApplication();
        when(loanTypeRepository.existsById(loanApplication.getType())).thenReturn(Mono.just(false));
        when(identityVerificationGateway.validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId())).thenReturn(Mono.just(true));

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectErrorMatches(ex ->
                        ex instanceof LoanTypeNotFoundException && ex.getMessage()
                                .contains("not found in data base. Please check the type."))
                .verify();

        verify(loanTypeRepository, times(1)).existsById(loanApplication.getType());
        verify(identityVerificationGateway, times(1)).validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId());
        verify(loanApplicationRepository, never()).saveLoanApplication(any());
    }

    @Test
    void shouldFailWhenUserEmailAndDocumentIdNotExists() {
        LoanApplication loanApplication = sampleLoanApplication();
        when(loanTypeRepository.existsById(loanApplication.getType())).thenReturn(Mono.just(true));
        when(identityVerificationGateway.validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId())).thenReturn(Mono.just(false));

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectErrorMatches(ex ->
                        ex instanceof UserNotFoundException && ex.getMessage()
                                .contains(" was not found."))
                .verify();

        verify(identityVerificationGateway, times(1)).validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId());
        verify(loanApplicationRepository, never()).saveLoanApplication(any());
    }

    @Test
    void shouldSaveLoan() {
        LoanApplication loanApplication = sampleLoanApplication();
        LoanApplication loanApplicationSaved = loanApplication.toBuilder().build();

        when(loanTypeRepository.existsById(loanApplication.getType())).thenReturn(Mono.just(true));
        when(identityVerificationGateway.validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId())).thenReturn(Mono.just(true));
        when(loanApplicationRepository.saveLoanApplication(loanApplication)).thenReturn(Mono.just(loanApplicationSaved));

        StepVerifier.create(loanApplicationUseCase.saveLoanApplication(loanApplication))
                .expectNextMatches(out ->
                        loanApplicationSaved.getType().equals(out.getType()) &&
                                loanApplicationSaved.getEmail().equals(out.getEmail()))
                .expectComplete()
                .verify();

        verify(loanTypeRepository, times(1)).existsById(loanApplication.getType());
        verify(identityVerificationGateway, times(1)).validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId());
    }

}
