package mediator;
import java.util.ArrayList;
import java.util.List;

// Mediator Interface
interface BankMediator {
  void transfer(Account sender, Account receiver, double amount);
  void registerAccount(Account account);
}

// Concrete Mediator (Bank)
class Bank implements BankMediator {
  private final List<Account> accounts = new ArrayList<>();

  @Override
  public void registerAccount(Account account) {
    accounts.add(account);
  }

  @Override
  public void transfer(Account sender, Account receiver, double amount) {
    if (sender.getBalance() >= amount) {
      sender.withdraw(amount);
      receiver.deposit(amount);
      System.out.printf("[Bank] Transferred $%.2f from %s to %s\n",
          amount, sender.getName(), receiver.getName());
    } else {
      System.out.println("[Bank] Transfer failed: Insufficient funds.");
    }
  }
}

// Colleague (Account)
class Account {
  private final String name;
  private double balance;
  private final BankMediator bank;

  public Account(String name, double balance, BankMediator bank) {
    this.name = name;
    this.balance = balance;
    this.bank = bank;
    bank.registerAccount(this); // Register with mediator
  }

  public void sendMoney(Account receiver, double amount) {
    bank.transfer(this, receiver, amount); // Delegate to mediator
  }

  public void deposit(double amount) {
    balance += amount;
  }

  public void withdraw(double amount) {
    balance -= amount;
  }

  // Getters
  public String getName() { return name; }
  public double getBalance() { return balance; }
}

public class BankClientDemo {
  public static void main(String[] args) {
    // 1. Create Mediator (Bank)
    BankMediator bank = new Bank();

    // 2. Create Accounts (Colleagues)
    Account alice = new Account("Alice", 1000, bank);
    Account bob = new Account("Bob", 500, bank);

    // 3. Transfer via Mediator
    alice.sendMoney(bob, 200);  // Success
    bob.sendMoney(alice, 1000); // Fails (insufficient funds)

    // 4. Check Balances
    System.out.printf("%s's balance: $%.2f\n", alice.getName(), alice.getBalance());
    System.out.printf("%s's balance: $%.2f\n", bob.getName(), bob.getBalance());
  }
}
