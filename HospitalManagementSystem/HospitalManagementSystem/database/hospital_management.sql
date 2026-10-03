CREATE DATABASE IF NOT EXISTS hospital_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_management;

CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE','FEMALE','OTHER') NOT NULL,
    phone VARCHAR(10) NOT NULL UNIQUE,
    email VARCHAR(254) NULL UNIQUE,
    address VARCHAR(250) NOT NULL,
    blood_group ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-') NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patients_name (full_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    consultation_fee DECIMAL(10,2) NOT NULL,
    availability_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_doctors_specialization (specialization),
    CONSTRAINT chk_doctor_fee CHECK (consultation_fee >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(250) NOT NULL,
    status ENUM('BOOKED','CONFIRMED','COMPLETED','CANCELLED','NO_SHOW') NOT NULL DEFAULT 'BOOKED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_appointments_patient (patient_id),
    INDEX idx_appointments_doctor_slot (doctor_id, appointment_date, appointment_time),
    INDEX idx_appointments_date_status (appointment_date, status),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id)
      REFERENCES patients(patient_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id)
      REFERENCES doctors(doctor_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL,
    diagnosis VARCHAR(500) NOT NULL,
    medication_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(150) NOT NULL,
    frequency VARCHAR(150) NOT NULL,
    duration VARCHAR(150) NOT NULL,
    notes VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prescriptions_appointment (appointment_id),
    CONSTRAINT fk_prescriptions_appointment FOREIGN KEY (appointment_id)
      REFERENCES appointments(appointment_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- OPTIONAL DEMO DATA: Skip this section if you want to register
-- all records through the Java application.
-- Unique emails and phone numbers make this suitable for a fresh database.

-- 1. SAMPLE PATIENTS
INSERT INTO patients
(full_name, date_of_birth, gender, phone, email, address, blood_group)
VALUES
('Arun Kumar', '1998-05-12', 'MALE', '9876543210',
 'arun@example.com', 'Anna Nagar, Chennai', 'O+'),

('Priya Sharma', '2000-08-21', 'FEMALE', '9876543211',
 'priya@example.com', 'Velachery, Chennai', 'A+'),

('Rahul Reddy', '1995-11-03', 'MALE', '9876543212',
 'rahul@example.com', 'T Nagar, Chennai', 'B+');


-- 2. SAMPLE DOCTORS
INSERT INTO doctors
(full_name, specialization, phone, email, consultation_fee, availability_status)
VALUES
('Dr. Ravi Kumar', 'Cardiology', '9876543213',
 'ravi.doctor@example.com', 800.00, 'ACTIVE'),

('Dr. Suresh Rao', 'General Medicine', '9876543214',
 'suresh.doctor@example.com', 500.00, 'ACTIVE'),

('Dr. Lakshmi Devi', 'Dermatology', '9876543215',
 'lakshmi.doctor@example.com', 600.00, 'ACTIVE');


-- 3. SAMPLE APPOINTMENTS
-- Dates are generated relative to the current date.
-- Each appointment is assigned to an existing patient and doctor.

INSERT INTO appointments
(patient_id, doctor_id, appointment_date, appointment_time, reason, status)
SELECT p.patient_id, d.doctor_id,
       DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00',
       'Routine heart checkup', 'BOOKED'
FROM patients p
CROSS JOIN doctors d
WHERE p.email = 'arun@example.com'
  AND d.email = 'ravi.doctor@example.com';

INSERT INTO appointments
(patient_id, doctor_id, appointment_date, appointment_time, reason, status)
SELECT p.patient_id, d.doctor_id,
       DATE_ADD(CURDATE(), INTERVAL 2 DAY), '11:00:00',
       'General health consultation', 'CONFIRMED'
FROM patients p
CROSS JOIN doctors d
WHERE p.email = 'priya@example.com'
  AND d.email = 'suresh.doctor@example.com';

INSERT INTO appointments
(patient_id, doctor_id, appointment_date, appointment_time, reason, status)
SELECT p.patient_id, d.doctor_id,
       DATE_SUB(CURDATE(), INTERVAL 1 DAY), '15:00:00',
       'Skin allergy consultation', 'COMPLETED'
FROM patients p
CROSS JOIN doctors d
WHERE p.email = 'rahul@example.com'
  AND d.email = 'lakshmi.doctor@example.com';


-- 4. SAMPLE PRESCRIPTION
-- Prescription is linked to Rahul's completed appointment.

INSERT INTO prescriptions
(appointment_id, diagnosis, medication_name, dosage, frequency, duration, notes)
SELECT a.appointment_id,
       'Skin allergy',
       'Cetirizine',
       'As prescribed by the doctor',
       'As prescribed by the doctor',
       'As prescribed by the doctor',
       'Follow the treating clinician instructions.'
FROM appointments a
JOIN patients p ON p.patient_id = a.patient_id
JOIN doctors d ON d.doctor_id = a.doctor_id
WHERE p.email = 'rahul@example.com'
  AND d.email = 'lakshmi.doctor@example.com'
  AND a.status = 'COMPLETED';
