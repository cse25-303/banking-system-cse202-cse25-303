package bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One line in an account's history. A transaction is never changed after it is created. */
public class Transaction {

    private int transactionId;            // the database will set this later; 0 for now
    private String accountNumber;
    private TransactionType type;
    private String description;
    private TransactionStatus status;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private BigDecimal balanceAfter;

    public Transaction(String accountNumber, TransactionType type, String description,
                       BigDecimal amount, BigDecimal balanceAfter) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.status = TransactionStatus.COMPLETED;
        this.timestamp = LocalDateTime.now();
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }

    @Override
    public String toString() {
        return type + " BWP " + amount + " (" + description + "), balance after: BWP " + balanceAfter;
    }
}
