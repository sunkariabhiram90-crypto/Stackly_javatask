# Online Food Order Management System

A beginner-friendly REST API built with Java 17, Spring Boot 3, Spring Web, Bean Validation, Spring Data JPA, and H2. MySQL can be enabled with a Spring profile.

## Features

- Create, list, retrieve, replace, partially update, and delete food orders.
- Automatic order IDs.
- Request DTO validation and `BigDecimal` money values.
- Centralized JSON error responses.
- PATCH preserves omitted fields and rejects nulls, empty objects, unknown fields, and attempts to change `orderId`.
- Integration tests using JUnit 5, Spring Boot Test, and MockMvc.
- OpenAPI/Swagger UI.

## Requirements

- JDK 17+
- Maven 3.6.3+ (or use a Maven wrapper if you add one)
- Optional: MySQL 8+

## Run in Eclipse

1. Extract the project.
2. Choose **File → Import → Maven → Existing Maven Projects**.
3. Select the extracted project directory and finish.
4. Right-click the project → **Maven → Update Project**.
5. Run `FoodOrderApplication.java` as **Java Application** or **Spring Boot App**.

## Build and run

```bash
mvn clean test
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api/orders`.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:foodorders`
- Username: `sa`
- Password: leave blank

The default H2 database is in memory and resets when the application stops.

## Optional MySQL configuration

Create a database if desired:

```sql
CREATE DATABASE food_order_management;
```

Configure environment variables before starting:

Windows PowerShell:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/food_order_management"
$env:DB_USERNAME="your_mysql_username"
$env:DB_PASSWORD="your_mysql_password"
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Alternatively:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=mysql
```

Do not commit real credentials. The MySQL driver is included; the default profile remains H2.

## Endpoints

| Method | Path | Success |
|---|---|---|
| GET | `/api/orders` | 200 |
| GET | `/api/orders/{id}` | 200 |
| POST | `/api/orders` | 201 + Location header |
| PUT | `/api/orders/{id}` | 200 |
| PATCH | `/api/orders/{id}` | 200 |
| DELETE | `/api/orders/{id}` | 204 |

## Create example

```http
POST /api/orders
Content-Type: application/json
```

```json
{
  "customerName": "Abhiram",
  "foodItem": "Chicken Burger",
  "quantity": 2,
  "price": 149.50
}
```

The server generates `orderId`. A successful response contains the generated ID and order data.

## PATCH behavior

```http
PATCH /api/orders/1
Content-Type: application/json
```

```json
{"quantity": 4}
```

Only quantity changes. The other fields are preserved. PATCH requires a non-empty JSON object, accepts only `customerName`, `foodItem`, `quantity`, and `price`, and rejects explicit nulls. PUT requires all editable fields.

## Validation rules

- Customer name: required, non-blank, max 100 characters.
- Food item: required, non-blank, max 120 characters.
- Quantity: required integer, minimum 1.
- Price: required, greater than zero, up to 8 integer digits and 2 decimal places.
- Strings are trimmed before storage.
- IDs must be positive integers.
- Unknown JSON properties are rejected for DTO-based create/PUT requests.

## Error response

Example for an invalid request:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/orders",
  "fieldErrors": {
    "customerName": "Customer name is required",
    "quantity": "Quantity must be at least 1"
  }
}
```

Timestamp is generated at request time. Field errors vary with the invalid fields.

## Tests

```bash
mvn clean test
```

The integration suite exercises create, list, get-by-ID, PUT, PATCH, DELETE, validation failures, malformed JSON, invalid IDs, and missing orders.

## API request file

Open `api-tests/food-order-api-tests.http` in IntelliJ IDEA or a compatible HTTP client. In VS Code, install a REST Client extension to run the requests. Postman can also import the requests manually.

## Implementation notes

- The `price` field is the unit price per item; this basic project does not calculate an order total.
- Authentication, payments, restaurant menus, delivery tracking, and customer accounts are outside the scope of this exercise.
- For production, add database migrations, authentication/authorization, rate limiting, audit logging, and operational monitoring.
