package momento;

import java.util.Stack;

// Originator: The object whose state we save/restore
class BankAccount {
  private double balance;

  public BankAccount(double balance) {
    this.balance = balance;
  }

  // Deposit money
  public void deposit(double amount) {
    balance += amount;
    System.out.printf("Deposited $%.2f | New Balance: $%.2f\n", amount, balance);
  }

  // Withdraw money
  public void withdraw(double amount) {
    if (balance >= amount) {
      balance -= amount;
      System.out.printf("Withdrew $%.2f | New Balance: $%.2f\n", amount, balance);
    } else {
      System.out.println("Insufficient funds!");
    }
  }

  // Save state to a memento
  public AccountMemento save() {
    return new AccountMemento(balance);
  }

  // Restore state from a memento
  public void restore(AccountMemento memento) {
    this.balance = memento.getBalance();
    System.out.printf("Restored balance to $%.2f\n", balance);
  }

  public double getBalance() {
    return balance;
  }
}


// Memento: Immutable snapshot of BankAccount's state
class AccountMemento {
  private final double balance;

  public AccountMemento(double balance) {
    this.balance = balance;
  }

  public double getBalance() {
    return balance;
  }
}

// Caretaker: Manages mementos (undo history)
class TransactionHistory {
  private final Stack<AccountMemento> history = new Stack<>();

  public void saveState(BankAccount account) {
    history.push(account.save());
  }

  public void undo(BankAccount account) {
    if (!history.isEmpty()) {
      account.restore(history.pop());
    } else {
      System.out.println("No transactions to undo!");
    }
  }
}

public class BankClientDemo {
  public static void main(String[] args) {
    BankAccount account = new BankAccount(1000.00);
    TransactionHistory history = new TransactionHistory();

    // Save initial state
    history.saveState(account);
    System.out.println("Initial balance: $" + account.getBalance());

    // Transaction 1: Deposit $200
    account.deposit(200);
    history.saveState(account);

    // Transaction 2: Withdraw $500
    account.withdraw(500);
    history.saveState(account);

    // Undo last transaction (balance rolls back to $1200)
    System.out.println("\nUndoing last transaction...");
    history.undo(account);
    System.out.println("Current balance: $" + account.getBalance());
  }
}
