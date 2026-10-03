package com;

public class Prescription {
    private int prescriptionId;
    private int appointmentId;
    private String diagnosis;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;

    public Prescription(int prescriptionId, int appointmentId, String diagnosis, String medicationName,
                        String dosage, String frequency, String duration, String notes) {
        this.prescriptionId = prescriptionId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
    }
    public int getPrescriptionId() { return prescriptionId; }
    public int getAppointmentId() { return appointmentId; }
    public String getDiagnosis() { return diagnosis; }
    public String getMedicationName() { return medicationName; }
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public String getDuration() { return duration; }
    public String getNotes() { return notes; }
}
