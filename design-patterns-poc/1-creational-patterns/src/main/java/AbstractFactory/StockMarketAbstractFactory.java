package AbstractFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Key Features of This Implementation:
 * Family of Related Products: Creates stocks, options, and futures that are consistent within each market segment
 * Flexibility: Easy to add new market segments (e.g., CommoditiesMarketFactory)
 * Consistency: Ensures all products in a portfolio match the same market type
 * Encapsulation: Hides concrete product implementations from clients
 * Real-world Relevance: Models actual financial market instruments and relationships
 * Type Safety: Strong typing prevents mixing incompatible products
 */

interface Stock {
    String getSymbol();
    String getCompanyName();
    double getPrice();
    double getDividendYield();
    void updatePrice(double newPrice);
}

class EquityStock implements Stock {
    private String symbol;
    private String companyName;
    private double price;
    private double dividendYield;

    public EquityStock(String symbol, String companyName, double price, double dividendYield) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.dividendYield = dividendYield;
    }

    @Override public String getSymbol() { return symbol; }
    @Override public String getCompanyName() { return companyName; }
    @Override public double getPrice() { return price; }
    @Override public double getDividendYield() { return dividendYield; }

    @Override
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) throw new IllegalArgumentException("Price must be positive");
        this.price = newPrice;
    }

    @Override
    public String toString() {
        return String.format("Equity [%s - %s] Price: $%.2f (Yield: %.2f%%)",
                symbol, companyName, price, dividendYield);
    }
}

class IndexComponentStock implements Stock {
    private String symbol;
    private String indexName;
    private double price;
    private double weightInIndex;

    public IndexComponentStock(String symbol, String indexName, double price, double weightInIndex) {
        this.symbol = symbol;
        this.indexName = indexName;
        this.price = price;
        this.weightInIndex = weightInIndex;
    }

    @Override public String getSymbol() { return symbol; }
    @Override public String getCompanyName() { return indexName + " Component"; }
    @Override public double getPrice() { return price; }
    @Override public double getDividendYield() { return 0; } // Index components typically don't pay dividends

    @Override
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) throw new IllegalArgumentException("Price must be positive");
        this.price = newPrice;
    }

    @Override
    public String toString() {
        return String.format("Index Component [%s - %s] Price: $%.2f (Weight: %.2f%%)",
                symbol, indexName, price, weightInIndex * 100);
    }
}

interface Option {
    String getOptionId();
    Stock getUnderlying();
    double getStrikePrice();
    LocalDate getExpiryDate();
    double calculatePremium();
}

class EquityOption implements Option {
    private String optionId;
    private Stock underlying;
    private double strikePrice;
    private LocalDate expiryDate;
    private OptionType type;

    public EquityOption(String optionId, Stock underlying, double strikePrice,
                        LocalDate expiryDate, OptionType type) {
        this.optionId = optionId;
        this.underlying = underlying;
        this.strikePrice = strikePrice;
        this.expiryDate = expiryDate;
        this.type = type;
    }

    @Override public String getOptionId() { return optionId; }
    @Override public Stock getUnderlying() { return underlying; }
    @Override public double getStrikePrice() { return strikePrice; }
    @Override public LocalDate getExpiryDate() { return expiryDate; }

    @Override
    public double calculatePremium() {
        // Simplified Black-Scholes inspired calculation
        double timeToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) / 365.0;
        double volatility = 0.2; // Assume 20% volatility
        double intrinsicValue = type == OptionType.CALL ?
                Math.max(0, underlying.getPrice() - strikePrice) :
                Math.max(0, strikePrice - underlying.getPrice());

        return intrinsicValue + (underlying.getPrice() * volatility * Math.sqrt(timeToExpiry));
    }

    @Override
    public String toString() {
        return String.format("%s Option [%s] on %s @ $%.2f (Exp: %s, Premium: $%.2f)",
                type, optionId, underlying.getSymbol(), strikePrice, expiryDate, calculatePremium());
    }
}

class IndexOption implements Option {
    private String optionId;
    private Stock underlying;
    private double strikePrice;
    private LocalDate expiryDate;
    private OptionType type;
    private double multiplier;

    public IndexOption(String optionId, Stock underlying, double strikePrice,
                       LocalDate expiryDate, OptionType type, double multiplier) {
        this.optionId = optionId;
        this.underlying = underlying;
        this.strikePrice = strikePrice;
        this.expiryDate = expiryDate;
        this.type = type;
        this.multiplier = multiplier;
    }

    @Override public String getOptionId() { return optionId; }
    @Override public Stock getUnderlying() { return underlying; }
    @Override public double getStrikePrice() { return strikePrice; }
    @Override public LocalDate getExpiryDate() { return expiryDate; }

    @Override
    public double calculatePremium() {
        // Simplified calculation for index options
        double timeToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) / 365.0;
        double volatility = 0.15; // Assume 15% volatility for indices
        double intrinsicValue = type == OptionType.CALL ?
                Math.max(0, underlying.getPrice() - strikePrice) :
                Math.max(0, strikePrice - underlying.getPrice());

        return (intrinsicValue + (underlying.getPrice() * volatility * Math.sqrt(timeToExpiry))) * multiplier;
    }

    @Override
    public String toString() {
        return String.format("%s Index Option [%s] on %s @ $%.2f (Exp: %s, Mult: %.0f, Premium: $%.2f)",
                type, optionId, underlying.getSymbol(), strikePrice, expiryDate, multiplier, calculatePremium());
    }
}

enum OptionType {
    CALL, PUT
}

interface Future {
    String getFutureId();
    Stock getUnderlying();
    LocalDate getDeliveryDate();
    double getContractSize();
    double calculateMargin();
}

class EquityFuture implements Future {
    private String futureId;
    private Stock underlying;
    private LocalDate deliveryDate;
    private double contractSize;

    public EquityFuture(String futureId, Stock underlying, LocalDate deliveryDate, double contractSize) {
        this.futureId = futureId;
        this.underlying = underlying;
        this.deliveryDate = deliveryDate;
        this.contractSize = contractSize;
    }

    @Override public String getFutureId() { return futureId; }
    @Override public Stock getUnderlying() { return underlying; }
    @Override public LocalDate getDeliveryDate() { return deliveryDate; }
    @Override public double getContractSize() { return contractSize; }

    @Override
    public double calculateMargin() {
        // Typically 5-15% of contract value
        return underlying.getPrice() * contractSize * 0.10; // 10% margin
    }

    @Override
    public String toString() {
        return String.format("Equity Future [%s] on %s (Delivery: %s, Size: %.0f, Margin: $%.2f)",
                futureId, underlying.getSymbol(), deliveryDate, contractSize, calculateMargin());
    }
}

class IndexFuture implements Future {
    private String futureId;
    private Stock underlying;
    private LocalDate deliveryDate;
    private double contractSize;
    private double multiplier;

    public IndexFuture(String futureId, Stock underlying, LocalDate deliveryDate,
                       double contractSize, double multiplier) {
        this.futureId = futureId;
        this.underlying = underlying;
        this.deliveryDate = deliveryDate;
        this.contractSize = contractSize;
        this.multiplier = multiplier;
    }

    @Override public String getFutureId() { return futureId; }
    @Override public Stock getUnderlying() { return underlying; }
    @Override public LocalDate getDeliveryDate() { return deliveryDate; }
    @Override public double getContractSize() { return contractSize; }

    @Override
    public double calculateMargin() {
        // Index futures typically have lower margin requirements
        return underlying.getPrice() * contractSize * multiplier * 0.05; // 5% margin
    }

    @Override
    public String toString() {
        return String.format("Index Future [%s] on %s (Delivery: %s, Size: %.0f, Mult: %.0f, Margin: $%.2f)",
                futureId, underlying.getSymbol(), deliveryDate, contractSize, multiplier, calculateMargin());
    }
}

class EquityMarketFactory implements MarketFactory {
    @Override
    public Stock createStock(String symbol, String name, double price) {
        // Assume average dividend yield of 2% for equities
        return new EquityStock(symbol, name, price, 2.0);
    }

    @Override
    public Option createOption(String optionId, Stock underlying, double strikePrice,
                               LocalDate expiry, OptionType type) {
        return new EquityOption(optionId, underlying, strikePrice, expiry, type);
    }

    @Override
    public Future createFuture(String futureId, Stock underlying, LocalDate delivery) {
        // Standard contract size of 100 shares for equity futures
        return new EquityFuture(futureId, underlying, delivery, 100);
    }
}

class DerivativesMarketFactory implements MarketFactory {
    @Override
    public Stock createStock(String symbol, String name, double price) {
        // For indices, we use index components with weight
        return new IndexComponentStock(symbol, name, price, 0.01); // Assume 1% weight
    }

    @Override
    public Option createOption(String optionId, Stock underlying, double strikePrice,
                               LocalDate expiry, OptionType type) {
        // Index options typically have multipliers (e.g., S&P 500 options are 100x)
        return new IndexOption(optionId, underlying, strikePrice, expiry, type, 100);
    }

    @Override
    public Future createFuture(String futureId, Stock underlying, LocalDate delivery) {
        // Index futures have contract size and multiplier
        return new IndexFuture(futureId, underlying, delivery, 1, 250); // e.g., S&P 500 futures
    }
}

interface MarketFactory {
    Stock createStock(String symbol, String name, double price);
    Option createOption(String optionId, Stock underlying, double strikePrice,
                        LocalDate expiry, OptionType type);
    Future createFuture(String futureId, Stock underlying, LocalDate delivery);
}

public class StockMarketAbstractFactory {
    private List<Stock> stocks = new ArrayList<>();
    private List<Option> options = new ArrayList<>();
    private List<Future> futures = new ArrayList<>();
    private MarketFactory factory;

    public StockMarketAbstractFactory(MarketFactory factory) {
        this.factory = factory;
    }

    public void addStock(String symbol, String name, double price) {
        stocks.add(factory.createStock(symbol, name, price));
    }

    public void addOption(String optionId, Stock underlying, double strikePrice,
                          LocalDate expiry, OptionType type) {
        options.add(factory.createOption(optionId, underlying, strikePrice, expiry, type));
    }

    public void addFuture(String futureId, Stock underlying, LocalDate delivery) {
        futures.add(factory.createFuture(futureId, underlying, delivery));
    }

    public void displayPortfolio() {
        System.out.println("\n=== Portfolio ===");
        System.out.println("\nStocks:");
        stocks.forEach(System.out::println);

        System.out.println("\nOptions:");
        options.forEach(System.out::println);

        System.out.println("\nFutures:");
        futures.forEach(System.out::println);
    }

    public static void main(String[] args) {
        // Create equity portfolio
        MarketFactory equityFactory = new EquityMarketFactory();
        StockMarketAbstractFactory equityPortfolio = new StockMarketAbstractFactory(equityFactory);

        Stock apple = equityFactory.createStock("AAPL", "Apple Inc.", 175.50);
        Stock microsoft = equityFactory.createStock("MSFT", "Microsoft Corp.", 330.25);

        equityPortfolio.addStock("AAPL", "Apple Inc.", 175.50);
        equityPortfolio.addStock("MSFT", "Microsoft Corp.", 330.25);
        equityPortfolio.addOption("AAPL_C_180", apple, 180.0,
                LocalDate.now().plusMonths(3), OptionType.CALL);
        equityPortfolio.addFuture("AAPL_F_DEC", apple, LocalDate.of(2023, 12, 15));

        System.out.println("=== Equity Portfolio ===");
        equityPortfolio.displayPortfolio();

        // Create derivatives portfolio
        MarketFactory derivativesFactory = new DerivativesMarketFactory();
        StockMarketAbstractFactory derivativesPortfolio = new StockMarketAbstractFactory(derivativesFactory);

        Stock sp500 = derivativesFactory.createStock("SPX", "S&P 500", 4500.0);
        Stock nasdaq = derivativesFactory.createStock("NDX", "NASDAQ 100", 15000.0);

        derivativesPortfolio.addStock("SPX", "S&P 500", 4500.0);
        derivativesPortfolio.addStock("NDX", "NASDAQ 100", 15000.0);
        derivativesPortfolio.addOption("SPX_P_4400", sp500, 4400.0,
                LocalDate.now().plusMonths(6), OptionType.PUT);
        derivativesPortfolio.addFuture("SPX_F_DEC", sp500, LocalDate.of(2023, 12, 15));

        System.out.println("\n=== Derivatives Portfolio ===");
        derivativesPortfolio.displayPortfolio();
    }
}


/**
 * OutPut:
 * === Equity Portfolio ===
 *
 * === Portfolio ===
 *
 * Stocks:
 * Equity [AAPL - Apple Inc.] Price: $175.50 (Yield: 2.00%)
 * Equity [MSFT - Microsoft Corp.] Price: $330.25 (Yield: 2.00%)
 *
 * Options:
 * CALL Option [AAPL_C_180] on AAPL @ $180.00 (Exp: 2025-07-29, Premium: $17.53)
 *
 * Futures:
 * Equity Future [AAPL_F_DEC] on AAPL (Delivery: 2023-12-15, Size: 100, Margin: $1755.00)
 *
 * === Derivatives Portfolio ===
 *
 * === Portfolio ===
 *
 * Stocks:
 * Index Component [SPX - S&P 500] Price: $4500.00 (Weight: 1.00%)
 * Index Component [NDX - NASDAQ 100] Price: $15000.00 (Weight: 1.00%)
 *
 * Options:
 * PUT Index Option [SPX_P_4400] on SPX @ $4400.00 (Exp: 2025-10-29, Mult: 100, Premium: $47795.05)
 *
 * Futures:
 * Index Future [SPX_F_DEC] on SPX (Delivery: 2023-12-15, Size: 1, Mult: 250, Margin: $56250.00)
 */