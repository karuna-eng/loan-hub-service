package com.loan.hub.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Utility class to calculate EMI using standard formula.
 */
public class EmiCalculator {

    /**
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
    */
   
    public static BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int months) {
       
         // Handle zero interest case
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        } 
        
        //Convert manual rate to montly rate 
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        // (1+r)^n
        BigDecimal factor = (monthlyRate.add(BigDecimal.ONE)).pow(months);
        
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);    
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);

    }
}
