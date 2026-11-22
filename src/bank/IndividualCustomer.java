package bank;

/** A person. Can open Savings, Investment and Cheque accounts. */
public class IndividualCustomer extends Customer {

    private String firstName;
    private String surname;
    private String idNumber;

    public IndividualCustomer(int userId, String username, String passwordHash, String address, String phone,
                              String firstName, String surname, String idNumber) {
        super(userId, username, passwordHash, address, phone);
        this.firstName = firstName;
        this.surname = surname;
        this.idNumber = idNumber;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + surname;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getIdNumber() { return idNumber; }
}
