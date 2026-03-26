package com.loan.hub.domain.model;

import java.math.BigDecimal;

import com.loan.hub.domain.enums.LoanPurpose;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents loan details requested by applicant.
 */
@Setter
@Getter
public class Loan {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "10000", message = "Loan amount must be between 10,000-50,000,000")
    @DecimalMax(value = "5000000", message = "Loan amount must be between 10,000-50,000,000")
    private BigDecimal amount;

    @Min(value = 6, message = "Tenure must be between 6–360 months")
    @Max(value = 360, message = "Tenure must be between 6–360 months")
    private int tenureMonths;

    @NotNull(message = "Loan purpose is required")
    private LoanPurpose purpose;

        // getters & setters
}
