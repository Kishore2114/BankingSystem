package com.bank.model;

import java.sql.Timestamp;

public class Transaction {

    private int       transactionId;
    private int       accountId;
    private String    transactionType;
    private double    amount;
    private double    balanceAfter;
    private Timestamp transactionDate;

    // Constructor
    public Transaction() {}

    // Getters
    public int       getTransactionId()   { return transactionId; }
    public int       getAccountId()       { return accountId; }
    public String    getTransactionType() { return transactionType; }
    public double    getAmount()          { return amount; }
    public double    getBalanceAfter()    { return balanceAfter; }
    public Timestamp getTransactionDate() { return transactionDate; }

    // Setters
    public void setTransactionId(int transactionId)      { this.transactionId = transactionId; }
    public void setAccountId(int accountId)               { this.accountId = accountId; }
    public void setTransactionType(String type)           { this.transactionType = type; }
    public void setAmount(double amount)                  { this.amount = amount; }
    public void setBalanceAfter(double balanceAfter)      { this.balanceAfter = balanceAfter; }
    public void setTransactionDate(Timestamp date)        { this.transactionDate = date; }
}