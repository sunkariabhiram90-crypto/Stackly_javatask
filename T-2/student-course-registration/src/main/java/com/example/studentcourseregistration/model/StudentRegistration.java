package com.example.studentcourseregistration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_registrations", indexes = {
    @Index(name = "idx_registration_email", columnList = "email"),
    @Index(name = "idx_registration_course", columnList = "course_name")
})
public class StudentRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "student_name", nullable = false, length = 80)
    private String studentName;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    void onCreate() { registeredAt = LocalDateTime.now(); }

    public StudentRegistration() {}
    public StudentRegistration(String studentName, String courseName, String email, Integer age) {
        this.studentName = studentName; this.courseName = courseName; this.email = email; this.age = age;
    }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
