package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(String type, BigDecimal amount, BigDecimal balanceAfter, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return String.format("%s | %-10s | %10s | balance: %s", timestamp.withNano(0), type, amount, balanceAfter);
    }
}
