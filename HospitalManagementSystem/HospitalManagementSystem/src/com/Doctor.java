package com;

import java.math.BigDecimal;

public class Doctor {
    private int doctorId;
    private String fullName;
    private String specialization;
    private String phone;
    private String email;
    private BigDecimal consultationFee;
    private String availabilityStatus;

    public Doctor(int doctorId, String fullName, String specialization, String phone,
                  String email, BigDecimal consultationFee, String availabilityStatus) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.consultationFee = consultationFee;
        this.availabilityStatus = availabilityStatus;
    }
    public int getDoctorId() { return doctorId; }
    public String getFullName() { return fullName; }
    public String getSpecialization() { return specialization; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public String getAvailabilityStatus() { return availabilityStatus; }
}
