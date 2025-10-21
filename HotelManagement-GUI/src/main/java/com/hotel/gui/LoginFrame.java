package com.hotel.gui;

import com.hotel.model.Admin;
import com.hotel.model.Customer;
import com.hotel.dao.CustomerDAO;
import com.hotel.gui.admin.AdminDashboard;
import com.hotel.gui.customer.CustomerDashboard;
import com.hotel.gui.components.CustomButton;
import com.hotel.gui.components.CustomTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JButton loginButton, registerButton;
    private CustomerDAO customerDAO;
    
    public LoginFrame() {
        customerDAO = new CustomerDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Hotel Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 15, 5);
        
        // User Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("User Type:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        userTypeCombo = new JComboBox<>(new String[]{"Customer", "Admin"});
        userTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(userTypeCombo, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        emailField = new CustomTextField(20);
        formPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        loginButton = new CustomButton("Login", new Color(52, 152, 219));
        registerButton = new CustomButton("Register", new Color(46, 204, 113));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        formPanel.add(buttonPanel, gbc);
        
        // Add components to main panel
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationDialog();
            }
        });
        
        // Enter key listener for login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if ("Customer".equals(userType)) {
            Customer customer = customerDAO.authenticate(email, password);
            if (customer != null) {
                openCustomerDashboard(customer);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Simple admin authentication (in real app, use proper authentication)
            if ("admin@hotel.com".equals(email) && "admin123".equals(password)) {
                Admin admin = new Admin(email, password, "System", "Administrator", "");
                openAdminDashboard(admin);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showRegistrationDialog() {
        JDialog registerDialog = new JDialog(this, "Customer Registration", true);
        registerDialog.setSize(400, 500);
        registerDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        
        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");
        
        registerBtn.addActionListener(e -> {
            // Registration logic here
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Customer customer = new Customer(
                emailField.getText(),
                password,
                firstNameField.getText(),
                lastNameField.getText(),
                phoneField.getText(),
                addressField.getText()
            );
            
            if (customerDAO.registerCustomer(customer)) {
                JOptionPane.showMessageDialog(registerDialog, "Registration successful! Please login.");
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> registerDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);
        
        registerDialog.add(panel, BorderLayout.CENTER);
        registerDialog.add(buttonPanel, BorderLayout.SOUTH);
        registerDialog.setVisible(true);
    }
    
    private void openCustomerDashboard(Customer customer) {
        CustomerDashboard dashboard = new CustomerDashboard(customer);
        dashboard.setVisible(true);
        this.dispose();
    }
    
    private void openAdminDashboard(Admin admin) {
        AdminDashboard dashboard = new AdminDashboard(admin);
        dashboard.setVisible(true);
        this.dispose();
    }
}