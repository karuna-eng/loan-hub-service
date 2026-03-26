package com.loan.hub.service;

import org.springframework.stereotype.Service;

import com.loan.hub.domain.dto.LoanApplicationRequest;
import com.loan.hub.domain.dto.LoanApplicationResponse;

import com.loan.hub.domain.model.Applicant;
import com.loan.hub.domain.model.Loan;
import com.loan.hub.repository.LoanApplicationRepository;
import com.loan.hub.repository.entity.LoanApplicationEntity;
import com.loan.hub.util.EmiCalculator;
import com.loan.hub.domain.enums.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.loan.hub.domain.dto.Offer;
import com.loan.hub.util.LoanConstants;
/**
 * Service layer for loan processing 
 */
@Slf4j
@Service
public class LoanApplicationService {

    private final LoanApplicationRepository repository;

    public LoanApplicationService(LoanApplicationRepository repository) {
        this.repository = repository;
    }

    /**
     * Processes a loan application request and returns a response with approval status, risk band, and offer details.
     * @param request the loan application request containing applicant and loan details
     * @return the loan application response with approval status, risk band, and offer details
     */
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
            loan.getAmount(), interestRate, loan.getTenureMonths()
        );

        // Rule 3 & 4: EMI thresholds (priority logic)
        checkEmiThreshold(emi, applicant.getMonthlyIncome(), rejectionReasons);
        

        // Final decision
        if (!rejectionReasons.isEmpty()) {
            log.info("Loan application rejected. Reasons: {}", rejectionReasons);
            return buildRejectedResponse(rejectionReasons);
        }
        log.info("Loan application approved with EMI: {}", emi);
        return buildApprovedResponse(riskBand, interestRate, emi, loan);
    }

    
    /**
     * Checks if the calculated EMI exceeds the thresholds based on the applicant's monthly income.
     * @param emi the calculated EMI
     * @param monthlyIncome the applicant's monthly income
     * @param rejectionReasons the list of rejection reasons
     */
    private void checkEmiThreshold(BigDecimal emi,
                              BigDecimal monthlyIncome,
                              List<String> rejectionReasons) {

        BigDecimal sixtyPercentIncome = calculateIncomeThreshold(monthlyIncome, LoanConstants.EMI_60_PERCENT);
        BigDecimal fiftyPercentIncome = calculateIncomeThreshold(monthlyIncome, LoanConstants.EMI_50_PERCENT);

        if (emi.compareTo(sixtyPercentIncome) > 0) {
            rejectionReasons.add("EMI_EXCEEDS_60_PERCENT");
        } else if (emi.compareTo(fiftyPercentIncome) > 0) {
            rejectionReasons.add("EMI_EXCEEDS_50_PERCENT");
        }
    }
    /**
     * Calculates the income threshold based on a percentage of the applicant's income.
     * @param income
     * @param percentage
     * @return
     */
    private BigDecimal calculateIncomeThreshold(BigDecimal income, BigDecimal percentage) {
        return income.multiply(percentage);
    }
    /**
     * Determines the risk band based on credit score.
     * @param creditScore the applicant's credit score
     * @return the risk band
     */
     
    public RiskBand getRiskBand(int creditScore) {

        if (creditScore >= 750) return RiskBand.LOW;
        if (creditScore >= 650) return RiskBand.MEDIUM;
        return RiskBand.HIGH;
    }
    /**
     * Calculates interest rate based on risk band and other factors.
     * @param applicant
     * @param loan
     * @param riskBand
     * @return calculated interest rate as BigDecimal
     */
    private BigDecimal calculateInterest(Applicant applicant, Loan loan, RiskBand riskBand) {

        BigDecimal rate = LoanConstants.BASE_INTEREST_RATE; // base rate

        // Risk premium
        if (riskBand == RiskBand.MEDIUM) rate = rate.add(LoanConstants.MEDIUM_RISK_PREMIUM);
        if (riskBand == RiskBand.HIGH) rate = rate.add(LoanConstants.HIGH_RISK_PREMIUM);

        // Employment premium
        if (applicant.getEmploymentType() == EmploymentType.SELF_EMPLOYED) {
            rate = rate.add(LoanConstants.SELF_EMPLOYED_PREMIUM);
        }

        // Loan size premium
        if (loan.getAmount().compareTo(LoanConstants.LOAN_THRESHOLD) > 0) {
            rate = rate.add(LoanConstants.LOAN_SIZE_PREMIUM);
        }

        return rate;
    }
    /**
     * Builds a rejected loan application response.
     * @param reasons the reasons for rejection
     * @return the rejected response
     */
    private LoanApplicationResponse buildRejectedResponse(List<String> reasons) {

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setApplicationId(UUID.randomUUID());
        response.setStatus("REJECTED");
        response.setRiskBand(null);
        response.setRejectionReasons(reasons);

        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setApplicationId(response.getApplicationId());
        entity.setStatus(ApplicationStatus.REJECTED.name());
        entity.setRejectionReasons(String.join(",", reasons));
        entity.setCreatedAt(LocalDateTime.now());

        repository.save(entity);

        return response;
    }
    /**
     * Builds an approved loan application response.
     * @param riskBand the risk band
     * @param interestRate the interest rate
     * @param emi the EMI
     * @param loan the loan
     * @return the approved response
     */
    private LoanApplicationResponse buildApprovedResponse(RiskBand riskBand,
                                                    BigDecimal interestRate,
                                                    BigDecimal emi,
                                                    Loan loan) {

        Offer offer = buildOffer(interestRate, emi, loan);

        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(loan.getTenureMonths()));
        offer.setTotalPayable(totalPayable);

        LoanApplicationResponse response = buildResponse(riskBand, offer);
        LoanApplicationEntity entity = buildEntity(riskBand, interestRate, emi, response);
        repository.save(entity);
        return response;
    }

    /**
     * Gets the offer for the loan application.
     * @param interestRate the interest rate
     * @param emi the EMI
     * @param loan the loan
     * @return the offer
     */
    public Offer buildOffer(BigDecimal interestRate, BigDecimal emi, Loan loan) {
        Offer offer = new Offer();
        offer.setInterestRate(interestRate);
        offer.setTenureMonths(loan.getTenureMonths());
        offer.setEmi(emi);
        return offer;
    }

    /**
     * Gets the response for the loan application.
     * @param riskBand the risk band
     * @param offer the offer
     * @return the response
     */
    private LoanApplicationResponse buildResponse(RiskBand riskBand, Offer offer) {
        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setApplicationId(UUID.randomUUID());
        response.setStatus(ApplicationStatus.APPROVED.name());
        response.setRiskBand(riskBand);
        response.setOffer(offer);
        return response;
    }

    /*
 */
    private LoanApplicationEntity buildEntity(RiskBand riskBand, BigDecimal interestRate, BigDecimal emi,
            LoanApplicationResponse response) {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setApplicationId(response.getApplicationId());
        entity.setStatus(ApplicationStatus.APPROVED.name());
        entity.setRiskBand(riskBand.name());
        entity.setEmi(emi);
        entity.setInterestRate(interestRate);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
