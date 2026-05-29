-- Reset TVPSShub WITHOUT DROP DATABASE (fixes phpMyAdmin #1010 errno 41).
-- Use in phpMyAdmin: select database TVPSShub, open SQL tab, paste all, Execute.
-- WARNING: Deletes all data in these tables.

USE TVPSShub;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS resource;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS schools;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- USERS TABLE
-- =========================
CREATE TABLE users (
    id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_of_birth DATE DEFAULT NULL,
    school VARCHAR(100) DEFAULT NULL,
    school_id BIGINT(20) DEFAULT NULL,
    identity_card_number VARCHAR(20) NOT NULL UNIQUE,
    role INT(11) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX (school_id)
) ENGINE=InnoDB;

-- =========================
-- SCHOOLS TABLE
-- =========================
CREATE TABLE schools (
    id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    district VARCHAR(100) DEFAULT NULL,
    representative VARCHAR(100) DEFAULT NULL,
    code VARCHAR(50) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    postcode VARCHAR(20) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    state VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(30) DEFAULT NULL,
    studio TINYINT(1) NOT NULL DEFAULT 0,
    school_recording TINYINT(1) NOT NULL DEFAULT 0,
    upload_youtube TINYINT(1) NOT NULL DEFAULT 0,
    recording TINYINT(1) NOT NULL DEFAULT 0,
    collaborate TINYINT(1) NOT NULL DEFAULT 0,
    greenscreen TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- =========================
-- ACTIVITY TABLE
-- =========================
CREATE TABLE activity (
    id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    organizer VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT NULL,
    date DATE DEFAULT NULL,
    venue VARCHAR(255) DEFAULT NULL,
    district VARCHAR(255) DEFAULT NULL,
    targetLanguage VARCHAR(100) DEFAULT NULL,
    competitionLevel VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    fileUpload VARCHAR(255) DEFAULT NULL,
    programDuration INT(11) DEFAULT NULL,
    participants_primary INT(11) DEFAULT 0,
    participants_secondary INT(11) DEFAULT 0,
    participants_open INT(11) DEFAULT 0,
    creator_id BIGINT(20) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX (creator_id)
) ENGINE=InnoDB;

-- =========================
-- FEEDBACK TABLE
-- =========================
CREATE TABLE feedback (
    feedback_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    activity_id INT(11) NOT NULL,
    user_id BIGINT(20) NOT NULL,
    feedback_text TEXT NOT NULL,
    rating INT(11) DEFAULT NULL,
    date DATE NOT NULL,
    INDEX (activity_id),
    INDEX (user_id),
    CONSTRAINT fk_feedback_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================
-- RESOURCE TABLE
-- =========================
CREATE TABLE resource (
    id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request VARCHAR(255) NOT NULL,
    school VARCHAR(255) DEFAULT NULL,
    state VARCHAR(100) DEFAULT NULL,
    updated_date DATE DEFAULT (CURRENT_DATE),
    level INT(11) DEFAULT NULL,
    type VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    reply TEXT DEFAULT NULL
) ENGINE=InnoDB;
