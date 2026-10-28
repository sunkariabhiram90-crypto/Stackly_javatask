package com.example.movieticket.service;

import com.example.movieticket.dto.TicketPatchRequest;
import com.example.movieticket.dto.TicketRequest;
import com.example.movieticket.exception.DuplicateSeatException;
import com.example.movieticket.exception.TicketNotFoundException;
import com.example.movieticket.model.Ticket;
import com.example.movieticket.model.TicketStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final ConcurrentHashMap<Long, Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public synchronized Ticket create(TicketRequest request) {
        String screening = clean(request.getScreeningId());
        String seat = clean(request.getSeatNumber()).toUpperCase(Locale.ROOT);
        ensureSeatAvailable(screening, seat, null);
        Ticket ticket = new Ticket(nextId.getAndIncrement(), clean(request.getMovieName()), clean(request.getCustomerName()),
                seat, request.getTicketPrice(), screening, TicketStatus.BOOKED);
        tickets.put(ticket.getTicketId(), ticket);
        return copy(ticket);
    }
    public List<Ticket> findAll() {
        return tickets.values().stream().map(this::copy).sorted(Comparator.comparing(Ticket::getTicketId)).toList();
    }
    public Ticket findById(Long id) { return copy(require(id)); }
    public List<Ticket> findByMovie(String movieName) {
        if (movieName == null || movieName.isBlank()) throw new IllegalArgumentException("Movie name cannot be blank");
        return tickets.values().stream().filter(t -> t.getMovieName().equalsIgnoreCase(movieName.trim()))
                .map(this::copy).sorted(Comparator.comparing(Ticket::getTicketId)).toList();
    }
    public synchronized Ticket replace(Long id, TicketRequest request) {
        Ticket current = require(id);
        if (current.getStatus() == TicketStatus.CANCELLED) throw new IllegalStateException("Cancelled tickets cannot be updated");
        String screening = clean(request.getScreeningId());
        String seat = clean(request.getSeatNumber()).toUpperCase(Locale.ROOT);
        ensureSeatAvailable(screening, seat, id);
        Ticket updated = new Ticket(id, clean(request.getMovieName()), clean(request.getCustomerName()), seat,
                request.getTicketPrice(), screening, current.getStatus());
        tickets.put(id, updated);
        return copy(updated);
    }
    public synchronized Ticket patch(Long id, TicketPatchRequest request) {
        if (request.isEmpty()) throw new IllegalArgumentException("PATCH request must contain at least one field");
        Ticket current = require(id);
        if (current.getStatus() == TicketStatus.CANCELLED) throw new IllegalStateException("Cancelled tickets cannot be updated");
        String screening = request.getScreeningId() == null ? current.getScreeningId() : clean(request.getScreeningId());
        String seat = request.getSeatNumber() == null ? current.getSeatNumber() : clean(request.getSeatNumber()).toUpperCase(Locale.ROOT);
        ensureSeatAvailable(screening, seat, id);
        Ticket updated = new Ticket(id,
                request.getMovieName() == null ? current.getMovieName() : clean(request.getMovieName()),
                request.getCustomerName() == null ? current.getCustomerName() : clean(request.getCustomerName()),
                seat, request.getTicketPrice() == null ? current.getTicketPrice() : request.getTicketPrice(),
                screening, current.getStatus());
        tickets.put(id, updated);
        return copy(updated);
    }
    public synchronized Ticket cancel(Long id) {
        Ticket current = require(id);
        if (current.getStatus() == TicketStatus.CANCELLED) throw new IllegalStateException("Ticket is already cancelled");
        current.setStatus(TicketStatus.CANCELLED);
        tickets.put(id, current);
        return copy(current);
    }
    public boolean isAvailable(String screeningId, String seatNumber) {
        if (screeningId == null || screeningId.isBlank() || seatNumber == null || seatNumber.isBlank())
            throw new IllegalArgumentException("screeningId and seatNumber are required");
        String screening = screeningId.trim(); String seat = seatNumber.trim();
        return tickets.values().stream().noneMatch(t -> t.getStatus() == TicketStatus.BOOKED
                && t.getScreeningId().equalsIgnoreCase(screening) && t.getSeatNumber().equalsIgnoreCase(seat));
    }
    private void ensureSeatAvailable(String screening, String seat, Long ignoreId) {
        boolean exists = tickets.values().stream().anyMatch(t -> !t.getTicketId().equals(ignoreId)
                && t.getStatus() == TicketStatus.BOOKED && t.getScreeningId().equalsIgnoreCase(screening)
                && t.getSeatNumber().equalsIgnoreCase(seat));
        if (exists) throw new DuplicateSeatException(seat, screening);
    }
    private Ticket require(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Ticket ID must be a positive number");
        Ticket ticket = tickets.get(id);
        if (ticket == null) throw new TicketNotFoundException(id);
        return ticket;
    }
    private String clean(String value) { return value.trim(); }
    private Ticket copy(Ticket t) { return new Ticket(t.getTicketId(), t.getMovieName(), t.getCustomerName(), t.getSeatNumber(), t.getTicketPrice(), t.getScreeningId(), t.getStatus()); }
}
