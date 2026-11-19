package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String holderName;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String accountNumber, String holderName) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Holder name is required");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
    }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
        record("DEPOSIT", amount);
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + accountNumber);
        }
        balance = balance.subtract(amount);
        record("WITHDRAW", amount);
    }

    void receiveTransfer(BigDecimal amount) {
        balance = balance.add(amount);
        record("TRANSFER_IN", amount);
    }

    void sendTransfer(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + accountNumber);
        }
        balance = balance.subtract(amount);
        record("TRANSFER_OUT", amount);
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void record(String type, BigDecimal amount) {
        history.add(new Transaction(type, amount, balance, LocalDateTime.now()));
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public BigDecimal getBalance() { return balance; }
    public List<Transaction> getHistory() { return Collections.unmodifiableList(history); }
}
