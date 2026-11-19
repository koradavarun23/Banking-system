package com.example.bank;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Bank {
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextId = 1001;

    public Account openAccount(String holderName) {
        String number = "ACC" + nextId++;
        Account account = new Account(number, holderName);
        accounts.put(number, account);
        return account;
    }

    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new NoSuchElementException("Account not found: " + accountNumber);
        }
        return account;
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) {
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);
        from.sendTransfer(amount);
        to.receiveTransfer(amount);
    }

    public void closeAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account.getBalance().signum() != 0) {
            throw new IllegalStateException("Account balance must be zero to close");
        }
        accounts.remove(accountNumber);
    }

    public Collection<Account> listAccounts() {
        return accounts.values();
    }
}
