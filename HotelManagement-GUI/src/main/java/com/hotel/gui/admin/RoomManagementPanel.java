package com.hotel.gui.admin;

import com.hotel.model.Room;
import com.hotel.dao.RoomDAO;
import com.hotel.gui.components.CustomButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private RoomDAO roomDAO;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    
    public RoomManagementPanel() {
        this.roomDAO = new RoomDAO();
        initializeUI();
        loadRooms();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton addButton = new CustomButton("Add Room", new Color(46, 204, 113));
        JButton editButton = new CustomButton("Edit Room", new Color(52, 152, 219));
        JButton deleteButton = new CustomButton("Delete Room", new Color(231, 76, 60));
        JButton refreshButton = new CustomButton("Refresh", new Color(149, 165, 166));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.NORTH);
        
        // Room table
        String[] columns = {"ID", "Room Number", "Type", "Price/Night", "Capacity", "Amenities", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Event listeners
        addButton.addActionListener(e -> showAddRoomDialog());
        editButton.addActionListener(e -> editSelectedRoom());
        deleteButton.addActionListener(e -> deleteSelectedRoom());
        refreshButton.addActionListener(e -> loadRooms());
    }
    
    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();
        
        for (Room room : rooms) {
            Object[] rowData = {
                room.getId(),
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
    
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Room", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField roomNumberField = new JTextField();
        JComboBox<Room.RoomType> typeCombo = new JComboBox<>(Room.RoomType.values());
        JTextField priceField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField amenitiesField = new JTextField();
        JCheckBox availableCheckbox = new JCheckBox("Available", true);
        
        panel.add(new JLabel("Room Number:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Room Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Price/Night:"));
        panel.add(priceField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        panel.add(new JLabel("Amenities:"));
        panel.add(amenitiesField);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckbox);
        
        JButton saveButton = new CustomButton("Save", new Color(46, 204, 113));
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                Room room = new Room();
                room.setRoomNumber(roomNumberField.getText());
                room.setRoomType((Room.RoomType) typeCombo.getSelectedItem());
                room.setPricePerNight(new BigDecimal(priceField.getText()));
                room.setDescription(descriptionArea.getText());
                room.setAmenities(amenitiesField.getText());
                room.setAvailable(availableCheckbox.isSelected());
                
                if (roomDAO.addRoom(room)) {
                    JOptionPane.showMessageDialog(dialog, "Room added successfully!");
                    loadRooms();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add room", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Room room = roomDAO.getRoomById(roomId);
        
        if (room != null) {
            showEditRoomDialog(room);
        }
    }
    
    private void showEditRoomDialog(Room room) {
        // Similar to add dialog but pre-filled with room data
        JOptionPane.showMessageDialog(this, "Edit room functionality to be implemented");
    }
    
    private void deleteSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String roomNumber = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete room " + roomNumber + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAO.deleteRoom(roomId)) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}