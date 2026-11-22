package bank;

/** A company or organisation. Cannot open a Cheque account (that is for a working person). */
public class BusinessCustomer extends Customer {

    private String businessName;
    private String registrationNumber;

    public BusinessCustomer(int userId, String username, String passwordHash, String address, String phone,
                            String businessName, String registrationNumber) {
        super(userId, username, passwordHash, address, phone);
        this.businessName = businessName;
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String getDisplayName() {
        return businessName;
    }

    /** Overrides the parent version to add the Cheque rule. */
    @Override
    public void openAccount(Account account) {
        if (account instanceof ChequeAccount) {
            throw new IllegalStateException("Business customers cannot open a Cheque account");
        }
        super.openAccount(account);
    }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getRegistrationNumber() { return registrationNumber; }
}
