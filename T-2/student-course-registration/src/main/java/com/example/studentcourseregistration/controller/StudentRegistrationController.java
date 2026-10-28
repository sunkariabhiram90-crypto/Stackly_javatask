package com.example.studentcourseregistration.controller;

import com.example.studentcourseregistration.dto.RegistrationRequest;
import com.example.studentcourseregistration.model.StudentRegistration;
import com.example.studentcourseregistration.service.StudentRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentRegistrationController {
    private final StudentRegistrationService service;
    public StudentRegistrationController(StudentRegistrationService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<StudentRegistration> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }
    @GetMapping
    public List<StudentRegistration> findAll() { return service.findAll(); }
    @GetMapping("/{id}")
    public StudentRegistration findById(@PathVariable Long id) { return service.findById(id); }
    @PutMapping("/{id}")
    public StudentRegistration update(@PathVariable Long id, @Valid @RequestBody RegistrationRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/search")
    public List<StudentRegistration> searchByCourse(@RequestParam String course) { return service.searchByCourse(course); }
}
