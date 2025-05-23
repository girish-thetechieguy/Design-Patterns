package stratergy;

interface InterestCalculationStrategy {
  double calculateInterest(double accountBalance);
}

// Savings Account Interest (2% annual)
class SavingsAccountInterest implements InterestCalculationStrategy {
  private static final double INTEREST_RATE = 0.02;

  @Override
  public double calculateInterest(double accountBalance) {
    return accountBalance * INTEREST_RATE;
  }
}

// Checking Account Interest (0.1% annual)
class CheckingAccountInterest implements InterestCalculationStrategy {
  private static final double INTEREST_RATE = 0.001;

  @Override
  public double calculateInterest(double accountBalance) {
    return accountBalance * INTEREST_RATE;
  }
}

// Loan Account Interest (7% annual - charged to customer)
class LoanAccountInterest implements InterestCalculationStrategy {
  private static final double INTEREST_RATE = 0.07;

  @Override
  public double calculateInterest(double accountBalance) {
    return accountBalance * INTEREST_RATE;
  }
}

class BankAccount {
  private final String accountNumber;
  private double balance;
  private InterestCalculationStrategy interestStrategy;

  public BankAccount(String accountNumber, double initialBalance,
                     InterestCalculationStrategy interestStrategy) {
    this.accountNumber = accountNumber;
    this.balance = initialBalance;
    this.interestStrategy = interestStrategy;
  }

  public void setInterestStrategy(InterestCalculationStrategy newStrategy) {
    this.interestStrategy = newStrategy;
  }

  public void applyInterest() {
    double interest = interestStrategy.calculateInterest(balance);
    balance += interest;
    System.out.printf("Applied %s interest: $%.2f%n",
        interestStrategy.getClass().getSimpleName(), interest);
  }

  public void displayBalance() {
    System.out.printf("Account %s balance: $%.2f%n", accountNumber, balance);
  }
}

public class BankClientDemo {
  public static void main(String[] args) {
    // Create accounts with different strategies
    BankAccount savings = new BankAccount("SAV001", 5000,
        new SavingsAccountInterest());
    BankAccount checking = new BankAccount("CHK001", 3000,
        new CheckingAccountInterest());
    BankAccount loan = new BankAccount("LOAN001", -10000,
        new LoanAccountInterest());

    // Apply interest
    System.out.println("--- Initial Balances ---");
    savings.displayBalance();
    checking.displayBalance();
    loan.displayBalance();

    System.out.println("\n--- Applying Interest ---");
    savings.applyInterest();
    checking.applyInterest();
    loan.applyInterest();

    System.out.println("\n--- Updated Balances ---");
    savings.displayBalance();
    checking.displayBalance();
    loan.displayBalance();

    // Change strategy dynamically
    System.out.println("\n--- Changing Checking to Savings Strategy ---");
    checking.setInterestStrategy(new SavingsAccountInterest());
    checking.applyInterest();
    checking.displayBalance();
  }
}
