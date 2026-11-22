package bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** A simple test: it runs the bank rules and prints what happens. No GUI and no database yet. */
public class BankTest {

    static BigDecimal money(String text) {
        return new BigDecimal(text);
    }

    static void title(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }

    public static void main(String[] args) {

        // ---------- 1. Customers ----------
        title("1. Create customers and an administrator");
        IndividualCustomer thabo = new IndividualCustomer(1, "thabo", User.hashPassword("pass123"), "Plot 12, Gaborone",
                "71234567", "Thabo", "Mokoena", "123456789");
        BusinessCustomer kgosi = new BusinessCustomer(2, "kgositraders", User.hashPassword("biz456"), "Main Mall, Francistown",
                "72345678", "Kgosi Traders", "BW00012345");
        Administrator admin = new Administrator(3, "admin", User.hashPassword("admin789"), "Neo Seretse");
        System.out.println(thabo.getDisplayName() + " (individual)");
        System.out.println(kgosi.getDisplayName() + " (business)");
        System.out.println(admin.getDisplayName() + " (administrator)");

        // ---------- 2. Opening accounts ----------
        title("2. Opening accounts (opening deposit rules)");
        SavingsAccount savings = new SavingsAccount("SAV-0001", "Main Branch", money("1000.00"));
        thabo.openAccount(savings);
        System.out.println("Opened: " + savings);

        try {
            new InvestmentAccount("INV-0001", "Main Branch", money("400.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (under BWP 500): " + e.getMessage());
        }
        InvestmentAccount investment = new InvestmentAccount("INV-0001", "Main Branch", money("2000.00"));
        thabo.openAccount(investment);
        System.out.println("Opened: " + investment);

        try {
            new SavingsAccount("SAV-0002", "Main Branch", money("0"));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (no opening deposit): " + e.getMessage());
        }

        try {
            new ChequeAccount("CHQ-0001", "Main Branch", money("0"), "", "");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (no employer details): " + e.getMessage());
        }
        ChequeAccount cheque = new ChequeAccount("CHQ-0001", "Main Branch", money("0"),
                "Debswana", "Orapa, Botswana");
        thabo.openAccount(cheque);
        System.out.println("Opened: " + cheque + " (employer: " + cheque.getCompanyName() + ")");

        System.out.println("Thabo now has " + thabo.getAccounts().size() + " accounts");

        // ---------- 3. Business rules ----------
        title("3. A business cannot open a Cheque account");
        try {
            kgosi.openAccount(new ChequeAccount("CHQ-0002", "Main Branch", money("0"), "Kgosi", "Francistown"));
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        SavingsAccount bizSavings = new SavingsAccount("SAV-0003", "Main Branch", money("50000.00"));
        kgosi.openAccount(bizSavings);
        System.out.println("Business opened: " + bizSavings);

        // ---------- 4. Deposits and withdrawals ----------
        title("4. Deposits and withdrawals");
        cheque.deposit(money("8500.00"), "Salary");
        System.out.println("After salary: " + cheque);
        cheque.withdraw(money("1500.00"));
        System.out.println("After withdrawal: " + cheque);
        investment.withdraw(money("500.00"));
        System.out.println("Investment after withdrawal: " + investment);

        try {
            savings.withdraw(money("100.00"));
        } catch (IllegalStateException e) {
            System.out.println("Rejected (Savings): " + e.getMessage());
        }
        try {
            cheque.withdraw(money("999999.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (too much): " + e.getMessage());
        }
        try {
            savings.deposit(money("-20.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (negative deposit): " + e.getMessage());
        }

        // ---------- 5. Monthly interest ----------
        title("5. Monthly interest (Savings 0.05%, Investment 5%, Cheque none)");
        List<Account> all = new ArrayList<>();
        all.addAll(thabo.getAccounts());
        all.addAll(kgosi.getAccounts());

        int paid = admin.payMonthlyInterest(all);
        System.out.println("Interest paid to " + paid + " accounts");
        for (Account a : all) {
            System.out.println("  " + a);
        }
        int again = admin.payMonthlyInterest(all);
        System.out.println("Run again in the same month: interest paid to " + again + " accounts");

        // ---------- 6. History ----------
        title("6. Transaction history of the Investment account");
        for (Transaction t : investment.getTransactions()) {
            System.out.println("  " + t);
        }

        // ---------- 7. Login and logout ----------
        title("7. Login, logout and change password");
        System.out.println("Wrong password: " + thabo.login("wrong"));
        System.out.println("Right password: " + thabo.login("pass123") + ", logged in = " + thabo.isLoggedIn());
        thabo.logout();
        System.out.println("After logout, logged in = " + thabo.isLoggedIn());
        System.out.println("Change password with wrong old password: " + thabo.changePassword("nope", "new1"));
        System.out.println("Change password with right old password: " + thabo.changePassword("pass123", "new1"));
        System.out.println("Login with new password: " + thabo.login("new1"));
        System.out.println("Stored value is a hash, not the password: " + thabo.getPasswordHash().substring(0, 16) + "...");
    }
}
