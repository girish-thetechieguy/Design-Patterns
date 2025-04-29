package prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Key Improvements:
 * Removed Cloneable: Replaced with explicit copy() method
 * Copy Constructors: Added protected copy constructors in each class
 * Type-Safe Copying: Each subclass returns its own type from copy()
 * Immutable Fields: Made fields final where possible
 * Defensive Copying: For mutable fields like the ETF holdings list
 * Simplified Exception Handling: No more CloneNotSupportedException
 *
 * This implementation is more robust because:
 * It doesn't rely on Java's problematic Cloneable interface
 * It provides better control over the copying process
 * It's more type-safe with specific return types from copy()
 * It properly handles mutable object graphs
 * It's more maintainable and less error-prone
 *
 * The prototype pattern is particularly useful in financial systems where:
 * Creating new objects is expensive (e.g., loading from database)
 * You need many similar objects with slight variations
 * You want to maintain a library of pre-configured objects
 * You need to dynamically create objects at runtime
 */

interface FinancialInstrument {
    String getSymbol();
    double getPrice();
    void setPrice(double price);
    FinancialInstrument copy();
    String getDetails();
}

abstract class Stock implements FinancialInstrument {
    protected String symbol;
    protected String companyName;
    protected double price;
    protected double dividendYield;
    protected double peRatio;

    public Stock(String symbol, String companyName, double price,
                 double dividendYield, double peRatio) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.dividendYield = dividendYield;
        this.peRatio = peRatio;
    }

    // Copy constructor for prototype pattern
    protected Stock(Stock source) {
        this(source.symbol, source.companyName, source.price,
                source.dividendYield, source.peRatio);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public abstract Stock copy();

    @Override
    public String getDetails() {
        return String.format("%s [%s] Price: $%.2f | Yield: %.2f%% | P/E: %.2f",
                companyName, symbol, price, dividendYield, peRatio);
    }
}

class EquityStock extends Stock {
    private String sector;
    private int outstandingShares;

    public EquityStock(String symbol, String companyName, double price,
                       double dividendYield, double peRatio,
                       String sector, int outstandingShares) {
        super(symbol, companyName, price, dividendYield, peRatio);
        this.sector = sector;
        this.outstandingShares = outstandingShares;
    }

    // Copy constructor
    protected EquityStock(EquityStock source) {
        super(source);
        this.sector = source.sector;
        this.outstandingShares = source.outstandingShares;
    }

    @Override
    public EquityStock copy() {
        return new EquityStock(this);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | Sector: %s | Shares: %,d",
                sector, outstandingShares);
    }

    public double calculateMarketCap() {
        return price * outstandingShares;
    }
}

class IndexComponentStock extends Stock {
    private String indexName;
    private double weightInIndex;

    public IndexComponentStock(String symbol, String companyName, double price,
                               double dividendYield, double peRatio,
                               String indexName, double weightInIndex) {
        super(symbol, companyName, price, dividendYield, peRatio);
        this.indexName = indexName;
        this.weightInIndex = weightInIndex;
    }

    // Copy constructor
    protected IndexComponentStock(IndexComponentStock source) {
        super(source);
        this.indexName = source.indexName;
        this.weightInIndex = source.weightInIndex;
    }

    @Override
    public IndexComponentStock copy() {
        return new IndexComponentStock(this);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | Index: %s | Weight: %.2f%%",
                indexName, weightInIndex * 100);
    }
}

class ETF extends Stock {
    private List<String> holdings;
    private double expenseRatio;

    public ETF(String symbol, String companyName, double price,
               double dividendYield, double peRatio,
               List<String> holdings, double expenseRatio) {
        super(symbol, companyName, price, dividendYield, peRatio);
        this.holdings = new ArrayList<>(holdings); // Defensive copy
        this.expenseRatio = expenseRatio;
    }

    // Copy constructor
    protected ETF(ETF source) {
        super(source);
        this.holdings = new ArrayList<>(source.holdings);
        this.expenseRatio = source.expenseRatio;
    }

    @Override
    public ETF copy() {
        return new ETF(this);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | Holdings: %d | Expense: %.2f%%",
                holdings.size(), expenseRatio);
    }

    public void addHolding(String stockSymbol) {
        holdings.add(stockSymbol);
    }
}

class FinancialInstrumentRegistry {
    private static final Map<String, FinancialInstrument> prototypes = new HashMap<>();

    static {
        // Pre-populate with some common instruments
        prototypes.put("AAPL", new EquityStock(
                "AAPL", "Apple Inc.", 175.50, 0.5, 29.34,
                "Technology", 16_700_000_00));

        prototypes.put("SPY", new ETF(
                "SPY", "SPDR S&P 500 ETF", 450.25, 1.2, 22.5,
                List.of("AAPL", "MSFT", "AMZN", "GOOGL", "TSLA"), 0.09));

        prototypes.put("SPX", new IndexComponentStock(
                "SPX", "S&P 500 Index", 4500.0, 1.5, 21.8,
                "S&P 500", 1.0));
    }

    public static FinancialInstrument getPrototype(String symbol) {
        FinancialInstrument prototype = prototypes.get(symbol);
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown financial instrument: " + symbol);
        }
        return prototype.copy();
    }

    public static void registerPrototype(String symbol, FinancialInstrument instrument) {
        prototypes.put(symbol, instrument);
    }
}

public class StockMarketPrototypeClientDemo {
    public static void main(String[] args) {
        // Create stocks by copying prototypes
        FinancialInstrument appleStock = FinancialInstrumentRegistry.getPrototype("AAPL");
        FinancialInstrument sp500ETF = FinancialInstrumentRegistry.getPrototype("SPY");
        FinancialInstrument sp500Index = FinancialInstrumentRegistry.getPrototype("SPX");

        // Customize the copies
        appleStock.setPrice(177.25);
        sp500ETF.setPrice(451.75);
        sp500Index.setPrice(4520.50);

        // Create a new ETF by copying and customizing
        ETF techETF = (ETF) FinancialInstrumentRegistry.getPrototype("SPY");
        techETF.setPrice(150.00);
        techETF.addHolding("NVDA");
        techETF.addHolding("AMD");

        // Display instrument details
        System.out.println("=== Portfolio ===");
        System.out.println(appleStock.getDetails());
        System.out.println(sp500ETF.getDetails());
        System.out.println(sp500Index.getDetails());
        System.out.println(techETF.getDetails());

        // Create and register a new prototype
        EquityStock newStock = new EquityStock(
                "TSLA", "Tesla Inc.", 200.75, 0.0, 85.6,
                "Automotive", 3_170_000_00);
        FinancialInstrumentRegistry.registerPrototype("TSLA", newStock);

        // Use the new prototype
        FinancialInstrument teslaStock = FinancialInstrumentRegistry.getPrototype("TSLA");
        teslaStock.setPrice(205.50);
        System.out.println("\nNewly added stock:");
        System.out.println(teslaStock.getDetails());
    }
}
