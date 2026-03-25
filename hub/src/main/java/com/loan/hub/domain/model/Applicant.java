package com.loan.hub.domain.model;

import java.math.BigDecimal;

import com.loan.hub.domain.enums.EmploymentType;
import jakarta.validation.constraints.*;;
/**
 * Represents applicant details for loan processing.
 */
public class Applicant {
    
    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 21, message = "Age must be at least 21")
    @Max(value = 60, message = "Age must not exceed 60")
    private int age;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Income must be greater than 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @Min(value = 300, message = "Credit score must be >= 300")
    @Max(value = 900, message = "Credit score must be <= 900")
    private int creditScore;

}
