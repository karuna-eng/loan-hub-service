package com.loan.hub.domain.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents approved loan offer details.
 */
@Setter
@Getter
public class Offer {
 private BigDecimal interestRate;
    private int tenureMonths;
    private BigDecimal emi;
    private BigDecimal totalPayable;

    // getters & setters
}
