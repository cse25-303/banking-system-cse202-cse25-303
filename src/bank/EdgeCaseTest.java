package bank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/** Checks the awkward cases (wrong input, rounding, security). Every line should say PASS. */
public class EdgeCaseTest {

    static int checks = 0;
    static int failures = 0;

    static BigDecimal money(String text) { return new BigDecimal(text); }

    static void check(String label, boolean ok) {
        checks++;
        if (!ok) failures++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + label);
    }

    static IndividualCustomer person(int id) {
        return new IndividualCustomer(id, "user" + id, User.hashPassword("pw" + id), "Gaborone", "71000000",
                "Test", "Person", "ID" + id);
    }

    public static void main(String[] args) {

        // ----- amounts -----
        SavingsAccount s = new SavingsAccount("S1", "Main", money("100.00"));
        boolean rejected = false;
        try { s.deposit(money("0.001")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Deposit with 3 decimal places is rejected", rejected);

        rejected = false;
        try { s.deposit(money("0")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Deposit of 0 is rejected", rejected);

        rejected = false;
        try { s.deposit(money("-5")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Negative deposit is rejected", rejected);

        rejected = false;
        try { s.deposit(null); } catch (IllegalArgumentException e) { rejected = true; }
        check("Deposit of null is rejected", rejected);

        check("Balance unchanged after the rejected deposits", s.getBalance().compareTo(money("100.00")) == 0);

        // ----- opening rules -----
        rejected = false;
        try { new InvestmentAccount("I1", "Main", money("499.99")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Investment with 499.99 is rejected", rejected);

        InvestmentAccount inv = new InvestmentAccount("I2", "Main", money("500.00"));
        check("Investment with exactly 500.00 is accepted", inv.getBalance().compareTo(money("500.00")) == 0);

        rejected = false;
        try { new ChequeAccount("C1", "Main", money("-50"), "Firm", "Town"); } catch (IllegalArgumentException e) { rejected = true; }
        check("Cheque with a negative opening deposit is rejected", rejected);

        ChequeAccount chq = new ChequeAccount("C2", "Main", null, "Firm", "Town");
        check("Cheque can open with no deposit (balance 0.00)", chq.getBalance().compareTo(BigDecimal.ZERO) == 0
                && chq.getTransactions().isEmpty());

        // ----- withdrawals -----
        inv.withdraw(money("500.00"));
        check("Can withdraw the whole balance (balance becomes 0.00)", inv.getBalance().compareTo(BigDecimal.ZERO) == 0);

        rejected = false;
        try { inv.withdraw(money("0.01")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Cannot withdraw from an empty account", rejected);

        rejected = false;
        try { chq.withdraw(money("1.005")); } catch (IllegalArgumentException e) { rejected = true; }
        check("Withdrawal with 3 decimal places is rejected", rejected);

        // ----- history -----
        SavingsAccount h = new SavingsAccount("S2", "Main", money("100.00"));
        h.deposit(money("50.50"), "Gift");
        List<Transaction> list = h.getTransactions();
        check("History has 2 lines (opening deposit and gift)", list.size() == 2);
        check("Balance after the gift is 150.50", list.get(1).getBalanceAfter().compareTo(money("150.50")) == 0);
        check("History line keeps its note", list.get(1).getDescription().equals("Gift"));

        boolean blocked = false;
        try { h.getTransactions().clear(); } catch (UnsupportedOperationException e) { blocked = true; }
        check("History cannot be wiped from outside", blocked);

        // ----- customers and accounts -----
        IndividualCustomer p = person(10);
        Account mine = new SavingsAccount("S3", "Main", money("10.00"));
        p.openAccount(mine);
        check("Opening an account sets the owner id", mine.getCustomerId() == 10);

        boolean duplicate = false;
        try { p.openAccount(mine); } catch (IllegalStateException e) { duplicate = true; }
        check("The same account cannot be opened twice", duplicate);

        check("findAccount returns the customer's own account", p.findAccount("S3") == mine);
        check("findAccount returns null for someone else's account", p.findAccount("OTHER") == null);

        blocked = false;
        try { p.getAccounts().add(chq); } catch (UnsupportedOperationException e) { blocked = true; }
        check("Accounts cannot be added behind the customer's back", blocked);

        BusinessCustomer biz = new BusinessCustomer(11, "biz", User.hashPassword("pw"), "Town", "72000000", "Biz Ltd", "R1");
        boolean noCheque = false;
        try { biz.openAccount(new ChequeAccount("C3", "Main", null, "Biz", "Town")); } catch (IllegalStateException e) { noCheque = true; }
        check("Business cannot open a Cheque account", noCheque);
        check("Business has no accounts after the refusal", biz.getAccounts().isEmpty());

        // ----- interest -----
        SavingsAccount i1 = new SavingsAccount("S4", "Main", money("333.33"));
        i1.payInterest();
        check("Savings interest rounds correctly (333.33 x 0.05% = 0.17)", i1.getBalance().compareTo(money("333.50")) == 0);

        InvestmentAccount i2 = new InvestmentAccount("I3", "Main", money("1234.50"));
        i2.payInterest();
        check("Investment interest rounds correctly (1234.50 x 5% = 61.73)", i2.getBalance().compareTo(money("1296.23")) == 0);

        i2.payInterest();
        check("Interest is not paid twice in the same month", i2.getBalance().compareTo(money("1296.23")) == 0);

        check("Interest line is recorded as INTEREST", i2.getTransactions().get(1).getType() == TransactionType.INTEREST);

        SavingsAccount tiny = new SavingsAccount("S5", "Main", money("0.50"));
        tiny.payInterest();
        check("Interest that rounds to 0.00 is not recorded", tiny.getTransactions().size() == 1);

        Administrator admin = new Administrator(1, "admin", User.hashPassword("admin"), "Neo");
        List<Account> mix = new ArrayList<>();
        mix.add(new SavingsAccount("S6", "Main", money("100.00")));
        mix.add(new InvestmentAccount("I4", "Main", money("600.00")));
        mix.add(new ChequeAccount("C4", "Main", money("900.00"), "Firm", "Town"));
        check("Interest run pays 2 of 3 accounts (Cheque earns none)", admin.payMonthlyInterest(mix) == 2);
        check("Second run in the same month pays 0", admin.payMonthlyInterest(mix) == 0);
        check("Empty list pays 0", admin.payMonthlyInterest(new ArrayList<Account>()) == 0);

        // ----- loading from a database later -----
        YearMonth thisMonth = YearMonth.now();
        SavingsAccount loaded = new SavingsAccount("S7", "Main", money("250.00"), LocalDate.of(2026, 1, 5), 7, thisMonth);
        check("A loaded account keeps its balance and has no fake opening deposit",
                loaded.getBalance().compareTo(money("250.00")) == 0 && loaded.getTransactions().isEmpty());
        loaded.payInterest();
        check("A loaded account that was paid this month is not paid again", loaded.getBalance().compareTo(money("250.00")) == 0);
        check("A loaded account keeps its owner and open date", loaded.getCustomerId() == 7
                && loaded.getOpenDate().equals(LocalDate.of(2026, 1, 5)));

        // ----- passwords -----
        IndividualCustomer u = person(20);
        check("Right password logs in", u.login("pw20") && u.isLoggedIn());
        u.logout();
        check("Logout works", !u.isLoggedIn());
        check("Wrong password fails", !u.login("nope") && !u.isLoggedIn());
        check("Null password fails without crashing", !u.login(null));
        check("Empty password fails", !u.login(""));
        check("Stored value is not the real password", !u.getPasswordHash().contains("pw20"));
        check("Change password with a wrong old password fails", !u.changePassword("bad", "new"));
        check("Change password to an empty one fails", !u.changePassword("pw20", ""));
        check("Change password works", u.changePassword("pw20", "new") && u.login("new") && !u.login("pw20"));

        IndividualCustomer fromDb = new IndividualCustomer(30, "x", u.getPasswordHash(), "a", "1", "A", "B", "ID");
        check("A user loaded with a stored hash can log in", fromDb.login("new"));

        System.out.println();
        System.out.println(checks + " checks, " + failures + " failed");
    }
}
