package com.example.bank;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BankController {

    record CreateAccountRequest(String holderName) {}
    record AmountRequest(BigDecimal amount) {}
    record TransferRequest(String from, String to, BigDecimal amount) {}
    record AccountView(String accountNumber, String holderName, BigDecimal balance) {
        static AccountView of(Account a) {
            return new AccountView(a.getAccountNumber(), a.getHolderName(), a.getBalance());
        }
    }

    private final Bank bank;

    public BankController(Bank bank) {
        this.bank = bank;
    }

    @GetMapping("/accounts")
    public List<AccountView> list() {
        synchronized (bank) {
            return bank.listAccounts().stream().map(AccountView::of).toList();
        }
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView open(@RequestBody CreateAccountRequest req) {
        synchronized (bank) {
            return AccountView.of(bank.openAccount(req.holderName()));
        }
    }

    @PostMapping("/accounts/{number}/deposit")
    public AccountView deposit(@PathVariable String number, @RequestBody AmountRequest req) {
        synchronized (bank) {
            Account a = bank.getAccount(number);
            a.deposit(req.amount());
            return AccountView.of(a);
        }
    }

    @PostMapping("/accounts/{number}/withdraw")
    public AccountView withdraw(@PathVariable String number, @RequestBody AmountRequest req) {
        synchronized (bank) {
            Account a = bank.getAccount(number);
            a.withdraw(req.amount());
            return AccountView.of(a);
        }
    }

    @PostMapping("/transfer")
    public List<AccountView> transfer(@RequestBody TransferRequest req) {
        if (req.from() == null || req.to() == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' accounts are required");
        }
        synchronized (bank) {
            bank.transfer(req.from(), req.to(), req.amount());
            return List.of(AccountView.of(bank.getAccount(req.from())),
                           AccountView.of(bank.getAccount(req.to())));
        }
    }

    @GetMapping("/accounts/{number}/transactions")
    public List<Transaction> transactions(@PathVariable String number) {
        synchronized (bank) {
            return List.copyOf(bank.getAccount(number).getHistory());
        }
    }

    @DeleteMapping("/accounts/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void close(@PathVariable String number) {
        synchronized (bank) {
            bank.closeAccount(number);
        }
    }
}
