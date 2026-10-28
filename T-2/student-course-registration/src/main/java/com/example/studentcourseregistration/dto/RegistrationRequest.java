package com.example.studentcourseregistration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {
    @NotBlank(message = "Student name cannot be empty")
    @Size(max = 80, message = "Student name must not exceed 80 characters")
    private String studentName;

    @NotBlank(message = "Course name cannot be empty")
    @Size(max = 100, message = "Course name must not exceed 100 characters")
    private String courseName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Enter valid email")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
