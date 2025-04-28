package singleton;

import java.util.concurrent.locks.ReentrantLock;


class BankTransactionManager {
    // Singleton instance
    private static BankTransactionManager instance;
    // Lock for thread safety
    private static final ReentrantLock lock = new ReentrantLock();

    // Bank-related properties
    private double totalBankBalance;
    private int transactionCount;

    // Private constructor to prevent instantiation
    private BankTransactionManager() {
        // Initialize bank properties
        this.totalBankBalance = 1_000_000.00; // Starting balance
        this.transactionCount = 0;
        System.out.println("BankTransactionManager initialized");
    }

    // Thread-safe instance creation with double-check locking and ReentrantLock
    public static BankTransactionManager getInstance() {
        if (instance == null) { // First check (no locking)
            lock.lock(); // Acquire lock
            try {
                if (instance == null) { // Second check (with locking)
                    instance = new BankTransactionManager();
                }
            } finally {
                lock.unlock(); // Release lock in finally block
            }
        }
        return instance;
    }

    // Thread-safe deposit method
    public void deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        lock.lock();
        try {
            // Simulate database operation
            System.out.println("Processing deposit to account " + accountNumber + ": +$" + amount);
            totalBankBalance += amount;
            transactionCount++;
            System.out.println("New bank balance: $" + totalBankBalance);
        } finally {
            lock.unlock();
        }
    }

    // Thread-safe withdrawal method
    public boolean withdraw(String accountNumber, double amount) {
        lock.lock();
        try {
            if (amount > totalBankBalance) {
                System.out.println("Withdrawal failed - insufficient funds");
                return false;
            }

            System.out.println("Processing withdrawal from account " + accountNumber + ": -$" + amount);
            totalBankBalance -= amount;
            transactionCount++;
            System.out.println("New bank balance: $" + totalBankBalance);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Thread-safe balance check
    public double getTotalBankBalance() {
        lock.lock();
        try {
            return totalBankBalance;
        } finally {
            lock.unlock();
        }
    }

    // Thread-safe transaction count
    public int getTransactionCount() {
        lock.lock();
        try {
            return transactionCount;
        } finally {
            lock.unlock();
        }
    }

//    public boolean transfer(String fromAccount, String toAccount, double amount) {
//        try {
//            if (lock.tryLock(1, TimeUnit.SECONDS)) {
//                try {
//                    if (withdraw(fromAccount, amount)) {
//                        deposit(toAccount, amount);
//                        return true;
//                    }
//                    return false;
//                } finally {
//                    lock.unlock();
//                }
//            } else {
//                System.out.println("Could not acquire lock for transfer");
//                return false;
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return false;
//        }
//    }
}

public class BankThreadSafeSingletonDemo {
    public static void main(String[] args) {
        // Create multiple threads to test thread safety
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                BankTransactionManager bank = BankTransactionManager.getInstance();

                // Perform transactions
                bank.deposit("ACC123", 1000);
                bank.withdraw("ACC123", 500);

                System.out.println("Thread " + Thread.currentThread().getId() +
                        " sees balance: $" + bank.getTotalBankBalance());
            }).start();
        }
    }
}
