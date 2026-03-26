package com.loan.hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.loan.hub.repository.entity.LoanApplicationEntity;
import java.util.UUID;

/**
 * Repository interface for managing loan application entities in the database.
 */
public interface LoanApplicationRepository  extends JpaRepository<LoanApplicationEntity, UUID> {

}
