package com.bank.main;

import com.bank.service.CustomerService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        CustomerService customerService = new CustomerService(sc);

        boolean running = true;

        while (running) {
            System.out.println("\n==========================");
            System.out.println("     WELCOME TO BANK      ");
            System.out.println("==========================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.println("==========================");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> customerService.register();
                case "2" -> customerService.login();
                case "0" -> {
                    System.out.println("Thank you! Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        sc.close();
    }
}
