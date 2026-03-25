# DEVELOPMENT NOTES

## 1. Approach

The application was designed as a layered Spring Boot REST service to evaluate loan applications. The system processes incoming requests, validates input data, applies business rules, and generates either an approved loan offer or rejection reasons.

The implementation was done incrementally:

* Setup project structure and logging
* Define domain models and validation rules
* Build REST controller and exception handling
* Implement core business logic (EMI, risk, eligibility)
* Test with multiple scenarios

---

## 2. Design Decisions

### Layered Architecture

The application follows a clear separation of concerns:

* Controller → Handles HTTP requests
* Service → Contains business logic
* Domain → DTOs, models, enums
* Util → Reusable logic (EMI calculation)

### Use of BigDecimal

Financial calculations (EMI, interest) use BigDecimal to ensure precision and avoid floating-point errors.

### Validation Strategy

Used Jakarta Bean Validation annotations to enforce input constraints and a global exception handler to return consistent error responses.

### Logging

Used structured logging to trace request processing and outcomes.

---

## 3. Key Business Logic

### EMI Calculation

Implemented using the standard EMI formula with proper rounding (scale = 2, HALF_UP).

### Risk Classification

Based on credit score:

* LOW (750+)
* MEDIUM (650–749)
* HIGH (600–649)

### Interest Rate Calculation

Final interest rate includes:

* Base rate (12%)
* Risk premium
* Employment premium
* Loan size premium

### Eligibility Rules

* Reject if credit score < 600
* Reject if age + tenure exceeds 65
* Reject if EMI > 60% of income
* Reject if EMI > 50% of income (offer constraint)

---

## 4. Assumptions

* Each applicant has a single employment type
* Loan purpose is a single value
* Interest rate is fixed per application
* No persistence layer required (in-memory processing)

---

## 5. Trade-offs

* Did not introduce database persistence to keep the solution simple and focused
* Combined some logic within service layer for readability

---

## 6. Improvements (Given More Time)

* Add persistence layer for audit storage
* Introduce integration tests
* Add API documentation (Swagger/OpenAPI)
* Improve validation error structure (field-wise response)


---

## 7. Git Workflow

* Used feature branches for incremental development
* Maintained clean and meaningful commit history
* Followed logical commit separation (models, controller, service, logic)

---
