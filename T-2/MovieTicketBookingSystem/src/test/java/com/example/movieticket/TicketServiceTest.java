package com.example.movieticket;

import static org.junit.jupiter.api.Assertions.*;
import com.example.movieticket.dto.TicketPatchRequest;
import com.example.movieticket.dto.TicketRequest;
import com.example.movieticket.exception.DuplicateSeatException;
import com.example.movieticket.exception.TicketNotFoundException;
import com.example.movieticket.model.Ticket;
import com.example.movieticket.model.TicketStatus;
import com.example.movieticket.service.TicketService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketServiceTest {
    private TicketService service;
    @BeforeEach void setUp() { service = new TicketService(); }
    private TicketRequest request(String seat) {
        TicketRequest r = new TicketRequest(); r.setMovieName(" Interstellar "); r.setCustomerName("Abhiram");
        r.setSeatNumber(seat); r.setTicketPrice(new BigDecimal("250.00")); r.setScreeningId("INT-001"); return r;
    }
    @Test void createsTicketAndTrimsText() {
        Ticket t = service.create(request("a10"));
        assertEquals(1L, t.getTicketId()); assertEquals("Interstellar", t.getMovieName());
        assertEquals("A10", t.getSeatNumber()); assertEquals(TicketStatus.BOOKED, t.getStatus());
    }
    @Test void rejectsDuplicateSeatForSameScreening() {
        service.create(request("A10")); assertThrows(DuplicateSeatException.class, () -> service.create(request("a10")));
    }
    @Test void cancellationReleasesSeat() {
        Ticket t = service.create(request("A10")); service.cancel(t.getTicketId());
        assertTrue(service.isAvailable("INT-001", "A10")); assertEquals(TicketStatus.CANCELLED, service.findById(t.getTicketId()).getStatus());
        assertEquals("A10", service.create(request("A10")).getSeatNumber());
    }
    @Test void patchChangesOnlySuppliedField() {
        Ticket t = service.create(request("A10")); TicketPatchRequest patch = new TicketPatchRequest(); patch.setCustomerName("New Name");
        Ticket updated = service.patch(t.getTicketId(), patch);
        assertEquals("New Name", updated.getCustomerName()); assertEquals("A10", updated.getSeatNumber());
    }
    @Test void missingTicketThrowsNotFound() { assertThrows(TicketNotFoundException.class, () -> service.findById(99L)); }
    @Test void emptyPatchIsRejected() { Ticket t = service.create(request("A10")); assertThrows(IllegalArgumentException.class, () -> service.patch(t.getTicketId(), new TicketPatchRequest())); }
}
