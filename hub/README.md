# Loan Hub Service

Spring Boot microservice for evaluating loan applications.

## Features
- Health check endpoint
- REST API structure
- Unit testing

Loan Hub Service

A Spring Boot REST API that evaluates loan applications and determines whether a loan offer can be approved based on business rules.

Features
    Loan application processing
    EMI calculation using BigDecimal
    Risk-based interest rate calculation
    Eligibility validation rules
    Structured rejection reasons
    REST API with proper validation and error handling
    Unit tests for core logic
Tech Stack
    Java 17+
    Spring Boot
    Maven
    Lombok
    JUnit 5
API Endpoint
    Create Loan Application

POST /hub-api/applications

Sample Request
{
  "applicant": {
    "name": "John",
    "age": 30,
    "monthlyIncome": 75000,
    "employmentType": "SALARIED",
    "creditScore": 720
  },
  "loan": {
    "amount": 500000,
    "tenureMonths": 36,
    "purpose": "PERSONAL"
  }
}

Sample Response (Approved)
{
  "applicationId": "UUID",
  "status": "APPROVED",
  "riskBand": "MEDIUM",
  "offer": {
    "interestRate": 13.5,
    "tenureMonths": 36,
    "emi": 16967.64,
    "totalPayable": 610835.04
  }
}

Sample Response (Rejected)
{
  "applicationId": "UUID",
  "status": "REJECTED",
  "riskBand": null,
  "rejectionReasons": [
    "LOW_CREDIT_SCORE"
  ]
}

Business Rules
    Validation Rules
    Age: 21–60
    Credit score: 300–900
    Loan amount: 10,000 – 50,00,000
    Tenure: 6–360 months
    Monthly income > 0
    Eligibility Rules
    Reject if credit score < 600
    Reject if age + tenure > 65
    Reject if EMI > 60% of monthly income
    Offer Rule
Reject if EMI > 50% of monthly income
    Risk Band Classification
Credit Score	Risk Band
    750+	LOW
    650–749	MEDIUM
    600–649	HIGH

Interest Calculation

Final Interest Rate =
Base Rate (12%) + Risk Premium + Employment Premium + Loan Size Premium

Sample Test Scenarios
    Approved Case
Income: 75,000
Loan: 500,000
Tenure: 36 months
    EMI within limits → APPROVED
    Low Credit Score
Credit Score: 500
    REJECTED (LOW_CREDIT_SCORE)
    Age + Tenure Exceeded
Age: 60, Tenure: 10 years
    REJECTED (AGE_TENURE_LIMIT_EXCEEDED)
    EMI > 60%
Low income, high loan
    REJECTED (EMI_EXCEEDS_60_PERCENT)
    EMI between 50–60%
Medium loan vs income
    REJECTED (EMI_EXCEEDS_50_PERCENT)
    Running Tests
mvn test
    Running the Application
    mvn spring-boot:run
Design Highlights
    Layered architecture (Controller, Service, Domain, Util)
    Clean separation of concerns
    BigDecimal for financial accuracy
    Minimal and structured logging
    Edge case handling (e.g., zero interest EMI)
Future Improvements
    Add database persistence for audit
    API documentation using Swagger/OpenAPI
    Integration tests
    Configurable business rules
