# Enterprise Banking Transaction System

## 1. Executive summary

The Enterprise Banking Transaction System is a Java 21 and Spring Boot REST
application for customer onboarding, account management, authentication, and
financial transactions. The current codebase is a functional backend
foundation with JPA persistence, BCrypt password hashing, JWT access and
refresh tokens, and MySQL integration.

This report consolidates the project documentation, database model, REST API
contract, analytics requirements, and an implementation review. It describes
the code that currently exists; proposed improvements are explicitly marked.

## 2. Expected deliverables

| Deliverable | Current status | Location or implementation |
|---|---|---|
| Enterprise banking application | Implemented foundation | `src/main/java` |
| Spring Boot project source | Implemented | Gradle project; Java 21 |
| Database schema | JPA-derived model documented below | Section 5 |
| REST API documentation | Documented below | Section 6 |
| Analytics reports | Transaction report endpoint and reporting design | Section 7 |
| Project documentation/report | Completed by this document | `PROJECT_REPORT.md` |

## 3. Technology and architecture

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| API | Spring MVC REST controllers |
| Security | Spring Security and JWT |
| Persistence | Spring Data JPA and Hibernate |
| Database | MySQL 8 |
| Build | Gradle |
| Password storage | BCrypt |
| Validation | Spring Boot validation dependency |

The request flow is:

```text
Client -> Controller -> Service -> Repository -> Hibernate -> MySQL
                    \-> DTO/Mapper
                    \-> Exception handling
```

The main domain relationships are:

```text
Customer 1 ---- * Account
Account  1 ---- * LedgerEntry
Account  1 ---- * Transaction (as source or destination)
User     1 ---- * RefreshToken
Customer 1 ---- * CustomerRefreshToken
```

## 4. Functional scope

### Implemented

- User registration, login, refresh-token, and logout endpoints.
- Customer registration and customer login.
- Savings, current, and fixed account types.
- Account states: active, frozen, blocked, and closed.
- Deposit, withdrawal, and transfer workflows.
- JWT-protected customer operations.
- Ledger entries and transaction references.
- Date-range transaction report generation.
- Centralized domain exceptions and response DTOs.

### Not evidenced in the current source

- Versioned database migrations such as Flyway or Liquibase.
- OpenAPI/Swagger generation.
- A separate analytics dashboard or persisted aggregate tables.
- Comprehensive unit, integration, security, and transaction-concurrency tests.
- CI configuration and deployment manifests.

## 5. Database schema

The application currently uses Hibernate schema auto-update. The following
logical schema is derived from the JPA entities and should be used as the
baseline for a future migration script.

### `customer`

| Column | Type | Constraints |
|---|---|---|
| `customer_code` | varchar(8) | primary key, not null |
| `first_name`, `last_name` | varchar | not null |
| `password` | varchar | not null |
| `date_of_birth` | date | not null |
| `gender` | varchar | not null |
| `aadhaar_number` | varchar | unique, not null |
| `pan_number` | varchar | nullable |
| `email` | varchar | nullable |
| `mobile_number` | varchar | not null |
| `address_line1` | varchar | not null |
| `address_line2` | varchar | nullable |
| `city`, `state`, `country` | varchar | not null |
| `postal_code` | varchar | nullable |
| `created_at`, `updated_at` | datetime | nullable |

### `account` and joined account subtype tables

`Account` is an abstract entity using joined inheritance. The base table has:

| Column | Type | Constraints |
|---|---|---|
| `account_number` | varchar(10) | primary key, not null |
| `customer_id` | varchar(8) | foreign key to `customer`, not null |
| `account_type` | varchar | enum: `SAVINGS`, `CURRENT`, `FIXED` |
| `available_balance` | decimal | defaults to zero |
| `kyc_status` | varchar | `PENDING`, `VERIFIED`, `REJECTED` |
| `status` | varchar | `ACTIVE`, `FROZEN`, `BLOCKED`, `CLOSED` |
| `daily_transfer_limit`, `minimum_balance` | decimal | nullable |
| `opened_date` | date | nullable |
| `created_at`, `updated_at` | datetime | nullable |

`saving_account`, `current_account`, and `fixed_account` are the expected
joined subtype tables. Their exact subtype columns should be generated from
the corresponding Java classes during migration creation.

### `transaction`

| Column | Type | Constraints |
|---|---|---|
| `transaction_reference` | varchar | primary key, unique, not null |
| `source_account_id` | varchar(10) | foreign key to `account`, not null |
| `destination_account_id` | varchar(10) | foreign key to `account`, not null |
| `amount`, `transaction_fee` | decimal | nullable |
| `transaction_type` | varchar | `DEPOSIT`, `WITHDRAWAL`, `TRANSFER`, `BILL_PAYMENT` |
| `status` | varchar | `PENDING`, `SUCCESS`, `FAILED`, `REVERSED` |
| `description`, `channel`, `failure_reason` | varchar | nullable |
| `initiated_at`, `completed_at` | datetime | nullable |

### `ledger_entry`

| Column | Type | Constraints |
|---|---|---|
| `transaction` | varchar | primary key in the current entity |
| `account_id` | varchar(10) | foreign key to `account` |
| `amount`, `total` | decimal | `total` not null |
| `entry_type` | varchar | `DEBIT` or `CREDIT` |
| `date` | datetime | nullable |

### Supporting tables

- `user`: generated numeric ID, unique username, password, role, status flags,
  login timestamp, and audit timestamps.
- `role`: generated ID and enum role type.
- `refresh_token`: generated ID, token data, and user relationship.
- `customer_refresh_token`: generated ID, token data, and customer relationship.
- `branch`: generated ID, branch code/name, IFSC code, address, location, and
  creation timestamp.

### Schema recommendations

1. Replace `spring.jpa.hibernate.ddl-auto=update` with versioned migrations
   before production deployment.
2. Add indexes for `customer_id`, transaction dates, transaction status, and
   account numbers.
3. Use a generated numeric `ledger_entry_id`; a transaction reference alone
   cannot safely identify multiple debit/credit entries.
4. Add database `CHECK` constraints for positive amounts and non-negative
   balances where supported.
5. Encrypt or tokenize Aadhaar and other regulated personal data.

## 6. REST API documentation

Base URL: `http://localhost:8080`

Protected endpoints use `Authorization: Bearer <access-token>`. Customer
transaction endpoints also support `X-Channel: WEB|MOBILE|ATM`.

### Authentication

| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register a system user |
| POST | `/api/auth/login` | No | Authenticate a system user |
| POST | `/api/auth/refresh-token?token={token}` | No | Issue a new access token |
| POST | `/api/auth/logout` | No | Revoke a refresh token |

### Customer operations

| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/customer/register` | No | Register a customer |
| POST | `/api/auth/customer/login` | No | Authenticate a customer |
| POST | `/api/auth/customer/deposit` | Yes | Deposit funds |
| POST | `/api/auth/customer/withdraw` | Yes | Withdraw funds |
| POST | `/api/auth/customer/transfer` | Yes | Transfer funds |
| POST | `/api/auth/customer/generate-report` | Yes | Query transactions by account/date |

Responses are wrapped in the project `ApiResponse<T>` type. Successful
operations return a message and a typed DTO. Domain failures are represented
by the project's exception handler and `ErrorResponseDTO`.

### Request examples

```json
{
  "accountNumber": "1234567890",
  "amount": 1000.00
}
```

```json
{
  "sourceAccountNumber": "1234567890",
  "destinationAccountNumber": "0987654321",
  "amount": 250.00,
  "description": "Invoice settlement"
}
```

The exact field names for registration and report requests are defined by the
request DTO classes and should be treated as the authoritative contract.

## 7. Analytics and reporting

The current reporting capability is the authenticated
`/api/auth/customer/generate-report` endpoint, which returns transaction report
rows for an account and date range.

Recommended report metrics:

| Metric | Definition |
|---|---|
| Transaction volume | Count of successful transactions by day/type |
| Credit total | Sum of successful deposits and incoming transfers |
| Debit total | Sum of successful withdrawals and outgoing transfers |
| Net movement | Credit total minus debit total |
| Failure rate | Failed transactions divided by all initiated transactions |
| Channel mix | Successful transaction count grouped by WEB/MOBILE/ATM |
| Average transfer | Mean successful transfer amount |

Example MySQL aggregation for an operations' dashboard:

```sql
SELECT DATE(initiated_at) AS transaction_day,
       transaction_type,
       channel,
       COUNT(*) AS transaction_count,
       SUM(amount) AS total_amount
FROM `transaction`
WHERE status = 'SUCCESS'
  AND initiated_at >= :start_date
  AND initiated_at < :end_date
GROUP BY DATE(initiated_at), transaction_type, channel
ORDER BY transaction_day, transaction_type, channel;
```

Reports should be authorization-scoped to the authenticated customer's
accounts, use an inclusive start/exclusive end date convention, and redact
personal identifiers.

## 8. Review findings and priorities

### High priority

1. **Transaction data model is too strict for deposits and withdrawals.**
   Both source and destination accounts are currently non-null, although a
   deposit or withdrawal normally has only one customer account. Model the
   operation explicitly or allow the unused side to be null with service-level
   invariants.
2. **Schema evolution is not controlled.** `ddl-auto=update` is unsuitable for
   production banking data. Introduce migrations and test them from an empty
   database.
3. **Financial concurrency needs proof.** Deposit, withdrawal, and transfer
   operations require transaction boundaries, balance locking/optimistic
   versioning, idempotency keys, and rollback tests to prevent lost updates or
   double spending.
4. **Sensitive data protection needs strengthening.** Aadhaar, PAN, passwords,
   and tokens require field-level handling, retention rules, audit logging, and
   secret values supplied through environment configuration.

### Medium priority

1. Add `@Valid` to request bodies and validation constraints to all request
   DTOs, especially positive monetary amounts and valid account identifiers.
2. Generate OpenAPI documentation and publish a machine-readable contract.
3. Add integration tests for authentication, authorization boundaries,
   insufficient balances, blocked accounts, duplicate requests, and report
   ownership.
4. Add structured audit events for login, token revocation, account status
   changes, and every financial state transition.
5. Standardize channel extraction on request headers for all transaction
   endpoints.

### Low priority

1. Add health, metrics, tracing, and centralized log correlation IDs.
2. Add CI checks for build, tests, formatting, dependency vulnerabilities, and
   migration validation.
3. Add deployment configuration with separate development, test, and
   production profiles.

## 9. Build and run

Prerequisites:

- JDK 21
- MySQL 8
- A database named `banking_system`

Configure database credentials through environment-specific configuration.
Do not commit passwords or JWT signing keys.

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

The existing test suite contains a Spring context-load test. Before release,
the test suite should include the financial and security cases listed in
Section 8.

## 10. Acceptance checklist

- [x] Application starts against a clean database.
- [x] User and customer credentials are hashed and never returned in DTOs.
- [ ] Every protected endpoint rejects missing, expired, and wrong-customer
  tokens.
- [ ] Amounts are positive and use `BigDecimal` without floating-point
  conversion.
- [ ] Concurrent withdrawals and transfers cannot overdraw an account.
- [ ] Failed transactions leave balances and ledger entries consistent.
- [ ] Reports cannot access another customer's account.
- [ ] Database changes are delivered through reviewed migrations.
- [ ] API examples and OpenAPI output match the DTO implementation.
