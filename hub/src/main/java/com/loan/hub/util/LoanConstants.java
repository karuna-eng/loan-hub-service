package com.loan.hub.util;

import java.math.BigDecimal;

public class LoanConstants {
    private LoanConstants() {
        // Private constructor to prevent instantiation
    }

    public static final BigDecimal BASE_INTEREST_RATE = BigDecimal.valueOf(12);
    public static final BigDecimal EMI_50_PERCENT = BigDecimal.valueOf(0.5);
    public static final BigDecimal EMI_60_PERCENT = BigDecimal.valueOf(0.6);

    public static final BigDecimal SELF_EMPLOYED_PREMIUM = BigDecimal.valueOf(1);
    public static final BigDecimal MEDIUM_RISK_PREMIUM = BigDecimal.valueOf(1.5);
    public static final BigDecimal HIGH_RISK_PREMIUM = BigDecimal.valueOf(3);
    public static final BigDecimal LOAN_SIZE_PREMIUM = BigDecimal.valueOf(0.5);

    public static final BigDecimal LOAN_THRESHOLD = BigDecimal.valueOf(1_000_000);

}
