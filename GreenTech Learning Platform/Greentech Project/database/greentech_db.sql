CREATE DATABASE IF NOT EXISTS greentech_db;
USE greentech_db;

-- Drop dependent tables first
DROP TABLE IF EXISTS certificates;
DROP TABLE IF EXISTS submissions;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS discussions;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS system_settings;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) DEFAULT '',
    role ENUM('ADMIN', 'INSTRUCTOR', 'LEARNER') NOT NULL DEFAULT 'LEARNER',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. System Settings Table
CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Courses Table
CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(180) NOT NULL,
    category VARCHAR(80) NOT NULL DEFAULT 'Renewable Energy',
    description TEXT NOT NULL,
    content TEXT,
    instructor_id INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Enrollments Table
CREATE TABLE enrollments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    learner_id INT NOT NULL,
    course_id INT NOT NULL,
    progress INT DEFAULT 0,
    status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    UNIQUE KEY unique_enrollment (learner_id, course_id),
    FOREIGN KEY (learner_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 5. Assignments Table
CREATE TABLE assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    max_score INT DEFAULT 100,
    due_date VARCHAR(50) DEFAULT 'Flexible',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 6. Assignment Submissions Table
CREATE TABLE submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id INT NOT NULL,
    learner_id INT NOT NULL,
    submission_text TEXT NOT NULL,
    score INT DEFAULT NULL,
    feedback TEXT DEFAULT NULL,
    status ENUM('SUBMITTED', 'GRADED') DEFAULT 'SUBMITTED',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    graded_at TIMESTAMP NULL,
    FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    FOREIGN KEY (learner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Community Discussions Table
CREATE TABLE discussions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NULL,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    parent_id INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 8. Certificates Table
CREATE TABLE certificates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    learner_id INT NOT NULL,
    course_id INT NOT NULL,
    certificate_code VARCHAR(50) UNIQUE NOT NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (learner_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Seed System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('platform_name', 'GreenLearn Academy', 'Public title of the learning platform'),
('maintenance_mode', 'false', 'Put platform into maintenance mode (true/false)'),
('allow_registration', 'true', 'Allow public visitor self-registration'),
('default_enrollment_cap', '100', 'Maximum learner capacity per course batch'),
('contact_email', 'support@greenlearn.com', 'Helpdesk inquiry contact address');

-- Seed Initial Users (password is admin123, instructor123, learner123)
-- Both plain-text fallback and secure SHA-256 hashed passwords are supported by PasswordUtil
INSERT INTO users(name, email, password_hash, salt, role, status) VALUES
('System Administrator', 'admin@greenlearn.com', 'admin123', 'static-salt-adm', 'ADMIN', 'ACTIVE'),
('Dr. Elena Green', 'instructor@greenlearn.com', 'instructor123', 'static-salt-ins', 'INSTRUCTOR', 'ACTIVE'),
('Alex Chen (Learner)', 'learner@greenlearn.com', 'learner123', 'static-salt-lrn', 'LEARNER', 'ACTIVE');

-- Seed Courses across various Green Technology categories
INSERT INTO courses(title, category, description, content, instructor_id, status) VALUES
('Solar Energy & Photovoltaic Systems', 'Renewable Energy', 'Comprehensive study of solar cell physics, photovoltaic module installation, inverter systems, and grid integration.', 'Module 1: Introduction to Photovoltaics\nModule 2: Solar Irradiance and Panel Efficiency\nModule 3: Battery Storage Systems\nModule 4: Grid-Tied vs Off-Grid Systems', 2, 'APPROVED'),
('Circular Economy & Waste Valorization', 'Waste Management', 'Strategies for industrial waste reduction, recycling paradigms, upcycling techniques, and closed-loop material cycles.', 'Module 1: Linear vs Circular Economic Models\nModule 2: Industrial Symbiosis\nModule 3: Organic Waste to Bioenergy\nModule 4: Zero-Waste Manufacturing', 2, 'APPROVED'),
('Green Building Design & LEED Certification', 'Sustainable Architecture', 'Explore energy-efficient HVAC, passive solar architectural principles, green roofing, and global environmental standards.', 'Module 1: Passive Solar Architecture\nModule 2: Embodied Carbon in Building Materials\nModule 3: Natural Ventilation & Thermal Mass\nModule 4: LEED & BREEAM Standards', 2, 'APPROVED'),
('Wind Turbine Engineering & Aerodynamics', 'Renewable Energy', 'Aerodynamic blade design, wind farm siting analysis, offshore wind installations, and grid stabilization.', 'Module 1: Wind Resource Assessment\nModule 2: Betz Limit and Blade Design\nModule 3: Offshore vs Onshore Installation\nModule 4: Maintenance and Life Cycle', 2, 'PENDING');

-- Seed Enrollments
INSERT INTO enrollments(learner_id, course_id, progress, status, completed_at) VALUES
(3, 1, 100, 'COMPLETED', NOW()),
(3, 2, 60, 'ACTIVE', NULL);

-- Seed Certificates (for the completed course)
INSERT INTO certificates(learner_id, course_id, certificate_code, issue_date) VALUES
(3, 1, 'GL-2026-SOLAR-7849', NOW());

-- Seed Assignments
INSERT INTO assignments(course_id, title, description, max_score, due_date) VALUES
(1, 'Solar PV Sizing Calculation', 'Calculate the kilowatt-peak rating and required panel array area for a 5-person residential home consuming 25 kWh per day.', 100, '2026-11-15'),
(2, 'Zero-Waste Life Cycle Assessment', 'Analyze an everyday consumer product packaging material and outline a closed-loop recycling workflow.', 100, '2026-11-30');

-- Seed Submissions
INSERT INTO submissions(assignment_id, learner_id, submission_text, score, feedback, status, submitted_at, graded_at) VALUES
(1, 3, 'Estimated solar irradiance at 4.8 peak sun hours. Total array requirement is 6.5 kWp using sixteen 410W monocrystalline panels with a 10kWh LiFePO4 battery pack.', 95, 'Excellent mathematical analysis and realistic derating factors applied. Great job!', 'GRADED', NOW(), NOW()),
(2, 3, 'Proposal to switch HDPE milk jugs into 100% rHDPE recycled pellets with chemical sterilization cycle.', NULL, NULL, 'SUBMITTED', NOW(), NULL);

-- Seed Community Discussions
INSERT INTO discussions(course_id, user_id, title, message, parent_id) VALUES
(1, 3, 'Inverter Efficiency in High Temperature Climates', 'Does ambient temperature above 40°C significantly degrade micro-inverter conversion efficiency?', NULL),
(1, 2, 'Re: Inverter Efficiency in High Temperature Climates', 'Yes Alex, silicon inverter electronics experience thermal derating typically past 45°C. Good thermal dissipation heatsinks are vital.', 1),
(NULL, 3, 'Best Books for Green Hydrogen Storage', 'Can anyone recommend introductory textbooks for electrochemical hydrogen generation and transport?', NULL);