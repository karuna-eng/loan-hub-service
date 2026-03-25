package com.loan.hub.util;


import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorTest {

    @Test
    void shouldCalculateEmiCorrectly() {

        BigDecimal principal = BigDecimal.valueOf(500000);
        BigDecimal rate = BigDecimal.valueOf(12);
        int tenure = 36;

        BigDecimal emi = EmiCalculator.calculateEmi(principal, rate, tenure);

        assertNotNull(emi);
        assertTrue(emi.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void shouldHandleZeroInterest() {

        BigDecimal principal = BigDecimal.valueOf(120000);
        BigDecimal rate = BigDecimal.ZERO;
        int tenure = 12;

        BigDecimal emi = EmiCalculator.calculateEmi(principal, rate, tenure);

        assertEquals(BigDecimal.valueOf(10000.00).setScale(2), emi);
    }

}
