package builder;

import java.time.LocalDate;
import java.util.UUID;

class BankAccount {
    private final String accountNumber;
    private final String accountHolderName;
    private final AccountType accountType;
    private final double balance;
    private final double interestRate;
    private final LocalDate openingDate;
    private final boolean isJointAccount;
    private final String branchCode;
    private final boolean isOverdraftAllowed;
    private final double overdraftLimit;

    private BankAccount(BankAccountBuilder builder) {
        this.accountNumber = builder.accountNumber;
        this.accountHolderName = builder.accountHolderName;
        this.accountType = builder.accountType;
        this.balance = builder.balance;
        this.interestRate = builder.interestRate;
        this.openingDate = builder.openingDate;
        this.isJointAccount = builder.isJointAccount;
        this.branchCode = builder.branchCode;
        this.isOverdraftAllowed = builder.isOverdraftAllowed;
        this.overdraftLimit = builder.overdraftLimit;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public AccountType getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public double getInterestRate() { return interestRate; }
    public LocalDate getOpeningDate() { return openingDate; }
    public boolean isJointAccount() { return isJointAccount; }
    public String getBranchCode() { return branchCode; }
    public boolean isOverdraftAllowed() { return isOverdraftAllowed; }
    public double getOverdraftLimit() { return overdraftLimit; }

    @Override
    public String toString() {
        return String.format("""
            Bank Account Details:
            Account Number: %s
            Holder: %s
            Type: %s
            Balance: $%.2f
            Interest Rate: %.2f%%
            Opened: %s
            Branch: %s
            Overdraft: %s (Limit: $%.2f)
            Joint Account: %s
            """,
                accountNumber, accountHolderName, accountType, balance,
                interestRate, openingDate, branchCode,
                isOverdraftAllowed ? "Allowed" : "Not Allowed",
                overdraftLimit, isJointAccount ? "Yes" : "No");
    }

    // Account Type Enum
    public enum AccountType {
        SAVINGS, CURRENT, FIXED_DEPOSIT, SALARY, STUDENT
    }

    // Builder Class
    public static class BankAccountBuilder {
        // Required parameters
        private final String accountHolderName;
        private final AccountType accountType;

        // Optional parameters with defaults
        private String accountNumber = UUID.randomUUID().toString();
        private double balance = 0.0;
        private double interestRate = 0.0;
        private LocalDate openingDate = LocalDate.now();
        private boolean isJointAccount = false;
        private String branchCode = "MAIN";
        private boolean isOverdraftAllowed = false;
        private double overdraftLimit = 0.0;

        public BankAccountBuilder(String accountHolderName, AccountType accountType) {
            this.accountHolderName = accountHolderName;
            this.accountType = accountType;
        }

        public BankAccountBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public BankAccountBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public BankAccountBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public BankAccountBuilder openingDate(LocalDate openingDate) {
            this.openingDate = openingDate;
            return this;
        }

        public BankAccountBuilder isJointAccount(boolean isJointAccount) {
            this.isJointAccount = isJointAccount;
            return this;
        }

        public BankAccountBuilder branchCode(String branchCode) {
            this.branchCode = branchCode;
            return this;
        }

        public BankAccountBuilder isOverdraftAllowed(boolean isOverdraftAllowed) {
            this.isOverdraftAllowed = isOverdraftAllowed;
            return this;
        }

        public BankAccountBuilder overdraftLimit(double overdraftLimit) {
            this.overdraftLimit = overdraftLimit;
            return this;
        }

        public BankAccount build() {
            validate();
            return new BankAccount(this);
        }

        private void validate() {
            if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
                throw new IllegalArgumentException("Account holder name cannot be empty");
            }
            if (balance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
            if (interestRate < 0) {
                throw new IllegalArgumentException("Interest rate cannot be negative");
            }
            if (isOverdraftAllowed && overdraftLimit <= 0) {
                throw new IllegalArgumentException("Overdraft limit must be positive when overdraft is allowed");
            }
            if (accountType == AccountType.SAVINGS && isOverdraftAllowed) {
                throw new IllegalArgumentException("Savings accounts cannot have overdraft");
            }
        }
    }
}

public class BankBuilderDesignDemo {
    public static void main(String[] args) {
        // Create different types of bank accounts using the builder

        // Basic savings account
        BankAccount savingsAccount = new BankAccount.BankAccountBuilder("John Doe", BankAccount.AccountType.SAVINGS)
                .balance(5000.00)
                .interestRate(2.5)
                .branchCode("NYC001")
                .build();

        // Current account with overdraft
        BankAccount currentAccount = new BankAccount.BankAccountBuilder("Jane Smith", BankAccount.AccountType.CURRENT)
                .balance(10000.00)
                .isOverdraftAllowed(true)
                .overdraftLimit(5000.00)
                .branchCode("LON002")
                .build();

        // Joint fixed deposit account
        BankAccount fixedDeposit = new BankAccount.BankAccountBuilder("Robert & Emily Johnson",
                BankAccount.AccountType.FIXED_DEPOSIT)
                .balance(25000.00)
                .interestRate(5.0)
                .isJointAccount(true)
                .openingDate(LocalDate.of(2023, 1, 15))
                .build();

        // Display account details
        System.out.println(savingsAccount);
        System.out.println(currentAccount);
        System.out.println(fixedDeposit);

        // Example of invalid account creation (will throw exception)
        try {
            BankAccount invalidAccount = new BankAccount.BankAccountBuilder("", BankAccount.AccountType.SAVINGS)
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }
}
