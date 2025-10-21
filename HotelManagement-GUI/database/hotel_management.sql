-- Create database
CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- Users table (for both customers and admins)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    user_type ENUM('CUSTOMER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    description TEXT,
    amenities VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'REFUNDED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    special_requests TEXT,
    booking_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Insert sample data
INSERT INTO users (email, password, first_name, last_name, phone, address, user_type) VALUES
('admin@hotel.com', 'admin123', 'System', 'Administrator', '123-456-7890', NULL, 'ADMIN'),
('john.doe@email.com', 'password123', 'John', 'Doe', '555-0101', '123 Main St, City, State', 'CUSTOMER'),
('jane.smith@email.com', 'password123', 'Jane', 'Smith', '555-0102', '456 Oak Ave, City, State', 'CUSTOMER');

INSERT INTO rooms (room_number, room_type, price_per_night, description, amenities) VALUES
('101', 'SINGLE', 99.99, 'Cozy single room with city view', 'WiFi, TV, Air Conditioning'),
('102', 'SINGLE', 99.99, 'Comfortable single room', 'WiFi, TV, Mini Fridge'),
('201', 'DOUBLE', 149.99, 'Spacious double room', 'WiFi, TV, Air Conditioning, Mini Bar'),
('202', 'DOUBLE', 149.99, 'Double room with balcony', 'WiFi, TV, Air Conditioning, Balcony'),
('301', 'SUITE', 299.99, 'Luxury suite with living area', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi'),
('302', 'DELUXE', 199.99, 'Deluxe room with premium amenities', 'WiFi, TV, Air Conditioning, Coffee Maker, Safe');

INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, total_amount, status, payment_status, special_requests, booking_date) VALUES
(2, 1, '2024-01-15', '2024-01-18', 299.97, 'CONFIRMED', 'PAID', 'Early check-in requested', '2024-01-10'),
(3, 3, '2024-01-20', '2024-01-25', 749.95, 'PENDING', 'PENDING', 'Honeymoon suite', '2024-01-12');