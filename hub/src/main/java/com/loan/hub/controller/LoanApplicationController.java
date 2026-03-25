package com.loan.hub.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.loan.hub.service.LoanApplicationService;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.loan.hub.domain.dto.LoanApplicationRequest;
import com.loan.hub.domain.dto.LoanApplicationResponse;

/**
 * REST controller for handling loan application requests.
 */
@Slf4j
@RestController
@RequestMapping("/hub-api")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

     /**
     * Accepts loan application and returns approval or rejection.
     */
    @PostMapping("/applications")
    public ResponseEntity<LoanApplicationResponse> applyApplication(
        @Valid @RequestBody LoanApplicationRequest request) {
           log.info("Received loan application for applicant: {}",
                request.getApplicant().getName());

        LoanApplicationResponse response = loanApplicationService.process(request);

        log.info("Application processed with status: {}",
                response.getStatus());
        
        return ResponseEntity.ok(response);
    }
    

}
