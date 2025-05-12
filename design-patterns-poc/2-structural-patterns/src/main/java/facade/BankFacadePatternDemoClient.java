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
