package com.loan.hub.domain.dto;

import com.loan.hub.domain.model.Applicant;
import com.loan.hub.domain.model.Loan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Root request object for loan application API.
 */
@Setter
@Getter
public class LoanApplicationRequest {
 @Valid
    @NotNull(message = "Applicant details are required")
    private Applicant applicant;

    @Valid
    @NotNull(message = "Loan details are required")
    private Loan loan;

    // getters & setters
}
