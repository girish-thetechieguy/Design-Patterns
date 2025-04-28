package builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Key Features of This Implementation:
 * Immutable Objects: The Stock class is immutable once built
 * Fluent Interface: Method chaining makes the client code readable
 * Validation: The builder validates parameters before creating the object
 * Flexible Construction: Clients can specify only the parameters they need
 * Default Values: Optional parameters have sensible defaults
 * Complex Object Creation: Handles objects with many attributes cleanly
 */

class Stock {
    private final String symbol;
    private final String companyName;
    private final double currentPrice;
    private final double high52Week;
    private final double low52Week;
    private final double peRatio;
    private final double dividendYield;
    private final String sector;
    private final String exchange;

    private Stock(StockBuilder builder) {
        this.symbol = builder.symbol;
        this.companyName = builder.companyName;
        this.currentPrice = builder.currentPrice;
        this.high52Week = builder.high52Week;
        this.low52Week = builder.low52Week;
        this.peRatio = builder.peRatio;
        this.dividendYield = builder.dividendYield;
        this.sector = builder.sector;
        this.exchange = builder.exchange;
    }

    // Getters for all fields (omitted for brevity)
    // ...

    @Override
    public String toString() {
        return String.format("Stock [%s - %s] Price: $%.2f (PE: %.2f, Yield: %.2f%%)",
                symbol, companyName, currentPrice, peRatio, dividendYield);
    }

    // Builder class
    public static class StockBuilder {
        // Required parameters
        private final String symbol;
        private final String companyName;

        // Optional parameters - initialized to default values
        private double currentPrice = 0.0;
        private double high52Week = 0.0;
        private double low52Week = 0.0;
        private double peRatio = 0.0;
        private double dividendYield = 0.0;
        private String sector = "Unknown";
        private String exchange = "NYSE"; // Default exchange

        public StockBuilder(String symbol, String companyName) {
            this.symbol = symbol;
            this.companyName = companyName;
        }

        public StockBuilder currentPrice(double currentPrice) {
            this.currentPrice = currentPrice;
            return this;
        }

        public StockBuilder high52Week(double high52Week) {
            this.high52Week = high52Week;
            return this;
        }

        public StockBuilder low52Week(double low52Week) {
            this.low52Week = low52Week;
            return this;
        }

        public StockBuilder peRatio(double peRatio) {
            this.peRatio = peRatio;
            return this;
        }

        public StockBuilder dividendYield(double dividendYield) {
            this.dividendYield = dividendYield;
            return this;
        }

        public StockBuilder sector(String sector) {
            this.sector = sector;
            return this;
        }

        public StockBuilder exchange(String exchange) {
            this.exchange = exchange;
            return this;
        }

        public Stock build() {
            validate();
            return new Stock(this);
        }

        private void validate() {
            if (symbol == null || symbol.trim().isEmpty()) {
                throw new IllegalArgumentException("Stock symbol cannot be null or empty");
            }
            if (companyName == null || companyName.trim().isEmpty()) {
                throw new IllegalArgumentException("Company name cannot be null or empty");
            }
            if (currentPrice < 0) {
                throw new IllegalArgumentException("Current price cannot be negative");
            }
            // Add more validation as needed
        }
    }
}

// Optional class with mock API response
class StockMarketService {
    public Stock getStockFromAPI(String symbol) {
        // Simulate fetching stock data from an API
        // In a real application, this would make an HTTP request

        return switch (symbol.toUpperCase()) {
            case "AAPL" -> new Stock.StockBuilder("AAPL", "Apple Inc.")
                    .currentPrice(175.50)
                    .peRatio(29.34)
                    .sector("Technology")
                    .exchange("NASDAQ")
                    .build();
            case "GOOGL" -> new Stock.StockBuilder("GOOGL", "Alphabet Inc.")
                    .currentPrice(135.25)
                    .peRatio(24.18)
                    .sector("Technology")
                    .exchange("NASDAQ")
                    .build();
            default -> throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        };
    }
}

public class StockPortfolioBuilderDesignDemo {
    private final List<Stock> stocks;

    public StockPortfolioBuilderDesignDemo() {
        this.stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void displayPortfolio() {
        System.out.println("\nStock Portfolio:");
        System.out.println("----------------");
        for (Stock stock : stocks) {
            System.out.println(stock);
        }
        System.out.println("----------------");
    }

    public static void main(String[] args) {
        StockPortfolioBuilderDesignDemo portfolio = new StockPortfolioBuilderDesignDemo();

        // Building stocks with different combinations of parameters
        Stock apple = new Stock.StockBuilder("AAPL", "Apple Inc.")
                .currentPrice(175.50)
                .high52Week(198.23)
                .low52Week(124.17)
                .peRatio(29.34)
                .dividendYield(0.5)
                .sector("Technology")
                .exchange("NASDAQ")
                .build();

        Stock microsoft = new Stock.StockBuilder("MSFT", "Microsoft Corporation")
                .currentPrice(330.25)
                .peRatio(32.15)
                .sector("Technology")
                .build();

        Stock tesla = new Stock.StockBuilder("TSLA", "Tesla Inc.")
                .currentPrice(200.75)
                .high52Week(299.29)
                .low52Week(101.81)
                .exchange("NASDAQ")
                .build();

        // Add stocks to portfolio
        portfolio.addStock(apple);
        portfolio.addStock(microsoft);
        portfolio.addStock(tesla);

        // Display portfolio
        portfolio.displayPortfolio();
    }
}
