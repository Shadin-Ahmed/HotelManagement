package com.hotel.gui.customer;

import com.hotel.model.Customer;
import com.hotel.gui.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hotel Management System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Navigation sidebar
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Main content area
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Add different panels
        mainPanel.add(new RoomSearchPanel(customer), "SEARCH");
        mainPanel.add(new MyBookingsPanel(customer), "BOOKINGS");
        mainPanel.add(new BookingPanel(customer), "NEW_BOOKING");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Show room search by default
        cardLayout.show(mainPanel, "SEARCH");
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Welcome, " + customer.getFullName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel("Customer Dashboard");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(200, 200, 200));
        
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textPanel.setBackground(new Color(52, 73, 94));
        textPanel.add(titleLabel);
        textPanel.add(Box.createHorizontalStrut(20));
        textPanel.add(roleLabel);
        
        JButton logoutButton = new CustomButton("Logout", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Return to login screen
                dispose();
                // You would typically show the login frame here
            }
        });
        
        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(44, 62, 80));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        String[] menuItems = {"Search Rooms", "My Bookings", "New Booking"};
        String[] cardNames = {"SEARCH", "BOOKINGS", "NEW_BOOKING"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuItems[i]);
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(180, 40));
            menuButton.setBackground(new Color(52, 152, 219));
            menuButton.setForeground(Color.WHITE);
            menuButton.setFocusPainted(false);
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            final String cardName = cardNames[i];
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, cardName);
                }
            });
            
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        return sidebarPanel;
    }
}