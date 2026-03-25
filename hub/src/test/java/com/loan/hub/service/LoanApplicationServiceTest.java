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
import com.loan.hub.domain.enums.EmploymentType;
import com.loan.hub.domain.enums.LoanPurpose;
import com.loan.hub.domain.enums.RiskBand;
import com.loan.hub.domain.model.Applicant;
import com.loan.hub.domain.model.Loan;
import com.loan.hub.repository.LoanApplicationRepository;

public class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    private LoanApplicationService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new LoanApplicationService(repository);
    }

    

    @Test
    void shouldReturnLowRisk() {
    assertEquals(RiskBand.LOW, service.getRiskBand(750));
    }

    @Test
    void shouldReturnMediumRisk() {
    assertEquals(RiskBand.MEDIUM, service.getRiskBand(700));
    }

    @Test
    void shouldReturnHighRisk() {
    assertEquals(RiskBand.HIGH, service.getRiskBand(620));
    }

    // Approval Test
    @Test
    void shouldApproveLoanApplication() {

        LoanApplicationRequest request = buildValidRequest();

        LoanApplicationResponse response = service.process(request);

        assertEquals("APPROVED", response.getStatus());
        assertNotNull(response.getOffer());
    }

    // Rejection Test
    @Test
    void shouldRejectForLowCreditScore() {

        LoanApplicationRequest request = buildRequestWithLowCredit();

        LoanApplicationResponse response = service.process(request);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getRejectionReasons().contains("LOW_CREDIT_SCORE"));
    }

    //Helper Methods

    private LoanApplicationRequest buildValidRequest() {

        Applicant applicant = new Applicant();
        applicant.setName("John");
        applicant.setAge(30);
        applicant.setMonthlyIncome(BigDecimal.valueOf(75000));
        applicant.setEmploymentType(EmploymentType.SALARIED);
        applicant.setCreditScore(720);

        Loan loan = new Loan();
        loan.setAmount(BigDecimal.valueOf(500000));
        loan.setTenureMonths(36);
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
