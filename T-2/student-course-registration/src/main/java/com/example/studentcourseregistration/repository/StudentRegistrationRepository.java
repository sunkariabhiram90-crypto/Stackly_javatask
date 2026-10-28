package com.example.studentcourseregistration.repository;

import com.example.studentcourseregistration.model.StudentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, Long> {
    boolean existsByEmailIgnoreCaseAndCourseNameIgnoreCase(String email, String courseName);
    boolean existsByEmailIgnoreCaseAndCourseNameIgnoreCaseAndStudentIdNot(String email, String courseName, Long studentId);
    List<StudentRegistration> findByCourseNameContainingIgnoreCase(String courseName);
}
