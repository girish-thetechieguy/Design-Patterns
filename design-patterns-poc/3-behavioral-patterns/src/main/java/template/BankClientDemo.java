package template;

interface InterestCalculationStrategy {
  double calculateInterest(double balance);
}

class SavingsAccountInterest implements InterestCalculationStrategy {
  private static final double RATE = 0.02; // 2% annual interest

  @Override
  public double calculateInterest(double balance) {
    return balance * RATE;
  }
}

class CheckingAccountInterest implements InterestCalculationStrategy {
  private static final double RATE = 0.005; // 0.5% annual interest

  @Override
  public double calculateInterest(double balance) {
    return balance * RATE;
  }
}

class LoanInterest implements InterestCalculationStrategy {
  private static final double RATE = 0.05; // 5% annual interest (charged)

  @Override
  public double calculateInterest(double balance) {
    return balance * RATE; // Returns a positive value (bank earns this)
  }
}

class BankAccount {
  final String accountNumber;
  private double balance;
  private InterestCalculationStrategy interestStrategy;

  public BankAccount(String accountNumber, double balance, InterestCalculationStrategy strategy) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.interestStrategy = strategy;
  }

  // Change strategy at runtime
  public void setInterestStrategy(InterestCalculationStrategy newStrategy) {
    this.interestStrategy = newStrategy;
  }

  // Apply interest based on the current strategy
  public void applyInterest() {
    double interest = interestStrategy.calculateInterest(balance);
    balance += interest; // For loans, this increases debt; for savings, it adds money
    System.out.printf("Applied interest: $%.2f (New Balance: $%.2f)\n", interest, balance);
  }

//  public void displayBalance() {
//    System.out.printf("Account %s | Balance: $%.2f\n", accountNumber, balance);
//  }
}


public class BankClientDemo {
  public static void main(String[] args) {
    // Create accounts with different strategies
    BankAccount savings = new BankAccount("SAV001", 5000, new SavingsAccountInterest());
    BankAccount checking = new BankAccount("CHK001", 3000, new CheckingAccountInterest());
    BankAccount loan = new BankAccount("LOAN001", -10000, new LoanInterest());

    // Apply interest
    System.out.println("--- Applying Interest ---");
    savings.applyInterest();
    checking.applyInterest();
    loan.applyInterest();

    // Change strategy dynamically (e.g., promotional rate)
    System.out.println("\n--- Changing Checking to Savings Strategy ---");
    checking.setInterestStrategy(new SavingsAccountInterest());
    checking.applyInterest();
  }
}
