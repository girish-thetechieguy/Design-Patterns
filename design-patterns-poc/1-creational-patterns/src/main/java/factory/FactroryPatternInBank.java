/**
 * Benefits:
 * Simplifies account creation process
 * Allows for different account types with varying parameters
 * Makes it easy to introduce new account types in the future
 */

package factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Account interface
interface Account {
    void deposit(double amount);
    void withdraw(double amount);
    double getBalance();
    String getAccountType();
}

// Account Factory
interface AccountFactory {
    Account createAccount();
}

// Concrete account implementations
class SavingsAccount implements Account {

    private static Logger logger = LoggerFactory.getLogger(SavingsAccount.class);

    private double balance;
    private double interestRate;

    public SavingsAccount(double interestRate) {
        this.interestRate = interestRate;
        this.balance = 0;
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            logger.info("Insufficient funds");
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }
}

class CurrentAccount implements Account {
    private static Logger logger = LoggerFactory.getLogger(CurrentAccount.class);
    private double balance;
    private double overdraftLimit;

    public CurrentAccount(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
        this.balance = 0;
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if (balance + overdraftLimit >= amount) {
            balance -= amount;
        } else {
            logger.info("Withdrawal exceeds overdraft limit");
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }
}

// Concrete factories
class SavingsAccountFactory implements AccountFactory {
    private double interestRate;

    public SavingsAccountFactory(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public Account createAccount() {
        return new SavingsAccount(interestRate);
    }
}

class CurrentAccountFactory implements AccountFactory {
    private double overdraftLimit;

    public CurrentAccountFactory(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public Account createAccount() {
        return new CurrentAccount(overdraftLimit);
    }
}


public class FactroryPatternInBank {
    private static Logger logger = LoggerFactory.getLogger(FactroryPatternInBank.class);
    public static void main(String[] args) {
        AccountFactory savingsFactory = new SavingsAccountFactory(2.5);
        Account savingsAccount = savingsFactory.createAccount();
        savingsAccount.deposit(1000);
        logger.info(savingsAccount.getAccountType() + " balance: $" + savingsAccount.getBalance());

        AccountFactory currentFactory = new CurrentAccountFactory(500);
        Account currentAccount = currentFactory.createAccount();
        currentAccount.deposit(2000);
        currentAccount.withdraw(2500);
        logger.info(currentAccount.getAccountType() + " balance: $" + currentAccount.getBalance());
    }
}


// Output:
//  22:35:03.128 [main] INFO factory.FactroryPatternInBank - Savings Account balance: $1000.0
//  22:35:03.135 [main] INFO factory.FactroryPatternInBank - Current Account balance: $-500.0