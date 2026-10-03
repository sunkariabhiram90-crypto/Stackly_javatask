CREATE DATABASE IF NOT EXISTS complaint_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE complaint_management;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone CHAR(10) NOT NULL UNIQUE,
    address VARCHAR(250) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_name CHECK (CHAR_LENGTH(TRIM(full_name)) >= 2)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS officers (
    officer_id INT PRIMARY KEY AUTO_INCREMENT,
    officer_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone CHAR(10) NOT NULL UNIQUE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_officer_name CHECK (CHAR_LENGTH(TRIM(officer_name)) >= 2)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS complaints (
    complaint_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    officer_id INT NULL,
    complaint_type VARCHAR(40) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    location VARCHAR(250) NOT NULL,
    priority ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('REGISTERED','ASSIGNED','IN_PROGRESS','RESOLVED','REJECTED') NOT NULL DEFAULT 'REGISTERED',
    resolution_details VARCHAR(2000) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_complaint_user FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_officer FOREIGN KEY (officer_id) REFERENCES officers(officer_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_complaint_type CHECK (CHAR_LENGTH(TRIM(complaint_type)) >= 2),
    INDEX idx_complaints_user (user_id),
    INDEX idx_complaints_officer (officer_id),
    INDEX idx_complaints_status (status),
    INDEX idx_complaints_priority (priority),
    INDEX idx_complaints_created (created_at)
) ENGINE=InnoDB;

-- OPTIONAL DEMO DATA: skip this section if you want to register all records through the Java menu.
-- Unique emails and phone numbers make this script intended for a fresh/empty database.
INSERT INTO users (full_name,email,phone,address) VALUES
('Arun Kumar','arun@example.com','9876543210','Anna Nagar, Chennai'),
('Priya Sharma','priya@example.com','9876543211','Velachery, Chennai'),
('Rahul Reddy','rahul@example.com','9876543212','T Nagar, Chennai');

INSERT INTO officers (officer_name,department,email,phone,is_available) VALUES
('Ravi Kumar','Road Maintenance','ravi.officer@example.com','9876543213',TRUE),
('Suresh Rao','Water Supply','suresh.officer@example.com','9876543214',TRUE),
('Lakshmi Devi','Sanitation','lakshmi.officer@example.com','9876543215',TRUE);

INSERT INTO complaints (user_id,complaint_type,description,location,priority,status)
SELECT user_id,'Road damage','Large pothole near the main road.','Anna Nagar, Chennai','HIGH','REGISTERED'
FROM users WHERE email='arun@example.com';

INSERT INTO complaints (user_id,complaint_type,description,location,priority,status)
SELECT user_id,'Water supply','Water supply has been irregular for three days.','Velachery, Chennai','MEDIUM','REGISTERED'
FROM users WHERE email='priya@example.com';

INSERT INTO complaints (user_id,complaint_type,description,location,priority,status)
SELECT user_id,'Garbage collection','Garbage has not been collected for several days.','T Nagar, Chennai','HIGH','REGISTERED'
FROM users WHERE email='rahul@example.com';

-- Useful verification queries:
-- SELECT * FROM users;
-- SELECT * FROM officers;
-- SELECT * FROM complaints;
