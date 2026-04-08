-- Create database if not exists
CREATE DATABASE IF NOT EXISTS plant_db;
USE plant_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    dob DATE,
    age INT,
    gender VARCHAR(20),
    mode_preference VARCHAR(10) DEFAULT 'LIGHT'
);

-- Plant info table
CREATE TABLE IF NOT EXISTS plant_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    species_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    care_instructions TEXT,
    toxicity_warning VARCHAR(255)
);

-- Insert dummy admin user
-- Password is 'password123'
INSERT IGNORE INTO users (username, password, name, dob, age, gender, mode_preference)
VALUES ('admin', 'password123', 'John Doe', '1995-05-15', 30, 'Male', 'LIGHT');

-- Insert some dummy plant entries
INSERT IGNORE INTO plant_info (species_name, description, care_instructions, toxicity_warning)
VALUES 
    ('Aloe Vera', 'A succulent plant species of the genus Aloe.', 'Water every 3 weeks. Keep in bright, indirect sunlight.', 'Mildly toxic to pets.'),
    ('Snake plant (Sanseviera)', 'Known for its stiff, upright leaves.', 'Low water needed. Can survive in low light.', 'Toxic to cats and dogs.'),
    ('Monstera Deliciosa (Monstera deliciosa)', 'Tropical plant with iconic split leaves.', 'Water moderately. Needs humidity.', 'Toxic to pets.');
