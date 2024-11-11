-- Description: SQL script for creating the database and tables for the Smart Door Lock project

-- Create database and use it
CREATE DATABASE IF NOT EXISTS smartdoorlock CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartdoorlock;

-- Create table for storing the default key
CREATE TABLE IF NOT EXISTS default_key (
    id INT PRIMARY KEY CHECK (id = 1), -- Only one default key is allowed
    key_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create table for storing valid card IDs
CREATE TABLE IF NOT EXISTS valid_card_id (
    id INT PRIMARY KEY CHECK (id = 1), -- Only one valid card ID is allowed
    card_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create table for storing door access logs
CREATE TABLE IF NOT EXISTS door_access_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    access_type ENUM('KEYPAD', 'RFID', 'APP') NOT NULL,
    door_status ENUM('OPEN', 'CLOSED') NOT NULL,
    access_result ENUM('GRANTED', 'DENIED', 'TIMEOUT') NOT NULL,
    access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial default key
INSERT INTO default_key (id, key_value) VALUES (1, '1357')
ON DUPLICATE KEY UPDATE key_value = '1357';

-- Insert initial valid card ID
INSERT INTO valid_card_id (id, card_id) VALUES (1, '23 35 5A ED')
ON DUPLICATE KEY UPDATE card_id = '23 35 5A ED';
