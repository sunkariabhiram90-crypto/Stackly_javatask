package com.example.movieticket.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class TicketPatchRequest {
    @Size(min = 1, max = 100, message = "Movie name must be 1 to 100 characters") private String movieName;
    @Size(min = 1, max = 100, message = "Customer name must be 1 to 100 characters") private String customerName;
    @Pattern(regexp = "(?i)^[A-Z]{1,2}[1-9][0-9]?$", message = "Seat number must look like A1, A10, or AA10") private String seatNumber;
    @Positive(message = "Ticket price must be positive") @Digits(integer = 7, fraction = 2, message = "Ticket price can have at most 2 decimal places") private BigDecimal ticketPrice;
    @Size(min = 1, max = 50, message = "Screening ID must be 1 to 50 characters") private String screeningId;
    public String getMovieName() { return movieName; } public void setMovieName(String v) { movieName = v; }
    public String getCustomerName() { return customerName; } public void setCustomerName(String v) { customerName = v; }
    public String getSeatNumber() { return seatNumber; } public void setSeatNumber(String v) { seatNumber = v; }
    public BigDecimal getTicketPrice() { return ticketPrice; } public void setTicketPrice(BigDecimal v) { ticketPrice = v; }
    public String getScreeningId() { return screeningId; } public void setScreeningId(String v) { screeningId = v; }
    public boolean isEmpty() { return movieName == null && customerName == null && seatNumber == null && ticketPrice == null && screeningId == null; }
}
