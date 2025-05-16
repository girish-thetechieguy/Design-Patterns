package iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

interface TransactionIterator {
  boolean hasNext();
  Transaction next();
}

class Transaction {
  private String id;
  private double amount;
  private String type; // "DEPOSIT" or "WITHDRAWAL"

  public Transaction(String id, double amount, String type) {
    this.id = id;
    this.amount = amount;
    this.type = type;
  }

  // Getters
  public String getId() { return id; }
  public double getAmount() { return amount; }
  public String getType() { return type; }

  @Override
  public String toString() {
    return type + " (" + id + "): $" + amount;
  }
}

class CheckingAccountIterator implements TransactionIterator {
  private Transaction[] transactions;
  private int position = 0;

  public CheckingAccountIterator(Transaction[] transactions) {
    this.transactions = transactions;
  }

  @Override
  public boolean hasNext() {
    return position < transactions.length && transactions[position] != null;
  }

  @Override
  public Transaction next() {
    if (!hasNext()) throw new NoSuchElementException();
    return transactions[position++];
  }
}

class SavingsAccountIterator implements TransactionIterator {
  private List<Transaction> transactions;
  private int index = 0;

  public SavingsAccountIterator(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public boolean hasNext() {
    return index < transactions.size();
  }

  @Override
  public Transaction next() {
    if (!hasNext()) throw new NoSuchElementException();
    return transactions.get(index++);
  }
}

interface BankAccount {
  TransactionIterator createIterator();
  String getAccountType();
}

class CheckingAccount implements BankAccount {
  private Transaction[] transactions = new Transaction[100];
  private int count = 0;

  public void addTransaction(Transaction t) {
    if (count < transactions.length) {
      transactions[count++] = t;
    }
  }

  @Override
  public TransactionIterator createIterator() {
    return new CheckingAccountIterator(transactions);
  }

  @Override
  public String getAccountType() {
    return "CHECKING";
  }
}

class SavingsAccount implements BankAccount {
  private List<Transaction> transactions = new ArrayList<>();

  public void addTransaction(Transaction t) {
    transactions.add(t);
  }

  @Override
  public TransactionIterator createIterator() {
    return new SavingsAccountIterator(transactions);
  }

  @Override
  public String getAccountType() {
    return "SAVINGS";
  }
}

public class BankClientDemo {
  public static void main(String[] args) {
    // Checking Account (Array-based)
    CheckingAccount checking = new CheckingAccount();
    checking.addTransaction(new Transaction("T1001", 500.0, "DEPOSIT"));
    checking.addTransaction(new Transaction("T1002", 200.0, "WITHDRAWAL"));

    // Savings Account (List-based)
    SavingsAccount savings = new SavingsAccount();
    savings.addTransaction(new Transaction("T2001", 1000.0, "DEPOSIT"));
    savings.addTransaction(new Transaction("T2002", 300.0, "WITHDRAWAL"));

    // Generate statements uniformly
    printStatement(checking);
    printStatement(savings);
  }

  // Client uses abstract TransactionIterator
  private static void printStatement(BankAccount account) {
    System.out.println("\n--- " + account.getAccountType() + " ACCOUNT STATEMENT ---");
    TransactionIterator iterator = account.createIterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }
}
