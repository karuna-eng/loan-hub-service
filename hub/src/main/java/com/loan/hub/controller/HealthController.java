package com.loan.hub.controller;

import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;


/**
 * Controller for health check endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/hub-api")
public class HealthController {

   
    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("Health check called");
        return Map.of(
            "status", "healthy",
            "service", "loan-hub",
            "time", LocalDateTime.now()
        );
    }
    
}
