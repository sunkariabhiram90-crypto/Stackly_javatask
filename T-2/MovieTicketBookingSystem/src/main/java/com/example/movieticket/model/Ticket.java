package com.example.movieticket.model;

import java.math.BigDecimal;

public class Ticket {
    private Long ticketId;
    private String movieName;
    private String customerName;
    private String seatNumber;
    private BigDecimal ticketPrice;
    private String screeningId;
    private TicketStatus status;

    public Ticket() {}
    public Ticket(Long ticketId, String movieName, String customerName, String seatNumber,
                  BigDecimal ticketPrice, String screeningId, TicketStatus status) {
        this.ticketId = ticketId; this.movieName = movieName; this.customerName = customerName;
        this.seatNumber = seatNumber; this.ticketPrice = ticketPrice; this.screeningId = screeningId; this.status = status;
    }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
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
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
}
