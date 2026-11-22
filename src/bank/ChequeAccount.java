package bank;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Cheque: for salaries. Deposits and withdrawals allowed, no interest. Needs employer details. */
public class ChequeAccount extends Account {

    private String companyName;
    private String companyAddress;

    public ChequeAccount(String accountNumber, String branch, BigDecimal initialDeposit,
                         String companyName, String companyAddress) {
        super(accountNumber, branch);
        if (companyName == null || companyName.trim().isEmpty()
                || companyAddress == null || companyAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("A Cheque account needs the company name and company address");
        }
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        if (initialDeposit != null && initialDeposit.signum() < 0) {
            throw new IllegalArgumentException("The opening deposit cannot be negative");
        }
        if (initialDeposit != null && initialDeposit.signum() > 0) {
            deposit(initialDeposit, "Opening deposit");     // an opening deposit is optional
        }
    }

    /** For a Cheque account that already exists (the database code will use this to load it). */
    public ChequeAccount(String accountNumber, String branch, BigDecimal balance, LocalDate openDate,
                         int customerId, String companyName, String companyAddress) {
        super(accountNumber, branch, balance, openDate, customerId);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        checkWithdrawal(amount);
        recordTransaction(TransactionType.WITHDRAWAL, amount, "Withdrawal");
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
}
