package bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/** Savings: no withdrawals, pays 0.05% interest each month. */
public class SavingsAccount extends Account implements InterestBearing {

    private static final BigDecimal RATE = new BigDecimal("0.0005");   // 0.05%
    private YearMonth lastInterestMonth;

    public SavingsAccount(String accountNumber, String branch, BigDecimal initialDeposit) {
        super(accountNumber, branch);
        if (initialDeposit == null || initialDeposit.signum() <= 0) {
            throw new IllegalArgumentException("A Savings account needs an opening deposit above 0");
        }
        deposit(initialDeposit, "Opening deposit");
    }

    /** For a Savings account that already exists (the database code will use this to load it). */
    public SavingsAccount(String accountNumber, String branch, BigDecimal balance, LocalDate openDate,
                          int customerId, YearMonth lastInterestMonth) {
        super(accountNumber, branch, balance, openDate, customerId);
        this.lastInterestMonth = lastInterestMonth;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        throw new IllegalStateException("Withdrawals are not allowed from a Savings account");
    }

    @Override
    public BigDecimal calculateInterest() {
        return getBalance().multiply(RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void payInterest() {
        YearMonth thisMonth = YearMonth.now();
        if (thisMonth.equals(lastInterestMonth)) {
            return;                                   // already paid this month
        }
        BigDecimal interest = calculateInterest();
        if (interest.signum() > 0) {
            recordTransaction(TransactionType.INTEREST, interest, "Monthly interest");
        }
        lastInterestMonth = thisMonth;
    }

    public YearMonth getLastInterestMonth() { return lastInterestMonth; }
}
