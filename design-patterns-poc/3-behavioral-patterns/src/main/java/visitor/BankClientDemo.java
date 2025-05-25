package visitor;

interface BankAccount {
	void accept(AccountVisitor visitor);
}

/**
 * @param interestRate e.g., 0.03 = 3%
 */
record SavingsAccount(String accountNumber, double balance, double interestRate) implements BankAccount {


	@Override
	public void accept(AccountVisitor visitor) {
		visitor.visit(this); // Let the visitor process this account
	}
}

/**
 * @param overdraftLimit e.g., 1000.0
 */
record CheckingAccount(String accountNumber, double balance, double overdraftLimit) implements BankAccount {


	@Override
	public void accept(AccountVisitor visitor) {
		visitor.visit(this); // Let the visitor process this account
	}
}

interface AccountVisitor {
	void visit(SavingsAccount savingsAccount);

	void visit(CheckingAccount checkingAccount);
}


class InterestCalculator implements AccountVisitor {
	@Override
	public void visit(SavingsAccount savingsAccount) {
		double interest = savingsAccount.balance() * savingsAccount.interestRate();
		System.out.printf("Savings Account %s | Interest Added: $%.2f\n",
				savingsAccount.accountNumber(), interest);
	}

	@Override
	public void visit(CheckingAccount checkingAccount) {
		System.out.printf("Checking Account %s | No interest applied\n",
				checkingAccount.accountNumber());
	}
}

class AccountReporter implements AccountVisitor {
	@Override
	public void visit(SavingsAccount savingsAccount) {
		System.out.printf("=== Savings Account %s ===\n", savingsAccount.accountNumber());
		System.out.printf("Balance: $%.2f | Interest Rate: %.2f%%\n",
				savingsAccount.balance(),
				savingsAccount.interestRate() * 100);
	}

	@Override
	public void visit(CheckingAccount checkingAccount) {
		System.out.printf("=== Checking Account %s ===\n", checkingAccount.accountNumber());
		System.out.printf("Balance: $%.2f | Overdraft Limit: $%.2f\n",
				checkingAccount.balance(),
				checkingAccount.overdraftLimit());
	}
}


public class BankClientDemo {
	public static void main(String[] args) {
		// Create bank accounts
		BankAccount savings = new SavingsAccount("SAV001", 5000.0, 0.03); // 3% interest
		BankAccount checking = new CheckingAccount("CHK001", 2500.0, 1000.0); // $1000 overdraft

		// Create visitors
		AccountVisitor interestCalculator = new InterestCalculator();
		AccountVisitor accountReporter = new AccountReporter();

		// Apply interest calculation
		System.out.println("=== Applying Interest ===");
		savings.accept(interestCalculator);
		checking.accept(interestCalculator);

		// Generate account reports
		System.out.println("\n=== Account Reports ===");
		savings.accept(accountReporter);
		checking.accept(accountReporter);
	}
}
