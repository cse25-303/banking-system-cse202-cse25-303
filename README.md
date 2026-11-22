# CSE202 Banking System: Core Model (Part B, Section 4)

Simple Java classes for the bank. There is no GUI and no database yet; those come in later weeks.

## What is in the project

    src/bank/
      User.java, Customer.java, IndividualCustomer.java, BusinessCustomer.java, Administrator.java
      Account.java, SavingsAccount.java, InvestmentAccount.java, ChequeAccount.java, InterestBearing.java
      Transaction.java
      TransactionType.java, TransactionStatus.java, AccountType.java   (enums)
      BankTest.java                                                    (runs the bank rules and prints the result)
      EdgeCaseTest.java                                                (44 checks on awkward cases; every line should say PASS)

## How to run (GitHub Codespaces or any computer with Java 11 or newer)

    mkdir -p out
    javac -d out src/bank/*.java
    java -cp out bank.BankTest
    java -cp out bank.EdgeCaseTest

## Bank rules in the code

| Rule | Where |
|---|---|
| Savings needs an opening deposit above 0, never allows withdrawals, pays 0.05% a month | SavingsAccount |
| Investment needs at least BWP 500.00 to open, allows withdrawals, pays 5% a month | InvestmentAccount |
| Cheque needs company name and address, allows withdrawals, pays no interest | ChequeAccount |
| A business customer cannot open a Cheque account | BusinessCustomer.openAccount() |
| Interest is paid at most once a month | payInterest() in Savings and Investment |
| Passwords are stored as SHA-256 hashes (create one with User.hashPassword) | User |
| Money has at most 2 decimal places and must be more than 0 | Account |
| Account lists and history are read-only from outside, so the rules cannot be skipped | Customer, Account |

## Author

CSE25-303, BSc Computer Systems Engineering, Botswana Accountancy College
