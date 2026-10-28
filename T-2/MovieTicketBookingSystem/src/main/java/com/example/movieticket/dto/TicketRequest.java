package com.example.movieticket.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class TicketRequest {
    @NotBlank(message = "Movie name is required") @Size(max = 100, message = "Movie name must be at most 100 characters")
    private String movieName;
    @NotBlank(message = "Customer name cannot be empty") @Size(max = 100, message = "Customer name must be at most 100 characters")
    private String customerName;
    @NotBlank(message = "Seat number is required") @Pattern(regexp = "(?i)^[A-Z]{1,2}[1-9][0-9]?$", message = "Seat number must look like A1, A10, or AA10")
    private String seatNumber;
    @Positive(message = "Ticket price must be positive") @Digits(integer = 7, fraction = 2, message = "Ticket price can have at most 2 decimal places")
    private BigDecimal ticketPrice;
    @NotBlank(message = "Screening ID is required") @Size(max = 50, message = "Screening ID must be at most 50 characters")
    private String screeningId;
    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public String getScreeningId() { return screeningId; }
    public void setScreeningId(String screeningId) { this.screeningId = screeningId; }
}
