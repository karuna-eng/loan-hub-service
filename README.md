# Loan Hub Service

Build a Spring Boot REST service that evaluates loan applications and
determines whether a single loan offer (based on requested tenure) can be
approved.

# Business Context
Build a backend service for a digital lending platform.
• Receives loan applications
• Evaluates eligibility using business rules
• Generates a single offer based on requested tenure
• Stores decisions for audit

## Features

Features
- Health check endpoint
- Loan application processing
- EMI calculation using BigDecimal
- Risk-based interest rate calculation
- Eligibility validation rules
- Structured rejection reasons
- REST API with proper validation and error handling
- Unit tests for core logic

## Tech Stack
- Java 17
- Spring Boot 3.5
- Maven 3.9
