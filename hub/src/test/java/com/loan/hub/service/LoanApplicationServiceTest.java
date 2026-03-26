package com.loan.hub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.loan.hub.domain.dto.LoanApplicationRequest;
import com.loan.hub.domain.dto.LoanApplicationResponse;
import com.loan.hub.domain.model.Applicant;
import com.loan.hub.domain.model.Loan;
import com.loan.hub.repository.LoanApplicationRepository;
import com.loan.hub.domain.enums.*;
public class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    private LoanApplicationService service;

    private static final int LOW_RISK_SCORE = 750;
    private static final int MEDIUM_RISK_SCORE = 700;
    private static final int HIGH_RISK_SCORE = 620;

    private static final BigDecimal DEFAULT_INCOME = BigDecimal.valueOf(75000);
    private static final BigDecimal DEFAULT_LOAN_AMOUNT = BigDecimal.valueOf(500000);
    private static final int DEFAULT_TENURE = 36;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new LoanApplicationService(repository);
    }

    

    @Test
    void shouldReturnLowRisk() {
    assertEquals(RiskBand.LOW, service.getRiskBand(LOW_RISK_SCORE));
    }

    @Test
    void shouldReturnMediumRisk() {
    assertEquals(RiskBand.MEDIUM, service.getRiskBand(MEDIUM_RISK_SCORE));
    }

    @Test
    void shouldReturnHighRisk() {
    assertEquals(RiskBand.HIGH, service.getRiskBand(HIGH_RISK_SCORE));
    }

    // Approval Test
    @Test
    void shouldApproveLoanApplication() {

        LoanApplicationRequest request = buildValidRequest();

        LoanApplicationResponse response = service.process(request);

        assertEquals(ApplicationStatus.APPROVED.name(), response.getStatus());
        assertNotNull(response.getOffer());
    }

    // Rejection Test
    @Test
    void shouldRejectForLowCreditScore() {

        LoanApplicationRequest request = buildRequestWithLowCredit();

        LoanApplicationResponse response = service.process(request);

        assertEquals(ApplicationStatus.REJECTED.name(), response.getStatus());
        assertTrue(response.getRejectionReasons().contains("LOW_CREDIT_SCORE"));
    }

    //Helper Methods

    private LoanApplicationRequest buildValidRequest() {

        Applicant applicant = new Applicant();
        applicant.setName("John");
        applicant.setAge(30);
        applicant.setMonthlyIncome(DEFAULT_INCOME);
        applicant.setEmploymentType(EmploymentType.SALARIED);
        applicant.setCreditScore(720);

        Loan loan = new Loan();
        loan.setAmount(DEFAULT_LOAN_AMOUNT);
        loan.setTenureMonths(DEFAULT_TENURE);
        loan.setPurpose(LoanPurpose.PERSONAL);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setApplicant(applicant);
        request.setLoan(loan);

        return request;
    }

    private LoanApplicationRequest buildRequestWithLowCredit() {

        LoanApplicationRequest request = buildValidRequest();
        request.getApplicant().setCreditScore(500);

        return request;
    }
}
