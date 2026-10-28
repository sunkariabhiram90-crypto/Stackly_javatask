package com.example.movieticket;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class TicketControllerTest {
    @Autowired MockMvc mvc;
    @Test void createsTicket() throws Exception {
        mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).content("""
          {"movieName":"Interstellar","customerName":"Abhiram","seatNumber":"A10","ticketPrice":250.00,"screeningId":"INT-TEST-001"}
        """)).andExpect(status().isCreated()).andExpect(jsonPath("$.ticketId").exists()).andExpect(jsonPath("$.status").value("BOOKED"));
    }
    @Test void blankMovieNameReturnsBadRequest() throws Exception {
        mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).content("""
          {"movieName":" ","customerName":"Abhiram","seatNumber":"A10","ticketPrice":250.00,"screeningId":"INT-TEST-002"}
        """)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}
