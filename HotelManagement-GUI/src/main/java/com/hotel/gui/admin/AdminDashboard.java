package com.hotel.gui.admin;

import com.hotel.model.Admin;
import com.hotel.gui.components.CustomButton;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private Admin admin;
    
    public AdminDashboard(Admin admin) {
        this.admin = admin;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hotel Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Room Management", new RoomManagementPanel());
        tabbedPane.addTab("Booking Management", new BookingManagementPanel());
        tabbedPane.addTab("Customer Management", new CustomerManagementPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Admin Dashboard - Welcome, " + admin.getFullName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new CustomButton("Logout", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
}