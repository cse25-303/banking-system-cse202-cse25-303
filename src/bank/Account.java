package bank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The parent of all accounts. It cannot be created directly (it is abstract). */
public abstract class Account {

    private String accountNumber;
    private int customerId;               // the owner (set when the customer opens the account)
    private BigDecimal balance;
    private String branch;
    private LocalDate openDate;
    private List<Transaction> transactions = new ArrayList<>();

    /** For a brand-new account: balance starts at 0.00 and today is the open date. */
    protected Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = new BigDecimal("0.00");
        this.openDate = LocalDate.now();
    }

    /** For an account that already exists (the database code will use this to load it). */
    protected Account(String accountNumber, String branch, BigDecimal balance, LocalDate openDate, int customerId) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = balance;
        this.openDate = openDate;
        this.customerId = customerId;
    }

    // ---- deposit (two versions: this is "overloading") ----

    public void deposit(BigDecimal amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(BigDecimal amount, String note) {
        checkAmount(amount);
        recordTransaction(TransactionType.DEPOSIT, amount, note);
    }

    // ---- withdraw: every account type decides its own rule ----

    public abstract void withdraw(BigDecimal amount);

    /** Shared check used by the accounts that allow withdrawals. */
    protected void checkWithdrawal(BigDecimal amount) {
        checkAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Not enough money in the account");
        }
    }

    /** An amount must be more than 0 and have at most 2 decimal places (like real money). */
    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("The amount must be more than 0");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("The amount can have at most 2 decimal places");
        }
    }

    /** Changes the balance and adds a line to the history. All money movements use this. */
    protected void recordTransaction(TransactionType type, BigDecimal amount, String note) {
        if (type == TransactionType.WITHDRAWAL) {
            balance = balance.subtract(amount);
        } else {
            balance = balance.add(amount);
        }
        transactions.add(new Transaction(accountNumber, type, note, amount, balance));
    }

    /** Only for the database code: puts an old transaction back in the history WITHOUT changing the balance. */
    public void loadTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // ---- getters (there is no setBalance on purpose: money only moves through deposit/withdraw/interest) ----

    public String getAccountNumber() { return accountNumber; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public BigDecimal getBalance() { return balance; }
    public String getBranch() { return branch; }
    public LocalDate getOpenDate() { return openDate; }

    /** A read-only list, so nobody can change the history from outside. */
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + accountNumber + " | balance: BWP " + balance;
    }
}
