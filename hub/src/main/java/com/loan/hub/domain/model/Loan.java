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
    @DecimalMin(value = "10000", message = "Minimum loan amount is 10000")
    @DecimalMax(value = "5000000", message = "Maximum loan amount is 5000000")
    private BigDecimal amount;

    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private int tenureMonths;

    @NotNull(message = "Loan purpose is required")
    private LoanPurpose purpose;

        // getters & setters
}
