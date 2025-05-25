package visitor;

interface FinancialInstrument {
	void accept(FinancialInstrumentVisitor visitor);
}

interface FinancialInstrumentVisitor {
	void visit(Stock stock);
	void visit(Bond bond);
}

/**
 * @param dividendYield e.g., 0.03 = 3%
 */
record Stock(String ticker, double price, double dividendYield) implements FinancialInstrument {

	@Override
	public void accept(FinancialInstrumentVisitor visitor) {
		visitor.visit(this); // Let the visitor analyze this stock
	}
}

/**
 * @param couponRate e.g., 0.05 = 5%
 */
record Bond(String issuer, double faceValue, double couponRate) implements FinancialInstrument {


	@Override
	public void accept(FinancialInstrumentVisitor visitor) {
		visitor.visit(this); // Let the visitor analyze this bond
	}
}

class DividendAnalyzer implements FinancialInstrumentVisitor {
	@Override
	public void visit(Stock stock) {
		double annualDividend = stock.price() * stock.dividendYield();
		System.out.printf("Stock %s | Annual Dividend: $%.2f\n",
				stock.ticker(), annualDividend);
	}

	@Override
	public void visit(Bond bond) {
		System.out.printf("Bond %s | No dividends (fixed coupon payments)\n",
				bond.issuer());
	}
}

class RiskAnalyzer implements FinancialInstrumentVisitor {
	@Override
	public void visit(Stock stock) {
		System.out.printf("Stock %s | Risk: High (Market Volatility)\n",
				stock.ticker());
	}

	@Override
	public void visit(Bond bond) {
		System.out.printf("Bond %s | Risk: %s\n",
				bond.issuer(),
				bond.couponRate() > 0.06 ? "Medium (Junk Bond)" : "Low (Investment Grade)");
	}
}

public class StockMarketClientDemo {
	public static void main(String[] args) {
		// Create financial instruments
		FinancialInstrument appleStock = new Stock("AAPL", 170.50, 0.005); // 0.5% dividend
		FinancialInstrument microsoftStock = new Stock("MSFT", 300.00, 0.008); // 0.8% dividend
		FinancialInstrument treasuryBond = new Bond("US Treasury", 1000.00, 0.04); // 4% coupon
		FinancialInstrument corporateBond = new Bond("XYZ Corp", 1000.00, 0.07); // 7% coupon

		// Create visitors
		FinancialInstrumentVisitor dividendAnalyzer = new DividendAnalyzer();
		FinancialInstrumentVisitor riskAnalyzer = new RiskAnalyzer();

		// Apply dividend analysis
		System.out.println("=== Dividend Analysis ===");
		appleStock.accept(dividendAnalyzer);
		microsoftStock.accept(dividendAnalyzer);
		treasuryBond.accept(dividendAnalyzer);

		// Apply risk analysis
		System.out.println("\n=== Risk Analysis ===");
		appleStock.accept(riskAnalyzer);
		corporateBond.accept(riskAnalyzer);
	}
}
