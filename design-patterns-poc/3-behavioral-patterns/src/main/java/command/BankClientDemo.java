package command;

import java.util.Stack;

interface TransactionCommand {
    void execute();
    void undo();
}

// Deposit Command
class DepositCommand implements TransactionCommand {
    private Account account;
    private double amount;

    public DepositCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.deposit(amount);
        System.out.printf("Deposited $%.2f to %s%n", amount, account.getId());
    }

    @Override
    public void undo() {
        account.withdraw(amount);
        System.out.printf("Undo: Withdrew $%.2f from %s%n", amount, account.getId());
    }
}

// Withdrawal Command
class WithdrawCommand implements TransactionCommand {
    private Account account;
    private double amount;

    public WithdrawCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        if (account.getBalance() >= amount) {
            account.withdraw(amount);
            System.out.printf("Withdrew $%.2f from %s%n", amount, account.getId());
        } else {
            System.out.println("Error: Insufficient funds");
        }
    }

    @Override
    public void undo() {
        account.deposit(amount);
        System.out.printf("Undo: Deposited $%.2f to %s%n", amount, account.getId());
    }
}

class Account {
    private String id;
    private double balance;

    public Account(String id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getId() {
        return id;
    }
}

class TransactionProcessor {
    private Stack<TransactionCommand> history = new Stack<>();

    public void executeTransaction(TransactionCommand command) {
        command.execute();
        history.push(command); // Save for undo
    }

    public void undoLastTransaction() {
        if (!history.isEmpty()) {
            TransactionCommand lastCommand = history.pop();
            lastCommand.undo();
        }
    }
}

public class BankClientDemo {
    public static void main(String[] args) {
        // Setup
        Account account = new Account("ACC123", 1000.0);
        TransactionProcessor processor = new TransactionProcessor();

        // Create transactions
        TransactionCommand deposit = new DepositCommand(account, 500.0);
        TransactionCommand withdraw = new WithdrawCommand(account, 200.0);

        // Execute
        processor.executeTransaction(deposit);
        processor.executeTransaction(withdraw);

        System.out.println("\nCurrent balance: $" + account.getBalance());

        // Undo last transaction
        System.out.println("\nUndoing last transaction:");
        processor.undoLastTransaction();
        System.out.println("Balance after undo: $" + account.getBalance());
    }
}
