package com.bank.service;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;

import java.util.List;
import java.util.Scanner;

public class CustomerService {

	
    private final Scanner      sc;
    private final CustomerDAO  customerDAO;
    private final AccountDAO   accountDAO;
    private final TransactionDAO transactionDAO;
    

    public CustomerService(Scanner sc) {
        this.sc          = sc;
        this.customerDAO = new CustomerDAO();
        this.accountDAO  = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // ─── REGISTER ──────────────────────────────────────────
    public void register() {
        System.out.println("\n==========================");
        System.out.println("        REGISTER          ");
        System.out.println("==========================");

        Customer customer = new Customer();

        System.out.print("  Full Name : ");
        customer.setFullName(sc.nextLine().trim());

        System.out.print("  Email     : ");
        customer.setEmail(sc.nextLine().trim());

        System.out.print("  Phone     : ");
        customer.setPhone(sc.nextLine().trim());

        System.out.print("  Password  : ");
        customer.setPassword(sc.nextLine().trim());

        boolean success = customerDAO.registerCustomer(customer);

        if (success) {
            System.out.println("\n  ✔ Registration successful! Please login.");
        } else {
            System.out.println("\n  ✘ Registration failed. Email may already exist.");
        }
    }

    // ─── LOGIN ─────────────────────────────────────────────
    public void login() {
        System.out.println("\n==========================");
        System.out.println("          LOGIN           ");
        System.out.println("==========================");

        System.out.print("  Email    : ");
        String email = sc.nextLine().trim();

        System.out.print("  Password : ");
        String password = sc.nextLine().trim();

        Customer customer = customerDAO.loginCustomer(email, password);

        if (customer == null) {
            System.out.println("\n  ✘ Invalid email or password.");
        } else {
            System.out.println("\n  ✔ Welcome, " + customer.getFullName() + "!");
            customerMenu(customer);
        }
    }

    // ─── CUSTOMER MENU ─────────────────────────────────────
    private void customerMenu(Customer customer) {
        boolean running = true;
        while (running) {
            System.out.println("\n==========================");
            System.out.println("     CUSTOMER MENU        ");
            System.out.println("==========================");
            System.out.println("1. Open New Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Check Balance");
            System.out.println("0. Logout");
            System.out.println("==========================");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> openAccount(customer);
                case "2" -> deposit(customer);
                case "3" -> withdraw(customer);
                case "4" -> transfer(customer);
                case "5" -> transactionHistory(customer);
                case "6" ->  checkBalance(customer);
                case "0" -> {
                    System.out.println("  Logged out successfully.");
                    running = false;
                }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ─── OPEN ACCOUNT ──────────────────────────────────────
    private void openAccount(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("      OPEN ACCOUNT        ");
        System.out.println("==========================");
        System.out.println("  1. SAVINGS");
        System.out.println("  2. CURRENT");
        System.out.print("  Choose type : ");

        String typeChoice = sc.nextLine().trim();
        String type = switch (typeChoice) {
            case "1" -> "SAVINGS";
            case "2" -> "CURRENT";
            default  -> null;
        };

        if (type == null) {
            System.out.println("  ✘ Invalid account type.");
            return;
        }

        System.out.print("  Initial Deposit (Rs): ");
        double initialDeposit;
        try {
            initialDeposit = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid amount.");
            return;
        }

        if (initialDeposit < 500) {
            System.out.println("  ✘ Minimum initial deposit is Rs 500.");
            return;
        }

        Account account = new Account();
        account.setCustomerId(customer.getCustomerId());
        account.setAccountNumber(accountDAO.generateAccountNumber());
        account.setAccountType(type);
        account.setBalance(initialDeposit);

        boolean success = accountDAO.createAccount(account);

        if (success) {
            System.out.println("  ✔ Account created successfully!");
            System.out.println("  Account Number : " + account.getAccountNumber());
            System.out.println("  Account Type   : " + type);
            System.out.println("  Balance        : Rs " + initialDeposit);
        } else {
            System.out.println("  ✘ Failed to create account.");
        }
    }

    // ─── HELPER - SELECT ACCOUNT ───────────────────────────
    private Account selectAccount(Customer customer) {
        List<Account> accounts = accountDAO.getAccountsByCustomer(customer.getCustomerId());

        if (accounts.isEmpty()) {
            System.out.println("  ✘ No active accounts found. Please open an account first.");
            return null;
        }

        System.out.println("\n  Your Accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("  %d. %s (%s) - Rs %.2f%n",
                i + 1,
                accounts.get(i).getAccountNumber(),
                accounts.get(i).getAccountType(),
                accounts.get(i).getBalance());
        }

        System.out.print("  Select account: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx >= 0 && idx < accounts.size()) {
                return accounts.get(idx);
            }
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid selection.");
        }
        return null;
    }
 // ─── DEPOSIT ───────────────────────────────────────────
    private void deposit(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("         DEPOSIT          ");
        System.out.println("==========================");

        // Step 1 - select account
        Account account = selectAccount(customer);
        if (account == null) return;

        // Step 2 - enter amount
        System.out.print("  Amount to Deposit (Rs): ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid amount.");
            return;
        }

        // Step 3 - validate amount
        if (amount <= 0) {
            System.out.println("  ✘ Amount must be greater than 0.");
            return;
        }

        // Step 4 - calculate new balance
        double newBalance = account.getBalance() + amount;

        // Step 5 - update balance in database
        boolean success = accountDAO.updateBalance(account.getAccountId(), newBalance);

        if (success) {
        		recordTransaction(account.getAccountId(), "DEPOSIT", amount, newBalance);
            System.out.println("  ✔ Deposit successful!");
            System.out.println("  Deposited  : Rs " + amount);
            System.out.println("  New Balance: Rs " + newBalance);
        } else {
            System.out.println("  ✘ Deposit failed.");
        }
    }
 // ─── WITHDRAW ──────────────────────────────────────────
    private void withdraw(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("         WITHDRAW         ");
        System.out.println("==========================");

        // Step 1 - select account
        Account account = selectAccount(customer);
        if (account == null) return;

        // Step 2 - enter amount
        System.out.print("  Amount to Withdraw (Rs): ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid amount.");
            return;
        }

        // Step 3 - validate amount
        if (amount <= 0) {
            System.out.println("  ✘ Amount must be greater than 0.");
            return;
        }

        // Step 4 - check sufficient balance
        if (amount > account.getBalance()) {
            System.out.println("  ✘ Insufficient balance.");
            System.out.println("  Available Balance: Rs " + account.getBalance());
            return;
        }

        // Step 5 - calculate new balance
        double newBalance = account.getBalance() - amount;

        // Step 6 - update balance in database
        boolean success = accountDAO.updateBalance(account.getAccountId(), newBalance);

        if (success) {
        		recordTransaction(account.getAccountId(), "WITHDRAWAL", amount, newBalance);
            System.out.println("  ✔ Withdrawal successful!");
            System.out.println("  Withdrawn  : Rs " + amount);
            System.out.println("  New Balance: Rs " + newBalance);
        } else {
            System.out.println("  ✘ Withdrawal failed.");
        }
    }
 // ─── TRANSFER ──────────────────────────────────────────
    private void transfer(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("         TRANSFER         ");
        System.out.println("==========================");

        // Step 1 - select your account
        System.out.println("  Select YOUR account to transfer FROM:");
        Account fromAccount = selectAccount(customer);
        if (fromAccount == null) return;

        // Step 2 - enter recipient account number
        System.out.print("  Enter Recipient Account Number: ");
        String toAccountNumber = sc.nextLine().trim();

        // Step 3 - get recipient account from database
        Account toAccount = accountDAO.getAccountByNumber(toAccountNumber);

        if (toAccount == null) {
            System.out.println("  ✘ Recipient account not found.");
            return;
        }

        // Step 4 - check not same account
        if (fromAccount.getAccountId() == toAccount.getAccountId()) {
            System.out.println("  ✘ Cannot transfer to same account.");
            return;
        }

        // Step 5 - check recipient account is active
        if (!toAccount.getStatus().equals("ACTIVE")) {
            System.out.println("  ✘ Recipient account is not active.");
            return;
        }

        // Step 6 - enter amount
        System.out.print("  Amount to Transfer (Rs): ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✘ Invalid amount.");
            return;
        }

        // Step 7 - validate amount
        if (amount <= 0) {
            System.out.println("  ✘ Amount must be greater than 0.");
            return;
        }

        // Step 8 - check sufficient balance
        if (amount > fromAccount.getBalance()) {
            System.out.println("  ✘ Insufficient balance.");
            System.out.println("  Available Balance: Rs " + fromAccount.getBalance());
            return;
        }

        // Step 9 - calculate new balances
        double newFromBalance = fromAccount.getBalance() - amount;
        double newToBalance   = toAccount.getBalance()   + amount;

        // Step 10 - update both accounts in database
        boolean deducted = accountDAO.updateBalance(fromAccount.getAccountId(), newFromBalance);
        boolean credited = accountDAO.updateBalance(toAccount.getAccountId(),   newToBalance);

        if (deducted && credited) {
        	 	recordTransaction(fromAccount.getAccountId(), "TRANSFER_OUT", amount, newFromBalance);
        	    recordTransaction(toAccount.getAccountId(),   "TRANSFER_IN",  amount, newToBalance);
            System.out.println("  ✔ Transfer successful!");
            System.out.println("  Transferred       : Rs " + amount);
            System.out.println("  Recipient Account : " + toAccountNumber);
            System.out.println("  Your New Balance  : Rs " + newFromBalance);
        } else {
            System.out.println("  ✘ Transfer failed.");
        }
    }
 // ─── HELPER - RECORD TRANSACTION ───────────────────────
    private void recordTransaction(int accountId, String type, 
                                    double amount, double balanceAfter) {
        Transaction t = new Transaction();
        t.setAccountId(accountId);
        t.setTransactionType(type);
        t.setAmount(amount);
        t.setBalanceAfter(balanceAfter);
        transactionDAO.addTransaction(t);
    }
 // ─── TRANSACTION HISTORY ───────────────────────────────
    private void transactionHistory(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("   TRANSACTION HISTORY    ");
        System.out.println("==========================");

        // Step 1 - select account
        Account account = selectAccount(customer);
        if (account == null) return;

        // Step 2 - get all transactions from database
        List<Transaction> transactions = transactionDAO
                            .getTransactionsByAccount(account.getAccountId());

        // Step 3 - check if any transactions exist
        if (transactions.isEmpty()) {
            System.out.println("  No transactions found.");
            return;
        }

        // Step 4 - display all transactions
        System.out.println("\n  Account  : " + account.getAccountNumber());
        System.out.println("  ─────────────────────────────────────────────────────");
        System.out.printf("  %-6s %-15s %10s %12s  %s%n",
            "ID", "Type", "Amount", "Balance", "Date");
        System.out.println("  ─────────────────────────────────────────────────────");

        for (Transaction t : transactions) {
            System.out.printf("  %-6d %-15s %10.2f %12.2f  %s%n",
                t.getTransactionId(),
                t.getTransactionType(),
                t.getAmount(),
                t.getBalanceAfter(),
                t.getTransactionDate());
        }

        System.out.println("  ─────────────────────────────────────────────────────");
        System.out.println("  Total Transactions: " + transactions.size());
    }
 // ─── CHECK BALANCE ─────────────────────────────────────
    private void checkBalance(Customer customer) {
        System.out.println("\n==========================");
        System.out.println("       CHECK BALANCE      ");
        System.out.println("==========================");

        // Step 1 - select account
        Account account = selectAccount(customer);
        if (account == null) return;

        // Step 2 - display balance details
        System.out.println("\n  Account Number : " + account.getAccountNumber());
        System.out.println("  Account Type   : " + account.getAccountType());
        System.out.println("  Balance        : Rs " + account.getBalance());
        System.out.println("  Status         : " + account.getStatus());
    }
}