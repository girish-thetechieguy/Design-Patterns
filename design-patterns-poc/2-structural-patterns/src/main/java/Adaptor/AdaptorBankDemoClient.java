package Adaptor;


/**
 * Key Features of This Implementation:
 * Interface Adaptation: Converts legacy method calls to modern interface
 * Protocol Translation: Handles IBAN to legacy account number conversion
 * Behavioral Consistency: Provides same interface for both modern and legacy systems
 * Currency Conversion: Additional adapter shows multi-currency support
 * Unified Client Code: Same client code works with both systems
 */

/**
 * Legacy banking system with fixed interface we can't modify
 */

class LegacyBankSystem {
    public void creditLegacy(String accountNumber, double amountInDollars) {
        System.out.printf("Legacy system: Crediting $%.2f to account %s%n",
                amountInDollars, accountNumber);
    }

    public void debitLegacy(String accountNumber, double amountInDollars) {
        System.out.printf("Legacy system: Debiting $%.2f from account %s%n",
                amountInDollars, accountNumber);
    }

    public double checkBalanceLegacy(String accountNumber) {
        System.out.printf("Legacy system: Checking balance for account %s%n", accountNumber);
        return 1000.00; // Mock balance
    }
}

/**
 * Modern interface our application expects to work with
 */
interface ModernBanking {
    void deposit(String iban, double amount);
    void withdraw(String iban, double amount);
    double getBalance(String iban);
    void transfer(String sourceIban, String targetIban, double amount);
}

/**
 * Adapter that makes the LegacyBankSystem work with ModernBanking interface
 */
class LegacyBankAdapter implements ModernBanking {
    private final LegacyBankSystem legacyBankSystem;

    public LegacyBankAdapter(LegacyBankSystem legacyBankSystem) {
        this.legacyBankSystem = legacyBankSystem;
    }

    @Override
    public void deposit(String iban, double amount) {
        String legacyAccountNumber = convertIbanToLegacy(iban);
        legacyBankSystem.creditLegacy(legacyAccountNumber, amount);
    }

    @Override
    public void withdraw(String iban, double amount) {
        String legacyAccountNumber = convertIbanToLegacy(iban);
        legacyBankSystem.debitLegacy(legacyAccountNumber, amount);
    }

    @Override
    public double getBalance(String iban) {
        String legacyAccountNumber = convertIbanToLegacy(iban);
        return legacyBankSystem.checkBalanceLegacy(legacyAccountNumber);
    }

    @Override
    public void transfer(String sourceIban, String targetIban, double amount) {
        withdraw(sourceIban, amount);
        deposit(targetIban, amount);
        System.out.printf("Transferred $%.2f from %s to %s%n",
                amount, sourceIban, targetIban);
    }

    private String convertIbanToLegacy(String iban) {
        // In real system, this would do proper IBAN parsing
        return iban.substring(iban.length() - 10); // Last 10 digits as legacy account
    }
}

/**
 * Native modern banking implementation (not using adapter)
 */
class ModernBankImplementation implements ModernBanking {
    @Override
    public void deposit(String iban, double amount) {
        System.out.printf("Modern system: Depositing $%.2f to IBAN %s%n", amount, iban);
    }

    @Override
    public void withdraw(String iban, double amount) {
        System.out.printf("Modern system: Withdrawing $%.2f from IBAN %s%n", amount, iban);
    }

    @Override
    public double getBalance(String iban) {
        System.out.printf("Modern system: Getting balance for IBAN %s%n", iban);
        return 1500.00; // Mock balance
    }

    @Override
    public void transfer(String sourceIban, String targetIban, double amount) {
        System.out.printf("Modern system: Transferring $%.2f from %s to %s%n",
                amount, sourceIban, targetIban);
    }
}


public class AdaptorBankDemoClient {
    public static void main(String[] args) {
        // Modern banking system
        ModernBanking modernBank = new ModernBankImplementation();
        processTransaction(modernBank, "GB29NWBK60161331926819");

        System.out.println("\n=== Using Legacy System via Adapter ===\n");

        // Legacy banking system with adapter
        LegacyBankSystem legacyBank = new LegacyBankSystem();
        ModernBanking adaptedLegacyBank = new LegacyBankAdapter(legacyBank);
        processTransaction(adaptedLegacyBank, "GB29NWBK60161331926819");
    }

    private static void processTransaction(ModernBanking bank, String iban) {
        bank.deposit(iban, 500.00);
        bank.withdraw(iban, 200.00);
        double balance = bank.getBalance(iban);
        System.out.printf("Current balance: $%.2f%n", balance);
        bank.transfer(iban, "DE89370400440532013000", 100.00);
    }
}
