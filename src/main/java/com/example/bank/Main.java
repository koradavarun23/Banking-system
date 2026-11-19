package com.example.bank;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner in = new Scanner(System.in);
        System.out.println("=== Simple Banking System ===");

        while (true) {
            System.out.println("\n1. Open account  2. Deposit  3. Withdraw  4. Transfer");
            System.out.println("5. Balance  6. Statement  7. List accounts  8. Close account  0. Exit");
            System.out.print("Choice: ");
            String choice = in.hasNextLine() ? in.nextLine().trim() : "0";

            try {
                switch (choice) {
                    case "1" -> {
                        String name = ask(in, "Holder name: ");
                        Account a = bank.openAccount(name);
                        System.out.println("Opened account " + a.getAccountNumber());
                    }
                    case "2" -> {
                        Account a = bank.getAccount(ask(in, "Account no: "));
                        a.deposit(new BigDecimal(ask(in, "Amount: ")));
                        System.out.println("New balance: " + a.getBalance());
                    }
                    case "3" -> {
                        Account a = bank.getAccount(ask(in, "Account no: "));
                        a.withdraw(new BigDecimal(ask(in, "Amount: ")));
                        System.out.println("New balance: " + a.getBalance());
                    }
                    case "4" -> {
                        String from = ask(in, "From account: ");
                        String to = ask(in, "To account: ");
                        bank.transfer(from, to, new BigDecimal(ask(in, "Amount: ")));
                        System.out.println("Transfer complete");
                    }
                    case "5" -> {
                        Account a = bank.getAccount(ask(in, "Account no: "));
                        System.out.println("Balance: " + a.getBalance());
                    }
                    case "6" -> {
                        Account a = bank.getAccount(ask(in, "Account no: "));
                        a.getHistory().forEach(System.out::println);
                    }
                    case "7" -> bank.listAccounts().forEach(a ->
                            System.out.printf("%s  %-15s %s%n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                    case "8" -> {
                        bank.closeAccount(ask(in, "Account no: "));
                        System.out.println("Account closed");
                    }
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid number");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }
}
