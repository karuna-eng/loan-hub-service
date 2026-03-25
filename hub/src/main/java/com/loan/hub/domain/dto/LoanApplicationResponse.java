package com.loan.hub.domain.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loan.hub.domain.enums.RiskBand;

import lombok.Getter;
import lombok.Setter;

/**
 * Response returned after loan evaluation.
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanApplicationResponse {
    private UUID applicationId;
    private String status;
    private RiskBand riskBand;
    private Offer offer;
    private List<String> rejectionReasons;

    // getters & setters
}
