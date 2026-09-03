# Enterprise Banking Transaction System

## 1. Overview

The **Enterprise Banking Transaction System** is a Spring Boot-based backend application for managing customers, bank accounts, authentication, and financial transactions.

### Main responsibilities

- Customer registration and login
- Customer code generation
- Password hashing using BCrypt
- Account creation
- Account type management
- JWT access-token authentication
- JWT refresh-token management
- Deposit and withdrawal workflows
- Customer and account data persistence using JPA/Hibernate
- MySQL database integration

---

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Web | Spring MVC |
| Security | Spring Security |
| Authentication | JWT |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Database | MySQL 8 |
| Build Tool | Gradle |
| Password Hashing | BCrypt |
| API Testing | Postman |
| Date/Time | `java.time` |

---

## 3. High-Level Architecture

```text
                    +------------------+
                    |     Postman      |
                    |   / API Client   |
                    +--------+---------+
                             |
                             | HTTP / JSON
                             v
                    +------------------+
                    |    Controller    |
                    | CustomerController|
                    +--------+---------+
                             |
                             v
                    +------------------+
                    |     Service      |
                    |  CustomerService |
                    |   AccountService |
                    +--------+---------+
                             |
              +--------------+--------------+
              |                             |
              v                             v
      +---------------+             +---------------+
      |    Mapper     |             |  Repository   |
      | CustomerMapper|             | Spring Data   |
      +---------------+             |     JPA       |
                                    +-------+-------+
                                            |
                                            v
                                    +---------------+
                                    |     MySQL     |
                                    | banking_system|
                                    +---------------+
```

---

## 4. Package Architecture

```text
com.chandan.enterprise_banking_transaction_system
│
├── config
│   └── JWTAuthConfig
│
├── controller
│   └── CustomerController
│
├── dto
│   ├── RequestDTO
│   │   ├── CustomerRequestDTO
│   │   ├── LoginRequestDTO
│   │   ├── DepositRequestDTO
│   │   └── WithdrawRequestDTO
│   │
│   └── ResponseDTO
│       ├── CustomerResponseDTO
│       ├── CustomerLoginResponseDTO
│       └── WithdrawResponseDTO
│
├── entity
│   ├── Customer
│   ├── Account
│   ├── AccountType
│   ├── AccountStatus
│   ├── KycStatus
│   └── CustomerRefreshToken
│
├── exception
│   ├── InvalidCredentialsException
│   └── UserAlreadyExistsException
│
├── mapper
│   └── CustomerMapper
│
├── repository
│   ├── CustomerRepository
│   └── CustomerRefreshTokenRepository
│
├── service
│   ├── CustomerService
│   └── AccountService
│
└── utils
    └── CustomerCodeGenerator
```

---

# 5. Domain Model

## 5.1 Customer

The `Customer` entity represents a bank customer.

### Important fields

```text
id
customerCode
firstName
lastName
password
dateOfBirth
gender
aadhaarNumber
panNumber
email
mobileNumber
addressLine1
addressLine2
city
state
country
postalCode
createdAt
updatedAt
```

### Relationship

```text
Customer 1 -------- * Account
```

A customer can have multiple bank accounts.

---

## 5.2 Account

`Account` is an abstract JPA entity.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account
```

### Important fields

```text
id
accountNumber
customer
accountType
availableBalance
kycStatus
status
dailyTransferLimit
minimumBalance
openedDate
createdAt
updatedAt
```

### Relationship

```text
Account * -------- 1 Customer
```

Each account belongs to exactly one customer.

---

## 5.3 AccountType

`AccountType` is an enum used to identify the type of bank account.

Example:

```java
public enum AccountType {
    SAVINGS,
    CURRENT,
    FIXED
}
```

> The exact values must match the values declared in your `AccountType` enum.

### Important

Java's:

```java
AccountType.valueOf(...)
```

requires an **exact enum constant**.

For example, if the enum contains:

```java
SAVINGS
```

then:

```java
AccountType.valueOf("SAVINGS")
```

works.

But:

```java
AccountType.valueOf("SAVING")
```

throws:

```text
IllegalArgumentException:
No enum constant AccountType.SAVING
```

Therefore, the API should either validate the input or expose the accepted account types clearly.

---

# 6. DTO Architecture

The API uses DTOs instead of directly exposing JPA entities.

## CustomerRequestDTO

```text
firstName
lastName
password
dateOfBirth
gender
aadhaarNumber
panNumber
email
mobileNumber
addressLine1
addressLine2
city
state
country
postalCode
accountType
```

`accountType` is received as a String from the API request and converted into the domain enum inside the service layer.

---

# 7. Customer Registration API

## Endpoint

```http
POST /customer/register
```

> Replace the path with the exact mapping defined in `CustomerController` if your controller uses a different base path.

## Request

```json
{
  "firstName": "John",
  "lastName": "Deo",
  "password": "StrongPassword@123",
  "dateOfBirth": "2003-01-15",
  "gender": "Male",
  "aadhaarNumber": "123456789012",
  "panNumber": "ABCDE1234F",
  "email": "johnDeo@example.com",
  "mobileNumber": "9876543210",
  "addressLine1": "123 Main Road",
  "addressLine2": "Near City Center",
  "city": "Indore",
  "state": "Madhya Pradesh",
  "country": "India",
  "postalCode": "452001",
  "accountType": "SAVINGS"
}
```

## Account Type

The value sent by Postman must correspond to the enum.

For example:

```json
"accountType": "SAVINGS"
```

If your enum contains `SAVINGS`.

If your enum contains `CURRENT`, then:

```json
"accountType": "CURRENT"
```

---

# 8. Customer Registration Workflow

```text
Client
  |
  | POST /customer/register
  v
CustomerController
  |
  v
CustomerService.register()
  |
  +--> Check Aadhaar already exists
  |
  +--> Convert CustomerRequestDTO -> Customer
  |
  +--> Generate Customer Code
  |
  +--> Check Customer Code uniqueness
  |
  +--> Encode password using BCrypt
  |
  +--> Save Customer
  |
  +--> Create Account through AccountService
  |
  +--> Build CustomerResponseDTO
  |
  v
Client receives response
```

## Detailed flow

### Step 1 — Validate duplicate customer

```java
customerRepository.existsByAadhaarNumber(...)
```

If Aadhaar already exists:

```text
UserAlreadyExistsException
```

is thrown.

### Step 2 — Convert DTO to entity

```java
Customer customer = CustomerMapper.toEntity(customerRequestDTO);
```

### Step 3 — Generate customer code

```java
String code = CustomerCodeGenerator.generate();
```

The generated code is checked against the database.

### Step 4 — Hash password

```java
customer.setPassword(
    passwordEncoder.encode(customerRequestDTO.getPassword())
);
```

The raw password should never be stored in the database.

### Step 5 — Save customer

```java
Customer savedCustomer = customerRepository.save(customer);
```

### Step 6 — Create account

```java
Account account = accountService.createAccount(
    savedCustomer,
    customerRequestDTO.getAccountType()
);
```

### Step 7 — Return response

```java
return CustomerMapper.toResponse(savedCustomer, account);
```

---

# 9. Transaction Boundary

Customer registration uses:

```java
@Transactional
```

This is important because registration performs multiple database operations.

Conceptually:

```text
BEGIN TRANSACTION

Save Customer
      |
Create Account
      |
Commit
```

If account creation fails after customer creation, the transaction can roll back the database changes.

This prevents a situation where a customer exists without the account that registration was supposed to create.

---

# 10. Login API

## Endpoint

```http
POST /customer/login
```

## Request

```json
{
  "customerCode": "ABC12345",
  "password": "StrongPassword@123"
}
```

## Workflow

```text
Client
  |
  | customerCode + password
  v
CustomerController
  |
  v
CustomerService.login()
  |
  +--> Find customer by customerCode
  |
  +--> Verify password using BCrypt
  |
  +--> Generate access token
  |
  +--> Generate refresh token
  |
  +--> Save refresh token
  |
  v
Return login response
```

---

# 11. JWT Authentication

The system uses two tokens.

## Access Token

Used to authenticate normal API requests.

```text
Short-lived
     |
     v
Authorization: Bearer <access-token>
```

## Refresh Token

Used to obtain a new access token when the access token expires.

The current implementation stores refresh tokens in:

```text
CustomerRefreshToken
```

with information such as:

```text
customer
token
revoked
expiryDate
createdAt
```

---

# 12. Login Response

Example:

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "customer_Name": "John Deo",
  "createdAt": "2026-08-27"
}
```

---

# 13. Account Creation Workflow

Account creation is delegated to:

```java
accountService.createAccount(...)
```

The responsibility is intentionally separated from `CustomerService`.

```text
CustomerService
      |
      | createAccount()
      v
AccountService
      |
      +--> Convert account type
      +--> Generate account number
      +--> Set initial balance
      +--> Set account status
      +--> Set KYC status
      +--> Set minimum balance
      +--> Set transfer limit
      +--> Save account
      |
      v
Account
```

---

# 14. Deposit Workflow

The intended deposit workflow is:

```text
Client
  |
  | POST /customer/deposit
  v
CustomerController
  |
  v
CustomerService.deposit()
  |
  +--> Validate customer/account
  |
  +--> Validate amount > 0
  |
  +--> Fetch account
  |
  +--> Increase availableBalance
  |
  +--> Create transaction record
  |
  +--> Save changes
  |
  v
Return DepositResponseDTO
```

### Important business rules

- Deposit amount must be positive.
- Account must exist.
- Account must be active.
- Account must belong to the authenticated customer.
- Transaction should be recorded.
- Balance update and transaction creation should occur in one transaction.

---

# 15. Withdrawal Workflow

The intended withdrawal workflow is:

```text
Client
  |
  | POST /customer/withdraw
  v
CustomerController
  |
  v
CustomerService.withdraw()
  |
  +--> Validate customer
  |
  +--> Validate account
  |
  +--> Validate amount > 0
  |
  +--> Check account status
  |
  +--> Check available balance
  |
  +--> Check minimum balance
  |
  +--> Deduct amount
  |
  +--> Create transaction record
  |
  +--> Save changes
  |
  v
Return WithdrawResponseDTO
```

### Core rule

A withdrawal must not allow:

```text
balance < minimumBalance
```

unless the product/business rules explicitly allow it.

---

# 16. Recommended Transaction Architecture

For a banking system, transaction processing should eventually be separated from `CustomerService`.

Recommended structure:

```text
controller
    |
    v
TransactionService
    |
    +--> AccountRepository
    |
    +--> TransactionRepository
    |
    +--> TransactionValidator
    |
    v
Database
```

Instead of putting all banking operations into:

```java
CustomerService
```

use:

```text
CustomerService
AccountService
TransactionService
```

This keeps responsibilities clean.

---

# 17. API Summary
# Base url 

```text 
 localhost:8080/api/auth
```

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/customer/register` | Register customer and create account |
| POST | `/customer/login` | Authenticate customer |
| POST | `/customer/deposit` | Deposit money |
| POST | `/customer/withdraw` | Withdraw money |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/logout` | Revoke refresh token |

> Verify endpoint paths against the actual controller mappings.

---

# 18. Error Handling

## Customer already exists

```text
UserAlreadyExistsException
```

Possible HTTP response:

```json
{
  "message": "Customer already exist"
}
```

Recommended status:

```http
409 CONFLICT
```

---

## Invalid login

```text
InvalidCredentialsException
```

Recommended status:

```http
401 UNAUTHORIZED
```

Avoid revealing whether the customer code or password was incorrect.

---

## Invalid Account Type

Current approach:

```java
AccountType.valueOf(
    customerRequestDTO.getAccountType().toUpperCase()
);
```

If the enum does not contain the requested value, Java throws:

```text
IllegalArgumentException
```

Recommended API behavior:

```http
400 BAD REQUEST
```

Example:

```json
{
  "message": "Invalid account type. Allowed values: SAVINGS, CURRENT"
}
```

---

# 19. Account Type Validation

A safer approach is to create a converter/helper method.

```java
public AccountType parseAccountType(String value) {

    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("Account type is required");
    }

    try {
        return AccountType.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
        throw new IllegalArgumentException(
            "Invalid account type: " + value
        );
    }
}
```

Then:

```java
AccountType accountType =
        parseAccountType(customerRequestDTO.getAccountType());
```

This makes the failure predictable instead of exposing a raw Java exception.

---

# 20. Important Design Improvements

## 20.1 Do not use `@Component` on DTOs

This:

```java
@Component
public class CustomerRequestDTO
```

is unnecessary.

DTOs are request/response data structures, not Spring beans.

Use:

```java
@Getter
@Setter
public class CustomerRequestDTO {
    ...
}
```

---

## 20.2 Prefer enum in internal service/domain boundaries

The API can receive:

```text
String accountType
```

but the service should convert it immediately:

```text
HTTP String
     |
     v
AccountType enum
     |
     v
AccountService
```

Do not keep passing arbitrary Strings deep into the application.

---

## 20.3 Add validation

Use Jakarta validation:

```java
@NotBlank
private String firstName;

@NotBlank
private String lastName;

@NotBlank
private String password;

@NotNull
private LocalDate dateOfBirth;

@NotBlank
private String aadhaarNumber;

@NotBlank
private String mobileNumber;

@NotBlank
private String accountType;
```

Controller:

```java
public CustomerResponseDTO register(
        @Valid @RequestBody CustomerRequestDTO request
) {
    ...
}
```

---

# 21. Security Architecture

```text
                 HTTP Request
                      |
                      v
              Spring Security
                      |
              JWT Authentication
                      |
          +-----------+-----------+
          |                       |
       Valid                   Invalid
          |                       |
          v                       v
   Controller                  401/403
          |
          v
       Service
```

For protected APIs:

```http
Authorization: Bearer <access-token>
```

The JWT should identify the authenticated customer.

---

# 22. Database Relationships

```text
Customer
   |
   | 1:N
   |
   +--------------------+
   |                    |
   v                    v
 Account           CustomerRefreshToken
   |
   |
   v
AccountType
```

Conceptually:

```text
Customer
  ├── Account
  ├── Account
  └── Account
```

and:

```text
Customer
  └── RefreshToken
      └── RefreshToken
```

This allows a customer to have multiple accounts and potentially multiple refresh-token sessions.

---

# 23. Recommended Future Architecture

As the project grows, use:

```text
                    API Gateway
                         |
              +----------+----------+
              |                     |
              v                     v
       Customer Service      Transaction Service
              |                     |
              v                     v
        Customer DB             Transaction DB
              |
              v
        Account Service
```

For the current monolithic project, however, the simpler architecture is appropriate:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

Do not introduce microservices just for the sake of saying the project is "microservices." First make the modular monolith correct.

---

# 24. Registration Example in Postman

### Request

```http
POST http://localhost:8080/customer/register
Content-Type: application/json
```

### Body

```json
{
  "firstName": "John",
  "lastName": "Deo",
  "password": "StrongPassword@123",
  "dateOfBirth": "2003-01-15",
  "gender": "Male",
  "aadhaarNumber": "123456789012",
  "panNumber": "ABCDE1234F",
  "email": "johndeo@example.com",
  "mobileNumber": "9876543210",
  "addressLine1": "123 Main Road",
  "addressLine2": "",
  "city": "Indore",
  "state": "Madhya Pradesh",
  "country": "India",
  "postalCode": "452001",
  "accountType": "SAVINGS"
}
```

The exact `accountType` value must be one of the constants declared in `AccountType`.

---

# 25. Current Implementation Status

| Module | Status |
|---|---|
| Customer Entity | Implemented |
| Account Entity | Implemented |
| Customer Registration | Implemented |
| Customer Code Generation | Implemented |
| Password Hashing | Implemented |
| Account Creation | Implemented |
| JWT Access Token | Implemented |
| JWT Refresh Token | Implemented |
| Login | Implemented |
| Deposit | Pending |
| Withdrawal | Pending |
| Transaction Entity | Recommended |
| Transaction History | Recommended |
| Global Exception Handler | Recommended |
| DTO Validation | Recommended |
| Role/Authorization | Recommended |
| Audit Logging | Recommended |

---

# 26. Key Design Principles

1. **Controller handles HTTP concerns.**
2. **Service handles business logic.**
3. **Repository handles persistence.**
4. **DTOs define API contracts.**
5. **Entities represent persistent domain objects.**
6. **Enums should represent fixed domain values.**
7. **Financial operations must be transactional.**
8. **Passwords must never be stored in plain text.**
9. **JWT access tokens should be short-lived.**
10. **Refresh tokens should be revocable and expire.**
11. **Account balance changes must be atomic.**
12. **Every deposit/withdrawal should have an auditable transaction record.**

---

# 27. End-to-End Customer Journey

```text
                CUSTOMER
                    |
                    v
             Register Account
                    |
                    v
          Customer + Bank Account
                    |
                    v
                 Login
                    |
                    v
         Access + Refresh Token
                    |
                    v
        Authenticated API Requests
                    |
          +---------+---------+
          |                   |
          v                   v
       Deposit            Withdrawal
          |                   |
          v                   v
      Update Balance      Validate Balance
          |                   |
          +---------+---------+
                    |
                    v
             Transaction
                 Record
                    |
                    v
              Account Balance
```

---

## 28. Conclusion

The application currently follows a **layered Spring Boot architecture** with DTOs, controllers, services, repositories, JPA entities, and MySQL persistence.

The most important next architectural step is to build a proper **Transaction module**. Deposits and withdrawals should not merely modify `availableBalance`; each operation should create an immutable transaction record with transaction ID, account, amount, type, status, timestamp, and reference information.

That will make the system much closer to a real banking transaction backend rather than a simple CRUD application.
