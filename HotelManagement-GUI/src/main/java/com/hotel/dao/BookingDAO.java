package com.hotel.dao;

import com.hotel.model.Booking;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, " +
                    "total_amount, status, payment_status, special_requests, booking_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            stmt.setBigDecimal(5, booking.getTotalAmount());
            stmt.setString(6, booking.getStatus().name());
            stmt.setString(7, booking.getPaymentStatus().name());
            stmt.setString(8, booking.getSpecialRequests());
            stmt.setDate(9, Date.valueOf(booking.getBookingDate()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, r.room_number, r.room_type " +
                    "FROM bookings b " +
                    "JOIN users u ON b.customer_id = u.id " +
                    "JOIN rooms r ON b.room_id = r.id " +
                    "WHERE b.customer_id = ? " +
                    "ORDER BY b.booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, r.room_number, r.room_type " +
                    "FROM bookings b " +
                    "JOIN users u ON b.customer_id = u.id " +
                    "JOIN rooms r ON b.room_id = r.id " +
                    "ORDER BY b.booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean updateBookingStatus(int bookingId, Booking.BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setCustomerId(rs.getInt("customer_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        booking.setPaymentStatus(Booking.PaymentStatus.valueOf(rs.getString("payment_status")));
        booking.setSpecialRequests(rs.getString("special_requests"));
        booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
        
        // Additional display fields
        booking.setCustomerName(rs.getString("first_name") + " " + rs.getString("last_name"));
        booking.setRoomNumber(rs.getString("room_number"));
        booking.setRoomType(rs.getString("room_type"));
        
        return booking;
    }
}