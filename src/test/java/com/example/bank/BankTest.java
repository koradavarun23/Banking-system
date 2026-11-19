package com.example.bank;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTest {
    private Bank bank;
    private Account alice;
    private Account bob;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        alice = bank.openAccount("Alice");
        bob = bank.openAccount("Bob");
    }

    @Test
    void depositIncreasesBalance() {
        alice.deposit(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), alice.getBalance());
    }

    @Test
    void withdrawDecreasesBalance() {
        alice.deposit(new BigDecimal("100"));
        alice.withdraw(new BigDecimal("40"));
        assertEquals(new BigDecimal("60"), alice.getBalance());
    }

    @Test
    void withdrawMoreThanBalanceFails() {
        alice.deposit(new BigDecimal("50"));
        assertThrows(InsufficientFundsException.class, () -> alice.withdraw(new BigDecimal("80")));
        assertEquals(new BigDecimal("50"), alice.getBalance());
    }

    @Test
    void negativeOrZeroAmountsRejected() {
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(new BigDecimal("-5")));
    }

    @Test
    void transferMovesMoney() {
        alice.deposit(new BigDecimal("200"));
        bank.transfer(alice.getAccountNumber(), bob.getAccountNumber(), new BigDecimal("75"));
        assertEquals(new BigDecimal("125"), alice.getBalance());
        assertEquals(new BigDecimal("75"), bob.getBalance());
    }

    @Test
    void failedTransferChangesNothing() {
        alice.deposit(new BigDecimal("10"));
        assertThrows(InsufficientFundsException.class,
                () -> bank.transfer(alice.getAccountNumber(), bob.getAccountNumber(), new BigDecimal("50")));
        assertEquals(new BigDecimal("10"), alice.getBalance());
        assertEquals(BigDecimal.ZERO, bob.getBalance());
    }

    @Test
    void transferToSameAccountRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> bank.transfer(alice.getAccountNumber(), alice.getAccountNumber(), BigDecimal.ONE));
    }

    @Test
    void unknownAccountThrows() {
        assertThrows(NoSuchElementException.class, () -> bank.getAccount("NOPE"));
    }

    @Test
    void cannotCloseAccountWithBalance() {
        alice.deposit(BigDecimal.TEN);
        assertThrows(IllegalStateException.class, () -> bank.closeAccount(alice.getAccountNumber()));
    }

    @Test
    void historyRecordsTransactions() {
        alice.deposit(new BigDecimal("100"));
        alice.withdraw(new BigDecimal("30"));
        assertEquals(2, alice.getHistory().size());
        assertEquals("WITHDRAW", alice.getHistory().get(1).type());
    }
}
