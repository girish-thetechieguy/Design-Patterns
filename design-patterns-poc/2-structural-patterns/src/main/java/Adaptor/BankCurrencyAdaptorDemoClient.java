package Adaptor;

/**
 * Another adapter example for currency conversion
 */
class ForeignBankAdapter implements ModernBanking {
    private final ForeignBankService foreignBankService;
    private final String currency;

    public ForeignBankAdapter(ForeignBankService foreignBankService, String currency) {
        this.foreignBankService = foreignBankService;
        this.currency = currency;
    }

    @Override
    public void deposit(String iban, double amount) {
        double convertedAmount = convertToForeignCurrency(amount);
        foreignBankService.foreignDeposit(iban, convertedAmount, currency);
    }

    @Override
    public void withdraw(String iban, double amount) {
        double convertedAmount = convertToForeignCurrency(amount);
        foreignBankService.foreignWithdraw(iban, convertedAmount, currency);
    }

    @Override
    public double getBalance(String iban) {
        double foreignBalance = foreignBankService.getForeignBalance(iban, currency);
        return convertToDollars(foreignBalance);
    }

    @Override
    public void transfer(String sourceIban, String targetIban, double amount) {
        double convertedAmount = convertToForeignCurrency(amount);
        foreignBankService.foreignTransfer(sourceIban, targetIban, convertedAmount, currency);
    }

    private double convertToForeignCurrency(double dollars) {
        // Mock conversion rates
        if ("EUR".equals(currency)) return dollars * 0.92;
        if ("JPY".equals(currency)) return dollars * 151.43;
        return dollars;
    }

    private double convertToDollars(double foreignAmount) {
        // Mock conversion rates
        if ("EUR".equals(currency)) return foreignAmount / 0.92;
        if ("JPY".equals(currency)) return foreignAmount / 151.43;
        return foreignAmount;
    }
}

// Mock Foreign Bank Service
class ForeignBankService {
    public void foreignDeposit(String iban, double amount, String currency) {
        System.out.printf("Foreign bank: Depositing %.2f %s to IBAN %s%n",
                amount, currency, iban);
    }

    public void foreignWithdraw(String iban, double amount, String currency) {
        System.out.printf("Foreign bank: Withdrawing %.2f %s from IBAN %s%n",
                amount, currency, iban);
    }

    public double getForeignBalance(String iban, String currency) {
        System.out.printf("Foreign bank: Checking balance for IBAN %s in %s%n", iban, currency);
        return 5000.00; // Mock balance
    }

    public void foreignTransfer(String sourceIban, String targetIban,
                                double amount, String currency) {
        System.out.printf("Foreign bank: Transferring %.2f %s from %s to %s%n",
                amount, currency, sourceIban, targetIban);
    }
}

public class BankCurrencyAdaptorDemoClient {
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
