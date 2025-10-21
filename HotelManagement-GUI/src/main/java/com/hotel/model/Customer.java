package com.hotel.model;

public class Customer extends User {
    private String address;
    
    public Customer() {}
    
    public Customer(String email, String password, String firstName, String lastName, String phone, String address) {
        super(email, password, firstName, lastName, phone);
        this.address = address;
    }
    
    @Override
    public String getUserType() {
        return "CUSTOMER";
    }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}