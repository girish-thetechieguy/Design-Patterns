package prototype;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Key Features:
 * Prototype Interface: BankAccountPrototype defines the cloning contract
 * Copy Constructors: Each account type implements proper copying
 * Deep Copy: Mutable state is handled correctly
 * Registry: Central repository of account prototypes
 * Type Safety: Each account type maintains its specific features
 * Real-world Banking Rules: Implements actual banking constraints
 *
 * Benefits for Banking Systems:
 * Rapid Account Creation: New accounts can be created by cloning prototypes
 * Consistent Configurations: Ensures all accounts of a type start with proper settings
 * Reduced Database Load: Avoids repeatedly loading default configurations
 * Flexibility: New account types can be added at runtime
 * Maintainability: Account creation logic is centralized
 */

interface BankAccountPrototype {
    BankAccountPrototype copy();
    String getAccountDetails();
    void setBalance(double balance);
    double getBalance();
    String getAccountNumber();

    void deposit(double v);
}

abstract class BankAccount implements BankAccountPrototype {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected double interestRate;
    protected LocalDate openingDate;

    public BankAccount(String accountNumber, String accountHolder,
                       double balance, double interestRate) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.interestRate = interestRate;
        this.openingDate = LocalDate.now();
    }

    // Copy constructor for prototype pattern
    protected BankAccount(BankAccount source) {
        this.accountNumber = generateNewAccountNumber(source.accountNumber);
        this.accountHolder = source.accountHolder;
        this.balance = source.balance;
        this.interestRate = source.interestRate;
        this.openingDate = LocalDate.now();
    }

    @Override
    public abstract BankAccount copy();

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String getAccountDetails() {
        return String.format("Account: %s | Holder: %s | Balance: $%.2f | Rate: %.2f%% | Opened: %s",
                accountNumber, accountHolder, balance, interestRate, openingDate);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public abstract void withdraw(double amount) throws InsufficientFundsException;

    private String generateNewAccountNumber(String originalNumber) {
        // In a real system, this would generate a proper new account number
        return originalNumber + "-C" + UUID.randomUUID().toString().substring(0, 4);
    }
}

class SavingsAccount extends BankAccount {
    private double minimumBalance;
    private int monthlyWithdrawalLimit;
    private int withdrawalsThisMonth;

    public SavingsAccount(String accountNumber, String accountHolder,
                          double balance, double interestRate,
                          double minimumBalance, int monthlyWithdrawalLimit) {
        super(accountNumber, accountHolder, balance, interestRate);
        this.minimumBalance = minimumBalance;
        this.monthlyWithdrawalLimit = monthlyWithdrawalLimit;
        this.withdrawalsThisMonth = 0;
    }

    // Copy constructor
    protected SavingsAccount(SavingsAccount source) {
        super(source);
        this.minimumBalance = source.minimumBalance;
        this.monthlyWithdrawalLimit = source.monthlyWithdrawalLimit;
        this.withdrawalsThisMonth = 0; // Reset for new account
    }

    @Override
    public SavingsAccount copy() {
        return new SavingsAccount(this);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (withdrawalsThisMonth >= monthlyWithdrawalLimit) {
            throw new InsufficientFundsException("Monthly withdrawal limit reached");
        }
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException("Cannot go below minimum balance");
        }
        balance -= amount;
        withdrawalsThisMonth++;
    }

    @Override
    public String getAccountDetails() {
        return super.getAccountDetails() +
                String.format(" | Type: Savings | Min Balance: $%.2f | Withdrawals: %d/%d",
                        minimumBalance, withdrawalsThisMonth, monthlyWithdrawalLimit);
    }

    public void resetMonthlyWithdrawals() {
        withdrawalsThisMonth = 0;
    }
}

class CheckingAccount extends BankAccount {
    private double overdraftLimit;
    private boolean hasDebitCard;

    public CheckingAccount(String accountNumber, String accountHolder,
                           double balance, double interestRate,
                           double overdraftLimit, boolean hasDebitCard) {
        super(accountNumber, accountHolder, balance, interestRate);
        this.overdraftLimit = overdraftLimit;
        this.hasDebitCard = hasDebitCard;
    }

    // Copy constructor
    protected CheckingAccount(CheckingAccount source) {
        super(source);
        this.overdraftLimit = source.overdraftLimit;
        this.hasDebitCard = source.hasDebitCard;
    }

    @Override
    public CheckingAccount copy() {
        return new CheckingAccount(this);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance - amount < -overdraftLimit) {
            throw new InsufficientFundsException("Exceeds overdraft limit");
        }
        balance -= amount;
    }

    @Override
    public String getAccountDetails() {
        return super.getAccountDetails() +
                String.format(" | Type: Checking | Overdraft: $%.2f | Debit Card: %s",
                        overdraftLimit, hasDebitCard ? "Yes" : "No");
    }
}

class FixedDepositAccount extends BankAccount {
    private LocalDate maturityDate;
    private boolean earlyWithdrawalPenalty;

    public FixedDepositAccount(String accountNumber, String accountHolder,
                               double balance, double interestRate,
                               int termMonths, boolean earlyWithdrawalPenalty) {
        super(accountNumber, accountHolder, balance, interestRate);
        this.maturityDate = LocalDate.now().plusMonths(termMonths);
        this.earlyWithdrawalPenalty = earlyWithdrawalPenalty;
    }

    // Copy constructor
    protected FixedDepositAccount(FixedDepositAccount source) {
        super(source);
        this.maturityDate = source.maturityDate;
        this.earlyWithdrawalPenalty = source.earlyWithdrawalPenalty;
    }

    @Override
    public FixedDepositAccount copy() {
        return new FixedDepositAccount(this);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount != balance) {
            throw new UnsupportedOperationException("Partial withdrawals not allowed for fixed deposits");
        }
        if (LocalDate.now().isBefore(maturityDate)){
            if (earlyWithdrawalPenalty) {
                double penalty = balance * 0.05; // 5% penalty
                balance -= penalty;
                System.out.println("Early withdrawal penalty applied: $" + penalty);
            }
        }
        balance = 0;
    }

    @Override
    public String getAccountDetails() {
        return super.getAccountDetails() +
                String.format(" | Type: Fixed Deposit | Matures: %s | Penalty: %s",
                        maturityDate, earlyWithdrawalPenalty ? "Yes" : "No");
    }
}

class BankAccountRegistry {
    private static final Map<String, BankAccountPrototype> prototypes = new HashMap<>();

    static {
        // Initialize with standard account prototypes
        prototypes.put("SAVINGS", new SavingsAccount(
                "SAV-0001", "Default Holder", 0.0, 1.5,
                100.0, 6));

        prototypes.put("CHECKING", new CheckingAccount(
                "CHK-0001", "Default Holder", 0.0, 0.1,
                500.0, true));

        prototypes.put("FIXED-1YR", new FixedDepositAccount(
                "FD-0001", "Default Holder", 0.0, 3.5,
                12, true));
    }

    public static BankAccountPrototype getAccountPrototype(String type) {
        BankAccountPrototype prototype = prototypes.get(type);
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown account type: " + type);
        }
        return prototype.copy();
    }

    public static void registerAccountPrototype(String type, BankAccountPrototype prototype) {
        prototypes.put(type, prototype);
    }
}

public class BankPrototypePatternClientDemo {

    public static void main(String[] args) {
            // Create accounts by cloning prototypes
            BankAccountPrototype savingsAccount = BankAccountRegistry.getAccountPrototype("SAVINGS");
            savingsAccount.setBalance(5000.0);
            ((SavingsAccount)savingsAccount).resetMonthlyWithdrawals();

            BankAccountPrototype checkingAccount = BankAccountRegistry.getAccountPrototype("CHECKING");
            checkingAccount.setBalance(2500.0);

            BankAccountPrototype fixedDeposit = BankAccountRegistry.getAccountPrototype("FIXED-1YR");
            fixedDeposit.setBalance(10000.0);

            // Customize account holders
            ((BankAccount)savingsAccount).accountHolder = "John Doe";
            ((BankAccount)checkingAccount).accountHolder = "Jane Smith";
            ((BankAccount)fixedDeposit).accountHolder = "Robert Johnson";

            // Perform transactions
            savingsAccount.deposit(500.0);
            checkingAccount.deposit(300.0);

            System.out.println("=== Bank Accounts ===");
            System.out.println(savingsAccount.getAccountDetails());
            System.out.println(checkingAccount.getAccountDetails());
            System.out.println(fixedDeposit.getAccountDetails());

            // Create and register a new account type
            FixedDepositAccount highYieldFD = new FixedDepositAccount(
                    "FD-HY-001", "Default Holder", 0.0, 5.0,
                    24, false);
            BankAccountRegistry.registerAccountPrototype("FIXED-2YR-HY", highYieldFD);

            // Use the new prototype
            BankAccountPrototype newFD = BankAccountRegistry.getAccountPrototype("FIXED-2YR-HY");
            newFD.setBalance(15000.0);
            ((BankAccount)newFD).accountHolder = "Alice Brown";

            System.out.println("\nNew High-Yield Fixed Deposit:");
            System.out.println(newFD.getAccountDetails());


    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}