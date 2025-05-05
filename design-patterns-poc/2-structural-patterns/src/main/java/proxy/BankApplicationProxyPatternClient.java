package proxy;

import java.util.HashMap;
import java.util.Map;

interface BankService {
    double getAccountBalance(String accountNumber);
    void deposit(String accountNumber, double amount);
    void withdraw(String accountNumber, double amount) throws InsufficientFundsException;
}

class RealBankService implements BankService {
    // Simulates actual bank database
    private Map<String, Double> accounts = new HashMap<>();

    public RealBankService() {
        // Initialize with some accounts
        accounts.put("12345", 1000.0);
        accounts.put("67890", 500.0);
    }

    @Override
    public double getAccountBalance(String accountNumber) {
        System.out.println("Accessing real bank database for account " + accountNumber);
        return accounts.getOrDefault(accountNumber, 0.0);
    }

    @Override
    public void deposit(String accountNumber, double amount) {
        System.out.println("Processing real deposit to account " + accountNumber);
        double currentBalance = accounts.getOrDefault(accountNumber, 0.0);
        accounts.put(accountNumber, currentBalance + amount);
    }

    @Override
    public void withdraw(String accountNumber, double amount) throws InsufficientFundsException {
        System.out.println("Processing real withdrawal from account " + accountNumber);
        double currentBalance = accounts.getOrDefault(accountNumber, 0.0);

        if (currentBalance < amount) {
            throw new InsufficientFundsException("Not enough funds");
        }

        accounts.put(accountNumber, currentBalance - amount);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}


class BankServiceProxy implements BankService {
    private RealBankService realBankService;
    private SecurityService securityService;

    public BankServiceProxy() {
        this.securityService = new SecurityService();
    }

    @Override
    public double getAccountBalance(String accountNumber) {
        // Lazy initialization
        if (realBankService == null) {
            realBankService = new RealBankService();
        }

        // Access control
        if (!securityService.hasAccess(accountNumber)) {
            throw new SecurityException("Access denied to account " + accountNumber);
        }

        return realBankService.getAccountBalance(accountNumber);
    }

    @Override
    public void deposit(String accountNumber, double amount) {
        if (realBankService == null) {
            realBankService = new RealBankService();
        }

        // Validation
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        realBankService.deposit(accountNumber, amount);
    }

    @Override
    public void withdraw(String accountNumber, double amount) throws InsufficientFundsException {
        if (realBankService == null) {
            realBankService = new RealBankService();
        }

        // Additional validation
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Daily withdrawal limit
        if (amount > 1000) {
            throw new SecurityException("Exceeds daily withdrawal limit");
        }

        realBankService.withdraw(accountNumber, amount);
    }
}

class SecurityService {
    public boolean hasAccess(String accountNumber) {
        // In real system, check user permissions
        return true; // Simplified for example
    }
}

public class BankApplicationProxyPatternClient {
    public static void main(String[] args) {
        // Client interacts with the proxy
        BankService bankService = new BankServiceProxy();

        try {
            // Check balance
            double balance = bankService.getAccountBalance("12345");
            System.out.println("Current balance: $" + balance);

            // Deposit money
            bankService.deposit("12345", 200);
            System.out.println("Deposited $200");

            // Withdraw money
            bankService.withdraw("12345", 300);
            System.out.println("Withdrew $300");

            // Check new balance
            System.out.println("New balance: $" + bankService.getAccountBalance("12345"));

            // Try invalid withdrawal
            bankService.withdraw("12345", 2000);
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Security Error: " + e.getMessage());
        }
    }
}
