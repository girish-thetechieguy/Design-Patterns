package bridge;

/**
 * The Bridge Pattern decouples an abstraction from its implementation, allowing them to vary independently. In banking, this helps separate
 * Account types (Savings, Checking, Loan)
 * Banking operations (Local, International, Online)
 * Key Benefits
 * Advantage	Banking Example
 * Decouples account types from operations	Add new operations (e.g., CryptoBanking) without changing account classes.
 * Avoids class explosion	No need for LocalSavingsAccount, InternationalCheckingAccount, etc.
 * Runtime flexibility	Switch operations dynamically: account.setOperation(new OnlineBanking())
 *
 * Real-World Analogies
 * Banking Channels:
 * Abstraction: Account types
 * Implementation: Branch, ATM, Mobile App
 *
 * Payment Processing:
 * Abstraction: Payment methods (Credit, Debit)
 * Implementation: Visa, Mastercard, PayPal
 */

abstract class BankAccount {
    protected BankingOperation operation; // Bridge to implementation

    public BankAccount(BankingOperation operation) {
        this.operation = operation;
    }

    abstract void open();
    abstract void deposit(double amount);
}

// Refined Abstractions
class SavingsAccount extends BankAccount {
    public SavingsAccount(BankingOperation operation) {
        super(operation);
    }

    @Override
    void open() {
        System.out.println("Opening Savings Account");
        operation.initialize();
    }

    @Override
    void deposit(double amount) {
        operation.processDeposit(amount);
        System.out.println("Added interest: $" + (amount * 0.02));
    }
}

class CheckingAccount extends BankAccount {
    public CheckingAccount(BankingOperation operation) {
        super(operation);
    }

    @Override
    void open() {
        System.out.println("Opening Checking Account");
        operation.initialize();
    }

    @Override
    void deposit(double amount) {
        operation.processDeposit(amount);
        System.out.println("No interest for checking accounts");
    }
}

interface BankingOperation {
    void initialize();
    void processDeposit(double amount);
}

// Concrete Implementations
class LocalBanking implements BankingOperation {
    @Override
    public void initialize() {
        System.out.println("[Local] Verified ID with national registry");
    }

    @Override
    public void processDeposit(double amount) {
        System.out.println("[Local] Deposited: $" + amount);
    }
}

class InternationalBanking implements BankingOperation {
    @Override
    public void initialize() {
        System.out.println("[International] Verified passport + visa");
    }

    @Override
    public void processDeposit(double amount) {
        System.out.println("[International] Deposited: $" + amount + " (FX fee: $5)");
    }
}

public class BankSystemBridgeClientDemo {
    public static void main(String[] args) {
        // Banking operations
        BankingOperation local = new LocalBanking();
        BankingOperation international = new InternationalBanking();

        // Accounts with different operations
        BankAccount savings = new SavingsAccount(local);
        BankAccount checking = new CheckingAccount(international);

        savings.open();
        savings.deposit(1000);

        System.out.println("-----");

        checking.open();
        checking.deposit(500);
    }
}
