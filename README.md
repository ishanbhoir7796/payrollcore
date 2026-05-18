# PayrollCore

An enterprise-grade Payroll Management System built as 3 independent microservices using Java Spring Boot and MongoDB.

## Architecture

```
+------------------+     +------------------+     +------------------+
|   Auth Service   |     | Employee Service |     | Payroll Service  |
|   Port: 8081     |     |   Port: 8082     |     |   Port: 8083     |
|                  |     |                  |     |                  |
| - Register       |     | - Employee CRUD  |     | - Run Payroll    |
| - Login          |     | - Salary Mgmt    |     | - Payslips       |
| - JWT Tokens     |     | - Departments    |     | - Tax Engine     |
|                  |     |                  |     | - Audit Logs     |
+------------------+     +------------------+     +------------------+
         |                       |                        |
         +───────────────────────+────────────────────────+
                                 |
                         MongoDB Atlas (Cloud)
                +────────────────+────────────────+
             auth-db          employee-db       payroll-db
```

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Core language |
| Spring Boot 3.5 | REST API framework |
| Spring Security | Authentication and authorization |
| JWT (jjwt 0.12.6) | Token based authentication |
| MongoDB Atlas | Cloud database |
| Spring Data MongoDB | Database layer |
| Gradle | Build tool |
| JUnit 5 + Mockito | Unit testing |
| SpringDoc OpenAPI | API documentation |
| Lombok | Boilerplate reduction |

## Roles and Permissions

| Role | Permissions |
|------|-------------|
| ADMIN | Full access to all services |
| HR | Manage employees, approve leaves |
| FINANCE | Run payroll, manage deductions, view reports |
| EMPLOYEE | View own payslips |

## Project Structure

```
payrollcore/
├── auth-service/          # JWT authentication and user management
├── employee-service/      # Employee profiles and salary structures
└── payroll-service/       # Payroll engine and payslip generation
```

## Prerequisites

- Java 21+
- MongoDB Atlas account (free tier)
- Gradle (handled by wrapper)

## Setup and Run

### 1. Clone the repository
```bash
git clone https://github.com/ishanbhoir7796/payrollcore.git
cd payrollcore
```

### 2. Configure MongoDB
Update `application.yml` in each service with your MongoDB Atlas URI:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/
```

### 3. Run services in order
```bash
# Terminal 1 - Auth Service
cd auth-service
./gradlew bootRun

# Terminal 2 - Employee Service
cd employee-service
./gradlew bootRun

# Terminal 3 - Payroll Service
cd payroll-service
./gradlew bootRun
```

### 4. Access Swagger UI
| Service | URL |
|---------|-----|
| Auth | http://localhost:8081/swagger-ui.html |
| Employee | http://localhost:8082/swagger-ui.html |
| Payroll | http://localhost:8083/swagger-ui.html |

## API Endpoints

### Auth Service (8081)
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login and get JWT token
```

### Employee Service (8082)
```
POST   /api/employees                            - Create employee (HR, ADMIN)
GET    /api/employees                            - Get all employees
GET    /api/employees/{id}                       - Get by ID
GET    /api/employees/code/{code}                - Get by employee code
GET    /api/employees/department/{department}    - Get by department
PUT    /api/employees/{id}                       - Update employee (HR, ADMIN)
PUT    /api/employees/{id}/salary                - Update salary (FINANCE, ADMIN)
DELETE /api/employees/{id}                       - Delete employee (ADMIN)
```

### Payroll Service (8083)
```
POST /api/payroll/run                                    - Run monthly payroll (FINANCE, ADMIN)
GET  /api/payroll/runs                                   - Get all payroll runs
GET  /api/payroll/runs/{id}/payslips                     - Get payslips by run
GET  /api/payroll/payslips/employee/{id}                 - Get employee payslips
GET  /api/payroll/payslips/employee/{id}/{month}/{year}  - Get specific payslip
POST /api/payroll/deductions                             - Create deduction config
PUT  /api/payroll/deductions/{id}                        - Update deduction config
GET  /api/payroll/deductions                             - Get all deduction configs
```

## Payroll Calculation Engine

```
Gross Salary  =  Basic Pay + HRA + Travel Allowance + Performance Bonus

Deductions:
  Provident Fund    =  Basic Pay x 12%
  Health Insurance  =  Gross Salary x 2%
  Income Tax        =  Slab based calculation

Net Pay  =  Gross Salary - Total Deductions
```

## Tax Slabs

| Income Range | Tax Rate |
|-------------|----------|
| 0 - 3000 | 0% |
| 3001 - 6000 | 10% |
| 6001 - 10000 | 20% |
| 10001+ | 30% |

## Running Tests

```bash
# Auth Service
cd auth-service
./gradlew test

# Employee Service
cd employee-service
./gradlew test

# Payroll Service
cd payroll-service
./gradlew test
```

## Authentication Flow

```
1. Register user with role (ADMIN, HR, FINANCE, EMPLOYEE)
2. Login to get JWT access token (24hr) + refresh token (7 days)
3. Include token in all requests: Authorization: Bearer <token>
4. Each service validates token independently
```

## Sample Request - Run Payroll

```json
POST /api/payroll/run
Authorization: Bearer <finance_token>

{
    "month": "MAY",
    "year": 2026
}
```

Sample Response:
```json
{
    "id": "6a04df3683997d45805a97e2",
    "month": "MAY",
    "year": 2026,
    "status": "COMPLETED",
    "totalEmployees": 1,
    "totalGrossPaid": 8500.0,
    "totalDeductions": 2470.0,
    "totalNetPaid": 6030.0,
    "processedBy": "finance@payrollcore.com",
    "processedAt": "2026-05-13T13:29:43.970022"
}
```

## Author

Ishan Bhoir
GitHub: https://github.com/ishanbhoir7796
LinkedIn: https://linkedin.com/in/ishanbhoir
