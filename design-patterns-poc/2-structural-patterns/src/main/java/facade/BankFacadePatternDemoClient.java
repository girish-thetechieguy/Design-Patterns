package facade;

/**
 * The Facade Pattern provides a simple interface to a complex subsystem. In banking,
 * customers shouldn't need to interact with separate systems for account validation,
 * transaction processing, and notifications—they just want to "transfer money."
 *
 * Bank System Without Facade (Problem)
 * A typical bank transfer involves:
 * AccountValidator – Checks if accounts exist
 * BalanceChecker – Verifies sufficient funds
 * TransactionProcessor – Moves money
 * NotificationService – Sends alerts
 *
 * Key Benefits
 * Advantage	Banking Example
 * Simplifies Clients	Customers call transferMoney(), not 4 subsystems.
 * Decouples Logic	Changes to TransactionProcessor don’t affect clients.
 * Centralized Control	Facade can add fraud checks before processing.
 * Error Handling	Failed validations trigger appropriate notifications.
 * Real-World Analogies
 * ATM Machine – A facade for:
 * Card validation
 * Balance checks
 * Cash dispensing
 * Receipt printing
 *
 * Online Banking Portal – Hides:
 * Database operations
 * Transaction logging
 * Security protocols
 *
 * When to Use?
 * ✅ You need to hide complex workflows (e.g., loan approvals)
 * ✅ Subsystems are prone to change (e.g., new fraud detection)
 * ✅ Clients should not depend on internal details.
 */



// Complex subsystem classes
class AccountValidator {
    public boolean isValid(String accountNo) {
        System.out.println("Validating account: " + accountNo);
        return true; // Simplified
    }
}

class BalanceChecker {
    public boolean hasSufficientBalance(String accountNo, double amount) {
        System.out.println("Checking balance for " + accountNo);
        return true; // Simplified
    }
}

class TransactionProcessor {
    public void transfer(String from, String to, double amount) {
        System.out.println("Transferring $" + amount + " from " + from + " to " + to);
    }
}

class NotificationsService {
    public void send(String accountNo, String message) {
        System.out.println("Notifying " + accountNo + ": " + message);
    }
}

class BankFacade {
    private AccountValidator validator;
    private BalanceChecker balanceChecker;
    private TransactionProcessor processor;
    private NotificationsService notifier;

    public BankFacade() {
        this.validator = new AccountValidator();
        this.balanceChecker = new BalanceChecker();
        this.processor = new TransactionProcessor();
        this.notifier = new NotificationsService();
    }

    // Unified method for money transfer
    public void transferMoney(String from, String to, double amount) {
        if (validator.isValid(from) && validator.isValid(to)) {
            if (balanceChecker.hasSufficientBalance(from, amount)) {
                processor.transfer(from, to, amount);
                notifier.send(from, "Debit: $" + amount);
                notifier.send(to, "Credit: $" + amount);
            } else {
                notifier.send(from, "Insufficient funds");
            }
        } else {
            System.out.println("Invalid account(s)");
        }
    }
}

public class BankFacadePatternDemoClient {
    public static void main(String[] args) {
        BankFacade bank = new BankFacade();
        // Simple interface for complex operation
        bank.transferMoney("ACC123", "ACC456", 1000);
    }
}
