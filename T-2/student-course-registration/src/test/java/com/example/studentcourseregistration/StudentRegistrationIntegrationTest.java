package com.example.studentcourseregistration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentRegistrationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    private String validBody(String name, String course, String email, int age) throws Exception {
        return objectMapper.writeValueAsString(new Request(name, course, email, age));
    }
    record Request(String studentName, String courseName, String email, Integer age) {}

    @Test void registersValidStudent() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
            .content(validBody("Abhiram S", "Java", "abhiram@example.com", 21)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.studentId").exists())
            .andExpect(jsonPath("$.studentName").value("Abhiram S"))
            .andExpect(jsonPath("$.courseName").value("Java"));
    }
    @Test void rejectsBlankName() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
            .content(validBody(" ", "Java", "valid@example.com", 20)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.studentName").value("Student name cannot be empty"));
    }
    @Test void rejectsInvalidEmail() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
            .content(validBody("Test Student", "Java", "not-an-email", 20)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.email").value("Enter valid email"));
    }
    @Test void rejectsAgeBelow18() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
            .content(validBody("Test Student", "Java", "young@example.com", 17)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.age").value("Age must be at least 18"));
    }
    @Test void returns404ForMissingRegistration() throws Exception {
        mvc.perform(get("/api/students/99999")).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Registration not found with ID: 99999"));
    }
    @Test void rejectsDuplicateEmailAndCourse() throws Exception {
        String body = validBody("Test Student", "Java", "same@example.com", 20);
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
    }
}
