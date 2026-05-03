package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // ─── CREATE ACCOUNT ────────────────────────────────────
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, balance) VALUES (?, ?, ?, ?)";

        try {
            Connection conn      = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountNumber());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Create account failed: " + e.getMessage());
        }
        return false;
    }

    // ─── GET ALL ACCOUNTS OF A CUSTOMER ────────────────────
    public List<Account> getAccountsByCustomer(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = 'ACTIVE'";

        try {
            Connection conn      = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setCustomerId(rs.getInt("customer_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setStatus(rs.getString("status"));
                list.add(account);
            }

        } catch (SQLException e) {
            System.out.println("Get accounts failed: " + e.getMessage());
        }
        return list;
    }

    // ─── GET ACCOUNT BY ACCOUNT NUMBER ─────────────────────
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try {
            Connection conn      = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setCustomerId(rs.getInt("customer_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setStatus(rs.getString("status"));
                return account;
            }

        } catch (SQLException e) {
            System.out.println("Get account failed: " + e.getMessage());
        }
        return null;
    }

    // ─── UPDATE BALANCE ────────────────────────────────────
    public boolean updateBalance(int accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try {
            Connection conn      = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update balance failed: " + e.getMessage());
        }
        return false;
    }

    // ─── GENERATE ACCOUNT NUMBER ───────────────────────────
    public String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
}