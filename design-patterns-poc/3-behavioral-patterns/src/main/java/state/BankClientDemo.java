package state;

interface AccountState {
  void deposit(double amount, Account account);
  void withdraw(double amount, Account account);
  void freezeAccount(Account account);
  void closeAccount(Account account);
  void activateAccount(Account account);
}

// Active State
class ActiveState implements AccountState {
  @Override
  public void deposit(double amount, Account account) {
    account.setBalance(account.getBalance() + amount);
    System.out.println("Deposited $" + amount + ". New balance: $" + account.getBalance());
  }

  @Override
  public void withdraw(double amount, Account account) {
    if (account.getBalance() >= amount) {
      account.setBalance(account.getBalance() - amount);
      System.out.println("Withdrew $" + amount + ". New balance: $" + account.getBalance());
    } else {
      System.out.println("Insufficient funds!");
    }
  }

  @Override
  public void freezeAccount(Account account) {
    account.setState(new FrozenState());
    System.out.println("Account has been frozen.");
  }

  @Override
  public void closeAccount(Account account) {
    account.setState(new ClosedState());
    System.out.println("Account has been closed.");
  }

  @Override
  public void activateAccount(Account account) {
    System.out.println("Account is already active.");
  }
}

// Frozen State
class FrozenState implements AccountState {
  @Override
  public void deposit(double amount, Account account) {
    System.out.println("Cannot deposit. Account is frozen.");
  }

  @Override
  public void withdraw(double amount, Account account) {
    System.out.println("Cannot withdraw. Account is frozen.");
  }

  @Override
  public void freezeAccount(Account account) {
    System.out.println("Account is already frozen.");
  }

  @Override
  public void closeAccount(Account account) {
    account.setState(new ClosedState());
    System.out.println("Account has been closed.");
  }

  @Override
  public void activateAccount(Account account) {
    account.setState(new ActiveState());
    System.out.println("Account has been activated.");
  }
}

// Closed State
class ClosedState implements AccountState {
  @Override
  public void deposit(double amount, Account account) {
    System.out.println("Cannot deposit. Account is closed.");
  }

  @Override
  public void withdraw(double amount, Account account) {
    System.out.println("Cannot withdraw. Account is closed.");
  }

  @Override
  public void freezeAccount(Account account) {
    System.out.println("Cannot freeze. Account is closed.");
  }

  @Override
  public void closeAccount(Account account) {
    System.out.println("Account is already closed.");
  }

  @Override
  public void activateAccount(Account account) {
    System.out.println("Cannot activate. Account is closed.");
  }
}

class Account {
  private AccountState state;
  private final String accountNumber;
  private double balance;

  public Account(String accountNumber) {
    this.accountNumber = accountNumber;
    this.balance = 0.0;
    this.state = new ActiveState(); // Start in active state
  }

  public void setState(AccountState state) {
    this.state = state;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  // Delegate all actions to the current state
  public void deposit(double amount) {
    state.deposit(amount, this);
  }

  public void withdraw(double amount) {
    state.withdraw(amount, this);
  }

  public void freezeAccount() {
    state.freezeAccount(this);
  }

  public void closeAccount() {
    state.closeAccount(this);
  }

  public void activateAccount() {
    state.activateAccount(this);
  }

  public void displayState() {
    System.out.println("Account Number: " + accountNumber);
    System.out.println("Balance: $" + balance);
    System.out.println("State: " + state.getClass().getSimpleName());
    System.out.println("---------------------");
  }
}


public class BankClientDemo {
  public static void main(String[] args) {
    Account account = new Account("ACC123456");
    account.displayState();

    // Active state operations
    account.deposit(1000);
    account.withdraw(200);
    account.displayState();

    // Freeze the account
    account.freezeAccount();
    account.displayState();

    // Try operations while frozen
    account.deposit(500);  // Will be rejected
    account.withdraw(100); // Will be rejected

    // Reactivate account
    account.activateAccount();
    account.displayState();

    // Close the account
    account.closeAccount();
    account.displayState();

    // Try operations after closing
    account.deposit(1000);  // Will be rejected
    account.activateAccount(); // Will be rejected
  }
}
