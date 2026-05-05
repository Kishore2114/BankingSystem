package com.bank.service;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dao.TransactionDAO;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminService {

    private final Scanner         sc;
    private final CustomerDAO     customerDAO;
    private final AccountDAO      accountDAO;
    private final TransactionDAO  transactionDAO;

    public AdminService(Scanner sc) {
        this.sc             = sc;
        this.customerDAO    = new CustomerDAO();
        this.accountDAO     = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // ─── ADMIN LOGIN ───────────────────────────────────────
    public void login() {
        System.out.println("\n==========================");
        System.out.println("       ADMIN LOGIN        ");
        System.out.println("==========================");

        System.out.print("  Username : ");
        String username = sc.nextLine().trim();

        System.out.print("  Password : ");
        String password = sc.nextLine().trim();

        if (validateAdmin(username, password)) {
            System.out.println("\n  ✔ Admin login successful!");
            adminMenu();
        } else {
            System.out.println("\n  ✘ Invalid admin credentials.");
        }
    }

    // ─── VALIDATE ADMIN ────────────────────────────────────
    private boolean validateAdmin(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try {
            Connection conn      = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Admin login failed: " + e.getMessage());
        }
        return false;
    }

    // ─── ADMIN MENU ────────────────────────────────────────
    private void adminMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n==========================");
            System.out.println("       ADMIN PANEL        ");
            System.out.println("==========================");
            System.out.println("1. View All Customers");
            System.out.println("2. View All Accounts");
            System.out.println("3. View Account Transactions");
            System.out.println("4. Bank Summary");
            System.out.println("0. Logout");
            System.out.println("==========================");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllCustomers();
                case "2" -> viewAllAccounts();
                case "3" -> viewAccountTransactions();
                case "4" -> bankSummary();
                case "0" -> {
                    System.out.println("  Admin logged out.");
                    running = false;
                }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ─── VIEW ALL CUSTOMERS ────────────────────────────────
    private void viewAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        System.out.println("\n  ── All Customers ──────────────────────────────");
        System.out.printf("  %-5s %-20s %-25s %-15s%n",
            "ID", "Name", "Email", "Phone");
        System.out.println("  ──────────────────────────────────────────────");

        for (Customer c : customers) {
            System.out.printf("  %-5d %-20s %-25s %-15s%n",
                c.getCustomerId(),
                c.getFullName(),
                c.getEmail(),
                c.getPhone());
        }
        System.out.println("  Total Customers: " + customers.size());
    }

    // ─── VIEW ALL ACCOUNTS ─────────────────────────────────
    private void viewAllAccounts() {
        List<Account> accounts = accountDAO.getAllAccounts();
        System.out.println("\n  ── All Accounts ───────────────────────────────");
        System.out.printf("  %-15s %-12s %12s %10s%n",
            "Account No.", "Type", "Balance", "Status");
        System.out.println("  ──────────────────────────────────────────────");

        for (Account a : accounts) {
            System.out.printf("  %-15s %-12s %12.2f %10s%n",
                a.getAccountNumber(),
                a.getAccountType(),
                a.getBalance(),
                a.getStatus());
        }
        System.out.println("  Total Accounts: " + accounts.size());
    }

    // ─── VIEW ACCOUNT TRANSACTIONS ─────────────────────────
    private void viewAccountTransactions() {
        System.out.print("  Enter Account Number: ");
        String accNum = sc.nextLine().trim();

        Account account = accountDAO.getAccountByNumber(accNum);
        if (account == null) {
            System.out.println("  ✘ Account not found.");
            return;
        }

        List<Transaction> transactions = transactionDAO
                            .getTransactionsByAccount(account.getAccountId());

        if (transactions.isEmpty()) {
            System.out.println("  No transactions found.");
            return;
        }

        System.out.println("\n  ── Transactions: " + accNum + " -------------------");
        System.out.printf("  %-6s %-15s %10s %12s  %s%n",
            "ID", "Type", "Amount", "Balance", "Date");
        System.out.println("  ------------------------------------------------------");

        for (Transaction t : transactions) {
            System.out.printf("  %-6d %-15s %10.2f %12.2f  %s%n",
                t.getTransactionId(),
                t.getTransactionType(),
                t.getAmount(),
                t.getBalanceAfter(),
                t.getTransactionDate());
        }
        System.out.println("  Total: " + transactions.size());
    }

    // ─── BANK SUMMARY ──────────────────────────────────────
    private void bankSummary() {
        List<Customer> customers = customerDAO.getAllCustomers();
        List<Account>  accounts  = accountDAO.getAllAccounts();

        double totalBalance = accounts.stream()
            .filter(a -> "ACTIVE".equals(a.getStatus()))
            .mapToDouble(Account::getBalance)
            .sum();

        long activeAccounts = accounts.stream()
            .filter(a -> "ACTIVE".equals(a.getStatus()))
            .count();

        System.out.println("\n ==============================");
        System.out.println("           BANK SUMMARY          ");
        System.out.println("  ================================");
        System.out.printf("    Total Customers  : %-8d  %n", customers.size());
        System.out.printf("    Total Accounts   : %-8d  %n", accounts.size());
        System.out.printf("    Active Accounts  : %-8d  %n", activeAccounts);
        System.out.printf("    Total Deposits   : Rs %-6.0f  %n", totalBalance);
        System.out.println("  =================================");
    }
}