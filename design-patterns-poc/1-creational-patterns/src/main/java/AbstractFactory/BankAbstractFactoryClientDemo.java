package AbstractFactory;

/**
 * Key Features of This Implementation:
 * Family of Related Products: Creates accounts, loans, and cards that work together
 * Consistency: Ensures all products are compatible (all personal or all business)
 * Flexibility: Easy to add new product types or new customer segments
 * Encapsulation: Hides concrete product implementations from clients
 * Single Responsibility: Each factory handles creation of one product family
 * Open/Closed Principle: New product families can be added without modifying existing code
 */

interface Account {
    String getAccountNumber();
    String getAccountType();
    double getBalance();
    void deposit(double amount);
    void withdraw(double amount) throws InsufficientFundsException;
}

class PersonalAccount implements Account {
    private String accountNumber;
    private double balance;

    public PersonalAccount(String accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
    }

    @Override public String getAccountNumber() { return accountNumber; }
    @Override public String getAccountType() { return "Personal"; }
    @Override public double getBalance() { return balance; }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new InsufficientFundsException();
        balance -= amount;
    }
}

class BusinessAccount implements Account {
    private String accountNumber;
    private double balance;
    private double overdraftLimit;

    public BusinessAccount(String accountNumber, double overdraftLimit) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.overdraftLimit = overdraftLimit;
    }

    @Override public String getAccountNumber() { return accountNumber; }
    @Override public String getAccountType() { return "Business"; }
    @Override public double getBalance() { return balance; }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance + overdraftLimit) throw new InsufficientFundsException();
        balance -= amount;
    }
}

interface Loan {
    String getLoanNumber();
    double getAmount();
    double getInterestRate();
    void makePayment(double amount);
}

class PersonalLoan implements Loan {
    private String loanNumber;
    private double amount;
    private double interestRate;

    public PersonalLoan(String loanNumber, double amount, double interestRate) {
        this.loanNumber = loanNumber;
        this.amount = amount;
        this.interestRate = interestRate;
    }

    @Override public String getLoanNumber() { return loanNumber; }
    @Override public double getAmount() { return amount; }
    @Override public double getInterestRate() { return interestRate; }

    @Override
    public void makePayment(double payment) {
        if (payment <= 0) throw new IllegalArgumentException("Payment must be positive");
        amount -= payment;
    }
}

class BusinessLoan implements Loan {
    private String loanNumber;
    private double amount;
    private double interestRate;
    private double earlyPaymentFee;

    public BusinessLoan(String loanNumber, double amount, double interestRate, double earlyPaymentFee) {
        this.loanNumber = loanNumber;
        this.amount = amount;
        this.interestRate = interestRate;
        this.earlyPaymentFee = earlyPaymentFee;
    }

    @Override public String getLoanNumber() { return loanNumber; }
    @Override public double getAmount() { return amount; }
    @Override public double getInterestRate() { return interestRate; }

    @Override
    public void makePayment(double payment) {
        if (payment <= 0) throw new IllegalArgumentException("Payment must be positive");
        if (payment > amount * 0.5) {
            amount -= (payment - earlyPaymentFee);
        } else {
            amount -= payment;
        }
    }
}

interface Card {
    String getCardNumber();
    String getCardType();
    double getCreditLimit();
    void makePurchase(double amount) throws InsufficientCreditException;
}

class PersonalCard implements Card {
    private String cardNumber;
    private double creditLimit;
    private double balance;

    public PersonalCard(String cardNumber, double creditLimit) {
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.balance = 0.0;
    }

    @Override public String getCardNumber() { return cardNumber; }
    @Override public String getCardType() { return "Personal"; }
    @Override public double getCreditLimit() { return creditLimit; }

    @Override
    public void makePurchase(double amount) throws InsufficientCreditException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (balance + amount > creditLimit) throw new InsufficientCreditException();
        balance += amount;
    }
}

class BusinessCard implements Card {
    private String cardNumber;
    private double creditLimit;
    private double balance;
    private double cashbackRate;

    public BusinessCard(String cardNumber, double creditLimit, double cashbackRate) {
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.cashbackRate = cashbackRate;
        this.balance = 0.0;
    }

    @Override public String getCardNumber() { return cardNumber; }
    @Override public String getCardType() { return "Business"; }
    @Override public double getCreditLimit() { return creditLimit; }

    @Override
    public void makePurchase(double amount) throws InsufficientCreditException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (balance + amount > creditLimit) throw new InsufficientCreditException();
        balance += amount;
        creditLimit += amount * cashbackRate / 100; // Add cashback to limit
    }
}

interface BankFactory {
    Account createAccount(String accountNumber);
    Loan createLoan(String loanNumber, double amount, double rate);
    Card createCard(String cardNumber, double creditLimit);
}

class PersonalBankingFactory implements BankFactory {
    @Override
    public Account createAccount(String accountNumber) {
        return new PersonalAccount(accountNumber);
    }

    @Override
    public Loan createLoan(String loanNumber, double amount, double rate) {
        return new PersonalLoan(loanNumber, amount, rate);
    }

    @Override
    public Card createCard(String cardNumber, double creditLimit) {
        return new PersonalCard(cardNumber, creditLimit);
    }
}

class BusinessBankingFactory implements BankFactory {
    private static final double BUSINESS_OVERDRAFT = 10000.0;
    private static final double EARLY_PAYMENT_FEE = 50.0;
    private static final double CASHBACK_RATE = 1.5;

    @Override
    public Account createAccount(String accountNumber) {
        return new BusinessAccount(accountNumber, BUSINESS_OVERDRAFT);
    }

    @Override
    public Loan createLoan(String loanNumber, double amount, double rate) {
        return new BusinessLoan(loanNumber, amount, rate, EARLY_PAYMENT_FEE);
    }

    @Override
    public Card createCard(String cardNumber, double creditLimit) {
        return new BusinessCard(cardNumber, creditLimit, CASHBACK_RATE);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds for this operation");
    }
}

class InsufficientCreditException extends Exception {
    public InsufficientCreditException() {
        super("Purchase would exceed credit limit");
    }
}

public class BankAbstractFactoryClientDemo {
    private Account account;
    private Loan loan;
    private Card card;

    public BankAbstractFactoryClientDemo(BankFactory factory, String accountNumber, String loanNumber, String cardNumber) {
        this.account = factory.createAccount(accountNumber);
        this.loan = factory.createLoan(loanNumber, 10000, 5.0);
        this.card = factory.createCard(cardNumber, 5000);
    }

    public void displayProducts() {
        System.out.println("Account: " + account.getAccountNumber() +
                " (" + account.getAccountType() + ")");
        System.out.println("Loan: " + loan.getLoanNumber() +
                " Amount: $" + loan.getAmount() +
                " Rate: " + loan.getInterestRate() + "%");
        System.out.println("Card: " + card.getCardNumber() +
                " (" + card.getCardType() + ") Limit: $" + card.getCreditLimit());
    }

    public static void main(String[] args) {
        // Create personal banking products
        BankFactory personalFactory = new PersonalBankingFactory();
        BankAbstractFactoryClientDemo personalClient = new BankAbstractFactoryClientDemo(
                personalFactory,
                "P-123456",
                "L-P-7890",
                "C-P-4567"
        );
        System.out.println("Personal Banking Products:");
        personalClient.displayProducts();

        System.out.println("\n------------------------\n");

        // Create business banking products
        BankFactory businessFactory = new BusinessBankingFactory();
        BankAbstractFactoryClientDemo businessClient = new BankAbstractFactoryClientDemo(
                businessFactory,
                "B-987654",
                "L-B-3210",
                "C-B-7654"
        );
        System.out.println("Business Banking Products:");
        businessClient.displayProducts();
    }
}
