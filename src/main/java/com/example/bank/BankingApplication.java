package com.example.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @Bean
    public Bank bank() {
        Bank bank = new Bank();
        // Demo data so the UI isn't empty on first load
        Account a = bank.openAccount("Alice Reddy");
        a.deposit(new java.math.BigDecimal("5000.00"));
        Account b = bank.openAccount("Bob Khan");
        b.deposit(new java.math.BigDecimal("1200.50"));
        return bank;
    }
}
