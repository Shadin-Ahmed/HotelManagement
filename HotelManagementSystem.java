import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HotelManagementSystem extends JFrame {
    private Connection connection;
    private DefaultTableModel customersModel, roomsModel, bookingsModel;
    private JTable customersTable, roomsTable, bookingsTable;
    
    // Colors for modern UI
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color CARD_COLOR = Color.WHITE;
    
    // Configuration option for ID behavior
    private boolean reuseDeletedIds = false; // Set to true if you want to reuse deleted IDs
    
    public HotelManagementSystem() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        initializeStyles();
        testDatabaseConnection();
        initializeUI();
    }
    
    private void initializeStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void testDatabaseConnection() {
        // Try different connection methods
        String[] urls = {
            "jdbc:mysql://localhost:3306/hotel_management",
            "jdbc:mysql://localhost:3306/hotel_management?useSSL=false",
            "jdbc:mysql://localhost:3306/hotel_management?useSSL=false&serverTimezone=UTC",
            "jdbc:mysql://127.0.0.1:3306/hotel_management?useSSL=false"
        };
        
        String[] usernames = {"root", "hotel_user"};
        String[] passwords = {"", "password", "root", "root123", "1234"};
        
        for (String url : urls) {
            for (String username : usernames) {
                for (String password : passwords) {
                    if (tryConnection(url, username, password)) {
                        showSuccessMessage("Database Connected Successfully!", 
                            "Connected to: " + url + "\nUsername: " + username);
                        return;
                    }
                }
            }
        }
        
        showErrorMessage("Database Connection Failed", 
            "Could not connect to database!\n\n" +
            "Starting in demo mode without database...");
    }
    
    private boolean tryConnection(String url, String username, String password) {
        try {
            System.out.println("Trying: " + url + " with user: " + username);
            Connection conn = DriverManager.getConnection(url, username, password);
            this.connection = conn;
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE 'hotel_management'");
            if (rs.next()) {
                System.out.println("Database found!");
                stmt.execute("USE hotel_management");
                return true;
            } else {
                System.out.println("Database not found, creating it...");
                stmt.execute("CREATE DATABASE IF NOT EXISTS hotel_management");
                stmt.execute("USE hotel_management");
                createTables();
                insertSampleData();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        
        String roomsTable = "CREATE TABLE IF NOT EXISTS rooms (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "room_number VARCHAR(10) UNIQUE NOT NULL, " +
            "room_type VARCHAR(20) NOT NULL, " +
            "price_per_night DECIMAL(10,2) NOT NULL, " +
            "amenities VARCHAR(500), " +
            "is_available BOOLEAN DEFAULT TRUE, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        String customersTable = "CREATE TABLE IF NOT EXISTS customers (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "first_name VARCHAR(100) NOT NULL, " +
            "last_name VARCHAR(100) NOT NULL, " +
            "email VARCHAR(255), " +
            "phone VARCHAR(20), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_id INT, " +
            "room_id INT, " +
            "check_in_date DATE, " +
            "check_out_date DATE, " +
            "total_amount DECIMAL(10,2), " +
            "status VARCHAR(20) DEFAULT 'PENDING', " +
            "special_requests TEXT, " +
            "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        stmt.execute(roomsTable);
        stmt.execute(customersTable);
        stmt.execute(bookingsTable);
        System.out.println("Tables created successfully!");
    }
    
    private void insertSampleData() throws SQLException {
        Statement stmt = connection.createStatement();
        
        // Clear existing data first
        stmt.execute("DELETE FROM bookings");
        stmt.execute("DELETE FROM customers");
        stmt.execute("DELETE FROM rooms");
        
        // Reset auto increment counters
        stmt.execute("ALTER TABLE customers AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE rooms AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE bookings AUTO_INCREMENT = 1");
        
        // Insert sample rooms
        stmt.execute("INSERT INTO rooms (room_number, room_type, price_per_night, amenities) VALUES " +
            "('101', 'SINGLE', 99.99, 'WiFi, TV, AC, Mini Fridge'), " +
            "('102', 'SINGLE', 89.99, 'WiFi, TV, AC'), " +
            "('201', 'DOUBLE', 149.99, 'WiFi, TV, AC, Mini Bar, Balcony'), " +
            "('202', 'DOUBLE', 139.99, 'WiFi, TV, AC, Mini Bar'), " +
            "('301', 'SUITE', 299.99, 'WiFi, TV, AC, Jacuzzi, Mini Bar, Living Area'), " +
            "('302', 'DELUXE', 199.99, 'WiFi, TV, AC, Coffee Maker, Safe, Balcony')");
        
        // Insert sample customers
        stmt.execute("INSERT INTO customers (first_name, last_name, email, phone) VALUES " +
            "('John', 'Doe', 'john.doe@email.com', '+1-555-0101'), " +
            "('Jane', 'Smith', 'jane.smith@email.com', '+1-555-0102'), " +
            "('Mike', 'Johnson', 'mike.johnson@email.com', '+1-555-0103'), " +
            "('Sarah', 'Wilson', 'sarah.wilson@email.com', '+1-555-0104')");
        
        // Insert sample bookings
        stmt.execute("INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, total_amount, status) VALUES " +
            "(1, 1, '2024-02-01', '2024-02-05', 399.96, 'CONFIRMED'), " +
            "(2, 3, '2024-02-10', '2024-02-15', 749.95, 'CONFIRMED'), " +
            "(3, 2, '2024-01-20', '2024-01-25', 449.95, 'CANCELLED'), " +
            "(4, 4, '2024-02-08', '2024-02-12', 559.96, 'PENDING')");
        
        System.out.println("Sample data inserted!");
    }
    
    private void initializeUI() {
        // Create modern header
        JPanel headerPanel = createHeaderPanel();
        
        // Create main tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if (connection != null) {
            tabbedPane.addTab("Rooms", createRoomsPanel());
            tabbedPane.addTab("Customers", createCustomersPanel());
            tabbedPane.addTab("Bookings", createBookingsPanel());
            tabbedPane.addTab("Dashboard", createDashboardPanel());
        } else {
            tabbedPane.addTab("Rooms (Demo)", createDemoPanel("Rooms Management"));
            tabbedPane.addTab("Customers (Demo)", createDemoPanel("Customers Management"));
            tabbedPane.addTab("Bookings (Demo)", createDemoPanel("Bookings Management"));
            tabbedPane.addTab("System Info", createSystemInfoPanel());
        }
        
        // Set layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        header.setPreferredSize(new Dimension(getWidth(), 80));
        
        // Title
        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Status indicator
        JLabel statusLabel = new JLabel(connection != null ? "Connected" : "Demo Mode");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(connection != null ? ACCENT_COLOR : DANGER_COLOR);
        header.add(titleLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        
        return header;
    }
    
    // ==================== ROOMS MANAGEMENT ====================
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createSectionHeader("Room Management", "Manage hotel rooms and availability");
        
        // Table
        String[] columns = {"ID", "Room Number", "Type", "Price/Night", "Amenities", "Status"};
        roomsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomsTable = createStyledTable(roomsModel);
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        JButton addRoomButton = createStyledButton("Add Room", SECONDARY_COLOR, e -> showAddRoomDialog());
        JButton toggleStatusButton = createStyledButton("Toggle Status", ACCENT_COLOR, e -> toggleRoomStatus());
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR, e -> loadRoomsData());
        
        controlPanel.add(addRoomButton);
        controlPanel.add(toggleStatusButton);
        controlPanel.add(refreshButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadRoomsData();
        return panel;
    }
    
    private void loadRoomsData() {
        try {
            roomsModel.setRowCount(0);
            String query = "SELECT * FROM rooms ORDER BY room_number";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String status = rs.getBoolean("is_available") ? "Available" : "Occupied";
                roomsModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    String.format("$%.2f", rs.getDouble("price_per_night")),
                    rs.getString("amenities"),
                    status
                });
            }
        } catch (SQLException e) {
            showError("Error loading rooms: " + e.getMessage());
        }
    }
    
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Add New Room", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JTextField roomNumberField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"SINGLE", "DOUBLE", "SUITE", "DELUXE"});
        JTextField priceField = new JTextField();
        JTextArea amenitiesArea = new JTextArea(3, 20);
        JCheckBox availableCheckbox = new JCheckBox("Available", true);
        
        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Room Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Price/Night:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Amenities:"));
        formPanel.add(new JScrollPane(amenitiesArea));
        formPanel.add(new JLabel("Available:"));
        formPanel.add(availableCheckbox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = createStyledButton("Save Room", ACCENT_COLOR, e -> {
            saveNewRoom(roomNumberField.getText(), (String) typeCombo.getSelectedItem(),
                       priceField.getText(), amenitiesArea.getText(), availableCheckbox.isSelected(), dialog);
        });
        
        JButton cancelButton = createStyledButton("Cancel", DANGER_COLOR, e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void saveNewRoom(String roomNumber, String roomType, String price, String amenities, boolean available, JDialog dialog) {
        if (roomNumber.trim().isEmpty() || price.trim().isEmpty()) {
            showError("Room number and price are required!");
            return;
        }
        
        try {
            double priceValue = Double.parseDouble(price);
            String query = "INSERT INTO rooms (room_number, room_type, price_per_night, amenities, is_available) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, roomNumber.trim());
            stmt.setString(2, roomType);
            stmt.setDouble(3, priceValue);
            stmt.setString(4, amenities.trim().isEmpty() ? null : amenities.trim());
            stmt.setBoolean(5, available);
            
            if (stmt.executeUpdate() > 0) {
                showSuccessMessage("Room Added", "Room " + roomNumber + " added successfully!");
                dialog.dispose();
                loadRoomsData();
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price number!");
        } catch (SQLException e) {
            showError("Error saving room: " + e.getMessage());
        }
    }
    
    private void toggleRoomStatus() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a room to toggle status");
            return;
        }
        
        int roomId = (Integer) roomsModel.getValueAt(selectedRow, 0);
        String roomNumber = (String) roomsModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) roomsModel.getValueAt(selectedRow, 5);
        boolean newStatus = !currentStatus.equals("Available");
        
        try {
            String query = "UPDATE rooms SET is_available = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, roomId);
            
            if (stmt.executeUpdate() > 0) {
                showSuccessMessage("Status Updated", 
                    "Room " + roomNumber + " is now " + (newStatus ? "Available" : "Occupied"));
                loadRoomsData();
            }
        } catch (SQLException e) {
            showError("Error updating room status: " + e.getMessage());
        }
    }
    
    // ==================== CUSTOMERS MANAGEMENT ====================
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createSectionHeader("Customer Management", "Manage customer information and profiles");
        
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Created Date"};
        customersModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        customersTable = createStyledTable(customersModel);
        
        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        JButton addButton = createStyledButton("Add Customer", SECONDARY_COLOR, e -> showAddCustomerDialog());
        JButton editButton = createStyledButton("Edit Selected", ACCENT_COLOR, e -> editSelectedCustomer());
        JButton deleteButton = createStyledButton("Delete Selected", DANGER_COLOR, e -> deleteSelectedCustomer());
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR, e -> loadCustomersData());
        
        // Add option to reset IDs
        JButton resetIdsButton = createStyledButton("Reset ID Sequence", new Color(155, 89, 182), e -> resetCustomerIdSequence());
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        controlPanel.add(resetIdsButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadCustomersData();
        return panel;
    }
    
    private void loadCustomersData() {
        try {
            customersModel.setRowCount(0);
            String query = "SELECT * FROM customers ORDER BY id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            
            while (rs.next()) {
                customersModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email") != null ? rs.getString("email") : "N/A",
                    rs.getString("phone") != null ? rs.getString("phone") : "N/A",
                    dateFormat.format(rs.getTimestamp("created_at"))
                });
            }
        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }
    
    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = createStyledButton("Save Customer", ACCENT_COLOR, e -> {
            saveNewCustomer(firstNameField.getText(), lastNameField.getText(), 
                          emailField.getText(), phoneField.getText(), dialog);
        });
        
        JButton cancelButton = createStyledButton("Cancel", DANGER_COLOR, e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void saveNewCustomer(String firstName, String lastName, String email, String phone, JDialog dialog) {
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            showError("First Name and Last Name are required!");
            return;
        }
        
        try {
            String query = "INSERT INTO customers (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, firstName.trim());
            stmt.setString(2, lastName.trim());
            stmt.setString(3, email.trim().isEmpty() ? null : email.trim());
            stmt.setString(4, phone.trim().isEmpty() ? null : phone.trim());
            
            if (stmt.executeUpdate() > 0) {
                showSuccessMessage("Customer Added", 
                    firstName + " " + lastName + " has been added successfully!");
                dialog.dispose();
                loadCustomersData();
            }
        } catch (SQLException e) {
            showError("Error saving customer: " + e.getMessage());
        }
    }
    
    private void editSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a customer to edit");
            return;
        }
        
        int customerId = (Integer) customersModel.getValueAt(selectedRow, 0);
        String firstName = (String) customersModel.getValueAt(selectedRow, 1);
        String lastName = (String) customersModel.getValueAt(selectedRow, 2);
        String email = (String) customersModel.getValueAt(selectedRow, 3);
        String phone = (String) customersModel.getValueAt(selectedRow, 4);
        
        if (email.equals("N/A")) email = "";
        if (phone.equals("N/A")) phone = "";
        
        showEditCustomerDialog(customerId, firstName, lastName, email, phone);
    }
    
    private void showEditCustomerDialog(int customerId, String currentFirstName, String currentLastName, 
                                      String currentEmail, String currentPhone) {
        JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JTextField firstNameField = new JTextField(currentFirstName);
        JTextField lastNameField = new JTextField(currentLastName);
        JTextField emailField = new JTextField(currentEmail);
        JTextField phoneField = new JTextField(currentPhone);
        
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = createStyledButton("Save Changes", ACCENT_COLOR, e -> {
            updateCustomer(customerId, firstNameField.getText(), lastNameField.getText(), 
                          emailField.getText(), phoneField.getText(), dialog);
        });
        
        JButton cancelButton = createStyledButton("Cancel", DANGER_COLOR, e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void updateCustomer(int customerId, String firstName, String lastName, String email, String phone, JDialog dialog) {
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            showError("First Name and Last Name are required!");
            return;
        }
        
        try {
            String query = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, firstName.trim());
            stmt.setString(2, lastName.trim());
            stmt.setString(3, email.trim().isEmpty() ? null : email.trim());
            stmt.setString(4, phone.trim().isEmpty() ? null : phone.trim());
            stmt.setInt(5, customerId);
            
            if (stmt.executeUpdate() > 0) {
                showSuccessMessage("Customer Updated", "Customer information updated successfully!");
                dialog.dispose();
                loadCustomersData();
            }
        } catch (SQLException e) {
            showError("Error updating customer: " + e.getMessage());
        }
    }
    
    private void deleteSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a customer to delete");
            return;
        }
        
        int customerId = (Integer) customersModel.getValueAt(selectedRow, 0);
        String customerName = (String) customersModel.getValueAt(selectedRow, 1) + " " + customersModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete Customer?\n\n" +
            "Name: " + customerName + "\n" +
            "ID: " + customerId + "\n\n" +
            "This action cannot be undone!",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check for bookings
                String checkBookings = "SELECT COUNT(*) as booking_count FROM bookings WHERE customer_id = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkBookings);
                checkStmt.setInt(1, customerId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt("booking_count") > 0) {
                    showError("Cannot delete customer! This customer has existing bookings.");
                    return;
                }
                
                // Delete customer
                String deleteQuery = "DELETE FROM customers WHERE id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, customerId);
                
                if (deleteStmt.executeUpdate() > 0) {
                    showSuccessMessage("Customer Deleted", customerName + " has been removed from the system.");
                    
                    // Optionally reset auto-increment to reuse IDs
                    if (reuseDeletedIds) {
                        resetCustomerIdSequence();
                    }
                    
                    loadCustomersData();
                }
            } catch (SQLException e) {
                showError("Error deleting customer: " + e.getMessage());
            }
        }
    }
    
    // NEW METHOD: Reset customer ID sequence to fill gaps
    private void resetCustomerIdSequence() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset Customer ID Sequence?\n\n" +
            "This will:\n" +
            "• Reassign all customer IDs sequentially\n" +
            "• Update related bookings\n" +
            "• Fill any gaps in the ID sequence\n\n" +
            "This operation should be done with caution!",
            "Reset ID Sequence", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            // Create temporary table with sequential IDs
            Statement stmt = connection.createStatement();
            
            // Step 1: Create a backup table with new sequential IDs
            stmt.execute("CREATE TEMPORARY TABLE customers_temp AS " +
                        "SELECT ROW_NUMBER() OVER (ORDER BY id) as new_id, " +
                        "first_name, last_name, email, phone, created_at " +
                        "FROM customers ORDER BY id");
            
            // Step 2: Update bookings to reference new IDs
            stmt.execute("UPDATE bookings b " +
                        "JOIN customers c ON b.customer_id = c.id " +
                        "JOIN customers_temp ct ON c.first_name = ct.first_name AND c.last_name = ct.last_name " +
                        "SET b.customer_id = ct.new_id");
            
            // Step 3: Delete old customers and reset auto increment
            stmt.execute("DELETE FROM customers");
            stmt.execute("ALTER TABLE customers AUTO_INCREMENT = 1");
            
            // Step 4: Insert data back with new sequential IDs
            stmt.execute("INSERT INTO customers (first_name, last_name, email, phone, created_at) " +
                        "SELECT first_name, last_name, email, phone, created_at FROM customers_temp");
            
            // Step 5: Drop temporary table
            stmt.execute("DROP TEMPORARY TABLE customers_temp");
            
            // Commit transaction
            connection.commit();
            connection.setAutoCommit(true);
            
            showSuccessMessage("ID Sequence Reset", 
                "Customer IDs have been reset sequentially. All gaps have been filled.");
            loadCustomersData();
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showError("Error resetting ID sequence: " + e.getMessage());
        }
    }
    
    // ==================== BOOKINGS MANAGEMENT ====================
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createSectionHeader("Booking Management", "Manage room bookings and reservations");
        
        String[] columns = {"ID", "Customer", "Room", "Check-in", "Check-out", "Amount", "Status"};
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingsTable = createStyledTable(bookingsModel);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        JButton newBookingButton = createStyledButton("New Booking", SECONDARY_COLOR, e -> showNewBookingDialog());
        JButton cancelBookingButton = createStyledButton("Cancel Booking", DANGER_COLOR, e -> cancelBooking());
        JButton removeCancelledButton = createStyledButton("Remove Cancelled", new Color(155, 89, 182), e -> removeCancelledBookings());
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR, e -> loadBookingsData());
        
        controlPanel.add(newBookingButton);
        controlPanel.add(cancelBookingButton);
        controlPanel.add(removeCancelledButton);
        controlPanel.add(refreshButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadBookingsData();
        return panel;
    }
    
    private void loadBookingsData() {
        try {
            bookingsModel.setRowCount(0);
            String query = "SELECT b.id, CONCAT(c.first_name, ' ', c.last_name) as customer, " +
                          "r.room_number, b.check_in_date, b.check_out_date, " +
                          "b.total_amount, b.status " +
                          "FROM bookings b " +
                          "JOIN customers c ON b.customer_id = c.id " +
                          "JOIN rooms r ON b.room_id = r.id " +
                          "ORDER BY b.id DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            
            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("customer"),
                    rs.getString("room_number"),
                    dateFormat.format(rs.getDate("check_in_date")),
                    dateFormat.format(rs.getDate("check_out_date")),
                    String.format("$%.2f", rs.getDouble("total_amount")),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showError("Error loading bookings: " + e.getMessage());
        }
    }
    
    private void showNewBookingDialog() {
        JOptionPane.showMessageDialog(this, 
            "New Booking Feature\n\n" +
            "This feature will allow you to:\n" +
            "• Select available rooms\n" +
            "• Choose customers\n" +
            "• Set check-in/check-out dates\n" +
            "• Calculate total amount\n\n" +
            "Coming in the next update!",
            "New Booking", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to cancel");
            return;
        }
        
        int bookingId = (Integer) bookingsModel.getValueAt(selectedRow, 0);
        String customerName = (String) bookingsModel.getValueAt(selectedRow, 1);
        String roomNumber = (String) bookingsModel.getValueAt(selectedRow, 2);
        String status = (String) bookingsModel.getValueAt(selectedRow, 6);
        
        if (status.equals("CANCELLED")) {
            showError("This booking is already cancelled!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel Booking?\n\n" +
            "Booking #" + bookingId + "\n" +
            "Customer: " + customerName + "\n" +
            "Room: " + roomNumber + "\n\n" +
            "This will mark the booking as cancelled.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, bookingId);
                
                if (stmt.executeUpdate() > 0) {
                    showSuccessMessage("Booking Cancelled", "Booking #" + bookingId + " has been cancelled.");
                    loadBookingsData();
                }
            } catch (SQLException e) {
                showError("Error cancelling booking: " + e.getMessage());
            }
        }
    }
    
    private void removeCancelledBookings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove All Cancelled Bookings?\n\n" +
            "This will permanently delete all bookings with status 'CANCELLED'.\n" +
            "This action cannot be undone!\n\n" +
            "Are you sure you want to proceed?",
            "Remove Cancelled Bookings", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM bookings WHERE status = 'CANCELLED'";
                PreparedStatement stmt = connection.prepareStatement(query);
                int deletedCount = stmt.executeUpdate();
                
                if (deletedCount > 0) {
                    showSuccessMessage("Cancelled Bookings Removed", 
                        deletedCount + " cancelled booking(s) have been permanently removed.");
                    loadBookingsData();
                } else {
                    showInfoMessage("No cancelled bookings found to remove.");
                }
            } catch (SQLException e) {
                showError("Error removing cancelled bookings: " + e.getMessage());
            }
        }
    }
    
    // ==================== DASHBOARD PANEL ====================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createSectionHeader("Dashboard", "Hotel performance overview and statistics");
        
        // Stats cards
        JPanel statsPanel = createStatsPanel();
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        try {
            int totalRooms = getCount("SELECT COUNT(*) FROM rooms");
            int availableRooms = getCount("SELECT COUNT(*) FROM rooms WHERE is_available = true");
            int totalCustomers = getCount("SELECT COUNT(*) FROM customers");
            int totalBookings = getCount("SELECT COUNT(*) FROM bookings");
            int activeBookings = getCount("SELECT COUNT(*) FROM bookings WHERE status IN ('CONFIRMED', 'PENDING')");
            int cancelledBookings = getCount("SELECT COUNT(*) FROM bookings WHERE status = 'CANCELLED'");
            
            panel.add(createStatCard("Total Rooms", String.valueOf(totalRooms), PRIMARY_COLOR));
            panel.add(createStatCard("Available Rooms", String.valueOf(availableRooms), ACCENT_COLOR));
            panel.add(createStatCard("Total Customers", String.valueOf(totalCustomers), SECONDARY_COLOR));
            panel.add(createStatCard("Active Bookings", String.valueOf(activeBookings), new Color(155, 89, 182)));
            
        } catch (SQLException e) {
            showError("Error loading statistics: " + e.getMessage());
        }
        
        return panel;
    }
    
    private int getCount(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // ==================== DEMO PANELS ====================
    private JPanel createDemoPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel messageLabel = new JLabel(
            "<html><center><h1>" + title + "</h1><br>" +
            "<p style='font-size:16px; color: #666;'>Demo mode - data not saved to database</p><br>" +
            "<p style='font-size:14px; color: #999;'>Connect to MySQL database to enable full features</p></center></html>",
            JLabel.CENTER
        );
        
        panel.add(messageLabel, BorderLayout.CENTER);
        return panel;
    }
    
    // ==================== SYSTEM INFO PANEL ====================
    private JPanel createSystemInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(CARD_COLOR);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        infoArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        StringBuilder info = new StringBuilder();
        info.append("HOTEL MANAGEMENT SYSTEM - SYSTEM INFORMATION\n");
        info.append("=============================================\n\n");
        
        if (connection != null) {
            info.append("DATABASE STATUS: CONNECTED\n\n");
            try {
                info.append("Database: ").append(connection.getCatalog()).append("\n");
                info.append("Connection URL: ").append(connection.getMetaData().getURL()).append("\n\n");
                
                info.append("TABLE STATISTICS:\n");
                info.append("• Rooms: ").append(getCount("SELECT COUNT(*) FROM rooms")).append(" entries\n");
                info.append("• Customers: ").append(getCount("SELECT COUNT(*) FROM customers")).append(" entries\n");
                info.append("• Bookings: ").append(getCount("SELECT COUNT(*) FROM bookings")).append(" entries\n\n");
                
                info.append("ID MANAGEMENT:\n");
                info.append("• Auto-increment gaps are normal in MySQL\n");
                info.append("• Use 'Reset ID Sequence' button to fill gaps\n");
                info.append("• This maintains referential integrity\n\n");
                
                info.append("APPLICATION FEATURES:\n");
                info.append("• Room Management (Add, View, Toggle Availability)\n");
                info.append("• Customer Management (Add, Edit, Delete, View, Reset IDs)\n");
                info.append("• Booking Management (View, Cancel, Remove Cancelled)\n");
                info.append("• Real-time Dashboard with Statistics\n");
                info.append("• Modern User Interface\n");
                
            } catch (SQLException e) {
                info.append("Error retrieving system information: ").append(e.getMessage()).append("\n");
            }
        } else {
            info.append("DATABASE STATUS: NOT CONNECTED\n\n");
            info.append("Running in DEMO MODE\n\n");
            info.append("To enable full database features:\n");
            info.append("1. Install MySQL Server 8.0+\n");
            info.append("2. Start MySQL Service\n");
            info.append("3. Update connection settings in code\n");
            info.append("4. Restart the application\n\n");
            info.append("Default connection settings:\n");
            info.append("URL: jdbc:mysql://localhost:3306/hotel_management\n");
            info.append("Username: root\n");
            info.append("Password: [blank or your MySQL password]\n");
        }
        
        infoArea.setText(info.toString());
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== UI HELPER METHODS ====================
    private JPanel createSectionHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        
        return header;
    }
    
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }
    
    private JButton createStyledButton(String text, Color color, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.addActionListener(action);
        return button;
    }
    
    // ==================== MESSAGE HELPERS ====================
    private void showSuccessMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showError(String message) {
        showErrorMessage("Error", message);
    }
    
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found!\n\n" +
                "Please download mysql-connector-j-8.0.33.jar\n" +
                "and place it in the same folder as this application.");
        }
        
        SwingUtilities.invokeLater(() -> {
            new HotelManagementSystem().setVisible(true);
        });
    }
}