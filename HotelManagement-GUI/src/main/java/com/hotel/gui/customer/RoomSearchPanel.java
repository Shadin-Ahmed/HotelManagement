package com.hotel.gui.customer;

import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.dao.RoomDAO;
import com.hotel.gui.components.CustomButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RoomSearchPanel extends JPanel {
    private Customer customer;
    private RoomDAO roomDAO;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> roomTypeCombo;
    private JButton searchButton, bookButton;
    
    public RoomSearchPanel(Customer customer) {
        this.customer = customer;
        this.roomDAO = new RoomDAO();
        initializeUI();
        loadRooms();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        searchPanel.add(new JLabel("Room Type:"));
        roomTypeCombo = new JComboBox<>(new String[]{"All", "SINGLE", "DOUBLE", "SUITE", "DELUXE"});
        searchPanel.add(roomTypeCombo);
        
        searchButton = new CustomButton("Search", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        bookButton = new CustomButton("Book Selected Room", new Color(46, 204, 113));
        searchPanel.add(bookButton);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Room table
        String[] columns = {"Room Number", "Type", "Price/Night", "Capacity", "Amenities", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Event listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRooms();
            }
        });
        
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSelectedRoom();
            }
        });
    }
    
    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAvailableRooms();
        
        String selectedType = (String) roomTypeCombo.getSelectedItem();
        
        for (Room room : rooms) {
            if ("All".equals(selectedType) || room.getRoomType().name().equals(selectedType)) {
                Object[] rowData = {
                    room.getRoomNumber(),
                    room.getRoomTypeDisplay(),
                    "$" + room.getPricePerNight(),
                    room.getCapacity(),
                    room.getAmenities(),
                    room.isAvailable() ? "Available" : "Occupied"
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void bookSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to book", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find the room object
        List<Room> rooms = roomDAO.getAvailableRooms();
        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                selectedRoom = room;
                break;
            }
        }
        
        if (selectedRoom != null) {
            // Show booking dialog
            showBookingDialog(selectedRoom);
        }
    }
    
    private void showBookingDialog(Room room) {
        JDialog bookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Room", true);
        bookingDialog.setSize(400, 300);
        bookingDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField roomField = new JTextField(room.getRoomNumber());
        roomField.setEditable(false);
        
        JTextField typeField = new JTextField(room.getRoomTypeDisplay());
        typeField.setEditable(false);
        
        JTextField priceField = new JTextField("$" + room.getPricePerNight());
        priceField.setEditable(false);
        
        JTextField checkInField = new JTextField();
        JTextField checkOutField = new JTextField();
        JTextArea specialRequestsArea = new JTextArea(3, 20);
        
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        panel.add(new JLabel("Room Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Price/Night:"));
        panel.add(priceField);
        panel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        panel.add(checkOutField);
        panel.add(new JLabel("Special Requests:"));
        panel.add(new JScrollPane(specialRequestsArea));
        
        JButton confirmButton = new CustomButton("Confirm Booking", new Color(46, 204, 113));
        JButton cancelButton = new JButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            // Here you would implement the booking logic
            JOptionPane.showMessageDialog(bookingDialog, "Booking functionality to be implemented");
            bookingDialog.dispose();
        });
        
        cancelButton.addActionListener(e -> bookingDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        bookingDialog.add(panel, BorderLayout.CENTER);
        bookingDialog.add(buttonPanel, BorderLayout.SOUTH);
        bookingDialog.setVisible(true);
    }
}