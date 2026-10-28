package com.example.movieticket.controller;

import com.example.movieticket.dto.TicketPatchRequest;
import com.example.movieticket.dto.TicketRequest;
import com.example.movieticket.model.Ticket;
import com.example.movieticket.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;
    public TicketController(TicketService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@Valid @RequestBody TicketRequest request) { return service.create(request); }
    @GetMapping public List<Ticket> all() { return service.findAll(); }
    @GetMapping("/{ticketId}") public Ticket byId(@PathVariable Long ticketId) { return service.findById(ticketId); }
    @GetMapping("/movie/{movieName}") public List<Ticket> byMovie(@PathVariable String movieName) { return service.findByMovie(movieName); }
    @PutMapping("/{ticketId}") public Ticket replace(@PathVariable Long ticketId, @Valid @RequestBody TicketRequest request) { return service.replace(ticketId, request); }
    @PatchMapping("/{ticketId}") public Ticket patch(@PathVariable Long ticketId, @Valid @RequestBody TicketPatchRequest request) { return service.patch(ticketId, request); }
    @DeleteMapping("/{ticketId}") public Ticket cancel(@PathVariable Long ticketId) { return service.cancel(ticketId); }
    @GetMapping("/availability") public Availability availability(@RequestParam String screeningId, @RequestParam String seatNumber) {
        return new Availability(screeningId, seatNumber, service.isAvailable(screeningId, seatNumber));
    }
    public record Availability(String screeningId, String seatNumber, boolean available) {}
}
