-- Create database and user
CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- Create user (replace 'password' with your actual password)
CREATE USER IF NOT EXISTS 'hotel_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON hotel_management.* TO 'hotel_user'@'localhost';
FLUSH PRIVILEGES;

-- Create tables
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    description TEXT,
    amenities VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    special_requests TEXT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Insert sample data
INSERT INTO rooms (room_number, room_type, price_per_night, description, amenities) VALUES
('101', 'SINGLE', 99.99, 'Cozy single room with city view', 'WiFi, TV, Air Conditioning'),
('102', 'SINGLE', 99.99, 'Comfortable single room', 'WiFi, TV, Mini Fridge'),
('201', 'DOUBLE', 149.99, 'Spacious double room', 'WiFi, TV, Air Conditioning, Mini Bar'),
('202', 'DOUBLE', 149.99, 'Double room with balcony', 'WiFi, TV, Air Conditioning, Balcony'),
('301', 'SUITE', 299.99, 'Luxury suite with living area', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi'),
('302', 'DELUXE', 199.99, 'Deluxe room with premium amenities', 'WiFi, TV, Air Conditioning, Coffee Maker, Safe');

INSERT INTO customers (first_name, last_name, email, phone) VALUES
('John', 'Doe', 'john.doe@email.com', '555-0101'),
('Jane', 'Smith', 'jane.smith@email.com', '555-0102'),
('Mike', 'Johnson', 'mike.johnson@email.com', '555-0103');

INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, total_amount, status) VALUES
(1, 1, '2024-01-15', '2024-01-18', 299.97, 'CONFIRMED'),
(2, 3, '2024-01-20', '2024-01-25', 749.95, 'PENDING');