package com.example.studentcourseregistration.service;

import com.example.studentcourseregistration.dto.RegistrationRequest;
import com.example.studentcourseregistration.exception.DuplicateRegistrationException;
import com.example.studentcourseregistration.exception.ResourceNotFoundException;
import com.example.studentcourseregistration.model.StudentRegistration;
import com.example.studentcourseregistration.repository.StudentRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentRegistrationService {
    private final StudentRegistrationRepository repository;
    public StudentRegistrationService(StudentRegistrationRepository repository) { this.repository = repository; }

    @Transactional
    public StudentRegistration register(RegistrationRequest request) {
        ensureNotDuplicate(request, null);
        StudentRegistration registration = new StudentRegistration(clean(request.getStudentName()), clean(request.getCourseName()), clean(request.getEmail()).toLowerCase(), request.getAge());
        return repository.save(registration);
    }
    @Transactional(readOnly = true)
    public List<StudentRegistration> findAll() { return repository.findAll(); }
    @Transactional(readOnly = true)
    public StudentRegistration findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
    }
    @Transactional
    public StudentRegistration update(Long id, RegistrationRequest request) {
        StudentRegistration existing = findById(id);
        ensureNotDuplicate(request, id);
        existing.setStudentName(clean(request.getStudentName()));
        existing.setCourseName(clean(request.getCourseName()));
        existing.setEmail(clean(request.getEmail()).toLowerCase());
        existing.setAge(request.getAge());
        return repository.save(existing);
    }
    @Transactional
    public void delete(Long id) {
        StudentRegistration existing = findById(id);
        repository.delete(existing);
    }
    @Transactional(readOnly = true)
    public List<StudentRegistration> searchByCourse(String course) {
        if (course == null || course.isBlank()) throw new IllegalArgumentException("Course search term cannot be empty");
        return repository.findByCourseNameContainingIgnoreCase(course.trim());
    }
    private void ensureNotDuplicate(RegistrationRequest r, Long currentId) {
        boolean duplicate = currentId == null
            ? repository.existsByEmailIgnoreCaseAndCourseNameIgnoreCase(r.getEmail().trim(), r.getCourseName().trim())
            : repository.existsByEmailIgnoreCaseAndCourseNameIgnoreCaseAndStudentIdNot(r.getEmail().trim(), r.getCourseName().trim(), currentId);
        if (duplicate) throw new DuplicateRegistrationException("This email is already registered for this course");
    }
    private String clean(String value) { return value.trim().replaceAll("\s+", " "); }
}
