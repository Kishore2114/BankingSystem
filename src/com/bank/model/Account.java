package com.bank.model;

public class Account {

    private int    accountId;
    private int    customerId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private String status;

    // Constructor
    public Account() {}

    // Getters
    public int    getAccountId()     { return accountId; }
    public int    getCustomerId()    { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType()   { return accountType; }
    public double getBalance()       { return balance; }
    public String getStatus()        { return status; }

    // Setters
    public void setAccountId(int accountId)          { this.accountId = accountId; }
    public void setCustomerId(int customerId)         { this.customerId = customerId; }
    public void setAccountNumber(String accountNumber){ this.accountNumber = accountNumber; }
    public void setAccountType(String accountType)    { this.accountType = accountType; }
    public void setBalance(double balance)            { this.balance = balance; }
    public void setStatus(String status)              { this.status = status; }
}