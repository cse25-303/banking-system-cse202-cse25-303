package bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** What every customer has in common. Abstract: a customer is always an Individual or a Business. */
public abstract class Customer extends User {

    private String address;
    private String phone;
    private List<Account> accounts = new ArrayList<>();

    protected Customer(int userId, String username, String passwordHash, String address, String phone) {
        super(userId, username, passwordHash);
        this.address = address;
        this.phone = phone;
    }

    /** An account always belongs to the customer who opens it. */
    public void openAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("No account was given");
        }
        if (accounts.contains(account)) {
            throw new IllegalStateException("This account is already opened");
        }
        account.setCustomerId(getUserId());
        accounts.add(account);
    }

    /** Finds one of THIS customer's accounts. Returns null if the customer does not own it. */
    public Account findAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /** A read-only list: accounts can only be added through openAccount(), so the rules cannot be skipped. */
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
