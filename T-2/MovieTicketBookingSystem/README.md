# Movie Ticket Booking System

A beginner-friendly REST API project using Java 17, Spring Boot 3, Maven, Bean Validation, and in-memory storage. No database is required.

## Features
- Book tickets with generated IDs
- List and retrieve bookings; search by movie name
- PUT full update and PATCH partial update
- Cancel bookings without deleting history; cancelled seats become available again
- Seat uniqueness per screening, with synchronized booking/update operations
- Input validation and centralized JSON error responses
- Unit and MockMvc integration tests
- Sample requests in `requests/movie-ticket-requests.http`

## Requirements
- JDK 17 or newer
- Maven 3.8+
- Eclipse IDE (Eclipse IDE for Enterprise Java and Web Developers is convenient) or VS Code with Java extensions

## Run
From this directory:

```bash
mvn clean test
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. Open `requests/movie-ticket-requests.http` in VS Code or use Postman/curl.

## Main endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/tickets` | Create a booking |
| GET | `/api/tickets` | List all tickets |
| GET | `/api/tickets/{ticketId}` | Retrieve a ticket |
| GET | `/api/tickets/movie/{movieName}` | Search by movie |
| PUT | `/api/tickets/{ticketId}` | Replace booking fields |
| PATCH | `/api/tickets/{ticketId}` | Change selected fields |
| DELETE | `/api/tickets/{ticketId}` | Cancel booking (soft cancellation) |
| GET | `/api/tickets/availability?screeningId=INT-2026-001&seatNumber=A10` | Check seat availability |

## Example request
```json
{
  "movieName": "Interstellar",
  "customerName": "Abhiram",
  "seatNumber": "A10",
  "ticketPrice": 250.00,
  "screeningId": "INT-2026-001"
}
```

`screeningId` is included because the same seat can be booked for different showings. The required exercise fields remain present. Seat numbers accept formats such as `A1`, `A10`, or `AA10`. Price must be positive and have at most two decimal places. Unknown JSON fields are rejected.

## Error responses
- `400 Bad Request`: invalid JSON, unknown fields, or validation errors
- `404 Not Found`: ticket ID does not exist
- `409 Conflict`: seat already booked or invalid status transition

## Important limitation
This project uses in-memory storage to keep the exercise simple. Bookings are cleared when the application restarts. It is not intended for production or multi-instance deployment. A database and transactional unique constraint would be needed for persistent, production-grade seat allocation.
