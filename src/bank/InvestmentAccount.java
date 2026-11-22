package bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/** Investment: needs BWP 500 to open, deposits and withdrawals allowed, pays 5% interest each month. */
public class InvestmentAccount extends Account implements InterestBearing {

    private static final BigDecimal RATE = new BigDecimal("0.05");                 // 5%
    private static final BigDecimal MIN_OPENING = new BigDecimal("500.00");
    private YearMonth lastInterestMonth;

    public InvestmentAccount(String accountNumber, String branch, BigDecimal initialDeposit) {
        super(accountNumber, branch);
        if (initialDeposit == null || initialDeposit.compareTo(MIN_OPENING) < 0) {
            throw new IllegalArgumentException("An Investment account needs at least BWP 500.00 to open");
        }
        deposit(initialDeposit, "Opening deposit");
    }

    /** For an Investment account that already exists (the database code will use this to load it). */
    public InvestmentAccount(String accountNumber, String branch, BigDecimal balance, LocalDate openDate,
                             int customerId, YearMonth lastInterestMonth) {
        super(accountNumber, branch, balance, openDate, customerId);
        this.lastInterestMonth = lastInterestMonth;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        checkWithdrawal(amount);
        recordTransaction(TransactionType.WITHDRAWAL, amount, "Withdrawal");
    }

    @Override
    public BigDecimal calculateInterest() {
        return getBalance().multiply(RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void payInterest() {
        YearMonth thisMonth = YearMonth.now();
        if (thisMonth.equals(lastInterestMonth)) {
            return;
        }
        BigDecimal interest = calculateInterest();
        if (interest.signum() > 0) {
            recordTransaction(TransactionType.INTEREST, interest, "Monthly interest");
        }
        lastInterestMonth = thisMonth;
    }

    public YearMonth getLastInterestMonth() { return lastInterestMonth; }
}
