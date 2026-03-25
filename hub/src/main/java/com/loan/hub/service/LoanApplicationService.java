package com.loan.hub.service;

import org.springframework.stereotype.Service;

import com.loan.hub.domain.dto.LoanApplicationRequest;
import com.loan.hub.domain.dto.LoanApplicationResponse;
import com.loan.hub.domain.enums.RiskBand;
import com.loan.hub.domain.model.Applicant;
import com.loan.hub.domain.model.Loan;
import com.loan.hub.util.EmiCalculator;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.loan.hub.domain.dto.Offer;

/**
 * Service layer for loan processing 
 */
@Slf4j
@Service
public class LoanApplicationService {

    public LoanApplicationResponse process(LoanApplicationRequest request) {
    
    Applicant applicant = request.getApplicant();
    Loan loan = request.getLoan();

    log.debug("Processing loan for applicant with credit score: {}", applicant.getCreditScore());

    List<String> rejectionReasons = new ArrayList<>();

    // Rule 1: credit score < 600
    if (applicant.getCreditScore() < 600) {
        log.debug("Rejected due to low credit score: {}", applicant.getCreditScore());
        rejectionReasons.add("LOW_CREDIT_SCORE");
    }

    // Rule 2: age + tenure > 65
    int tenureYears = loan.getTenureMonths() / 12;
    if (applicant.getAge() + tenureYears > 65) {
        log.debug("Rejected due to age and tenure limit: age {} + tenure {} years",
                applicant.getAge(), tenureYears);
        rejectionReasons.add("AGE_TENURE_LIMIT_EXCEEDED");
    }

    // Risk band
    RiskBand riskBand = getRiskBand(applicant.getCreditScore());

    // Interest rate
    BigDecimal interestRate = calculateInterest(applicant, loan, riskBand);

    // EMI
    BigDecimal emi = EmiCalculator.calculateEmi(
            loan.getAmount(),
            interestRate,
            loan.getTenureMonths()
    );

    // Rule 3: EMI > 60% income
    BigDecimal sixtyPercentIncome = applicant.getMonthlyIncome()
            .multiply(BigDecimal.valueOf(0.6));

    if (emi.compareTo(sixtyPercentIncome) > 0) {
        log.debug("Rejected due to EMI > 60% income");
        rejectionReasons.add("EMI_EXCEEDS_60_PERCENT");
    }

    // Rule 4: EMI > 50% income (offer validity rule)
    BigDecimal fiftyPercentIncome = applicant.getMonthlyIncome()
            .multiply(BigDecimal.valueOf(0.5));

    if (emi.compareTo(fiftyPercentIncome) > 0) {
        log.debug("Rejected due to EMI > 50% income");
        rejectionReasons.add("EMI_EXCEEDS_50_PERCENT");
    }

    // Final decision
    if (!rejectionReasons.isEmpty()) {
         log.info("Loan application rejected. Reasons: {}", rejectionReasons);
        return buildRejectedResponse(rejectionReasons);
    }
    log.info("Loan application approved with EMI: {}", emi);
    return buildApprovedResponse(riskBand, interestRate, emi, loan);
    }

    /**
     * Simple risk banding based on credit score.
     */
    public RiskBand getRiskBand(int creditScore) {

        if (creditScore >= 750) return RiskBand.LOW;
        if (creditScore >= 650) return RiskBand.MEDIUM;
        return RiskBand.HIGH;
    }
    private BigDecimal calculateInterest(Applicant applicant, Loan loan, RiskBand riskBand) {

        BigDecimal rate = BigDecimal.valueOf(12); // base rate

        // Risk premium
        if (riskBand == RiskBand.MEDIUM) rate = rate.add(BigDecimal.valueOf(1.5));
        if (riskBand == RiskBand.HIGH) rate = rate.add(BigDecimal.valueOf(3));

        // Employment premium
        if (applicant.getEmploymentType().name().equals("SELF_EMPLOYED")) {
            rate = rate.add(BigDecimal.valueOf(1));
        }

        // Loan size premium
        if (loan.getAmount().compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
            rate = rate.add(BigDecimal.valueOf(0.5));
        }

        return rate;
    }
    private LoanApplicationResponse buildRejectedResponse(List<String> reasons) {

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setApplicationId(UUID.randomUUID());
        response.setStatus("REJECTED");
        response.setRiskBand(null);
        response.setRejectionReasons(reasons);

        return response;
    }
    private LoanApplicationResponse buildApprovedResponse(RiskBand riskBand,
                                                       BigDecimal interestRate,
                                                       BigDecimal emi,
                                                       Loan loan) {

        Offer offer = new Offer();
        offer.setInterestRate(interestRate);
        offer.setTenureMonths(loan.getTenureMonths());
        offer.setEmi(emi);

        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(loan.getTenureMonths()));
        offer.setTotalPayable(totalPayable);

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setApplicationId(UUID.randomUUID());
        response.setStatus("APPROVED");
        response.setRiskBand(riskBand);
        response.setOffer(offer);

        return response;
    }
}
