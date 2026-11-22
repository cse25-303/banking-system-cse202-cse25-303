package bank;

import java.math.BigDecimal;
import java.util.List;

/** Bank staff. Runs the monthly interest. */
public class Administrator extends User {

    private String staffName;

    public Administrator(int userId, String username, String passwordHash, String staffName) {
        super(userId, username, passwordHash);
        this.staffName = staffName;
    }

    @Override
    public String getDisplayName() {
        return staffName;
    }

    /** Pays interest to every account that earns it. Returns how many accounts actually received interest. */
    public int payMonthlyInterest(List<Account> accounts) {
        int paid = 0;
        for (Account account : accounts) {
            if (account instanceof InterestBearing) {             // Savings and Investment only
                BigDecimal before = account.getBalance();
                ((InterestBearing) account).payInterest();
                if (account.getBalance().compareTo(before) > 0) {  // the balance went up, so interest was paid
                    paid++;
                }
            }
        }
        return paid;
    }
}
