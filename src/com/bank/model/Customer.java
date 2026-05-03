package com.bank.model;

public class Customer {

    private int    customerId;
    private String fullName;
    private String email;
    private String phone;
    private String password;

    // Constructor
    public Customer() {}

    // Getters - used to READ the value
    public int    getCustomerId() { return customerId; }
    public String getFullName()   { return fullName; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }
    public String getPassword()   { return password; }

    // Setters - used to SET the value
    public void setCustomerId(int customerId)   { this.customerId = customerId; }
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public void setEmail(String email)           { this.email = email; }
    public void setPhone(String phone)           { this.phone = phone; }
    public void setPassword(String password)     { this.password = password; }
}