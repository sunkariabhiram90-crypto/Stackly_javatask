package com.example.foodorder;

import com.example.foodorder.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired OrderRepository repository;
    @Autowired JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE FOOD_ORDERS ALTER COLUMN ORDER_ID RESTART WITH 1");
    }

    private String validOrder() {
        return """
                {
                  "customerName": "Abhiram",
                  "foodItem": "Chicken Burger",
                  "quantity": 2,
                  "price": 149.50
                }
                """;
    }

    @Test
    void createAndRetrieveOrder() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/orders/")))
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.customerName").value("Abhiram"))
                .andExpect(jsonPath("$.price").value(149.50));

        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodItem").value("Chicken Burger"));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        String invalid = """
                {"customerName":"   ","foodItem":"","quantity":0,"price":-1}
                """;
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.customerName").exists())
                .andExpect(jsonPath("$.fieldErrors.foodItem").exists())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists())
                .andExpect(jsonPath("$.fieldErrors.price").exists());
    }

    @Test
    void getMissingOrderReturns404() throws Exception {
        mvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found with ID: 999"));
    }

    @Test
    void putReplacesOrder() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated());
        String replacement = """
                {"customerName":"Priya","foodItem":"Pizza","quantity":1,"price":250.00}
                """;
        mvc.perform(put("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content(replacement))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Priya"))
                .andExpect(jsonPath("$.foodItem").value("Pizza"))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    void patchChangesOnlySuppliedField() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4))
                .andExpect(jsonPath("$.customerName").value("Abhiram"))
                .andExpect(jsonPath("$.foodItem").value("Chicken Burger"));
    }

    @Test
    void patchRejectsEmptyNullAndUnknownFields() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":null}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":7}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"unexpected\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204AndSubsequentGet404() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/orders/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get("/api/orders/1")).andExpect(status().isNotFound());
    }

    @Test
    void rejectsMalformedJsonAndInvalidPathId() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/orders/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvalidPatchValues() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(validOrder()))
                .andExpect(status().isCreated());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":0}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/orders/1").contentType(MediaType.APPLICATION_JSON).content("{\"price\":0}"))
                .andExpect(status().isBadRequest());
    }
}
