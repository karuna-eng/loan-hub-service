package com.loan.hub.repository.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationEntity {
    @Id
    private UUID applicationId;

    private String status;

    private String riskBand;

    private BigDecimal emi;

    private BigDecimal intrestRate;

    private String rejectionReasons;

    private  LocalDateTime createdAt;

    private LocalDateTime updatedAt;
   
}
