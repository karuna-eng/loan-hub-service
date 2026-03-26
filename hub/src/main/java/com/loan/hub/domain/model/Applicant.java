package com.loan.hub.domain.model;

import java.math.BigDecimal;

import com.loan.hub.domain.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;;
/**
 * Represents applicant details for loan processing.
 */
@Setter
@Getter
public class Applicant {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 21, message = "Age must be between 21–60")
    @Max(value = 60, message = "Age must be between 21–60")
    private Integer age;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Credit score is required")
    @Min(value = 300, message = "Credit score must be between 300–900")
    @Max(value = 900, message = "Credit score must be between 300–900")
    private Integer creditScore;

}
