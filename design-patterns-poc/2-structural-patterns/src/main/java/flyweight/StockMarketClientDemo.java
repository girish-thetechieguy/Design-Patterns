package flyweight;

import java.util.HashMap;
import java.util.Map;

class StockMetadata {
    private final String symbol;
    private final String companyName;
    private final String sector;

    public StockMetadata(String symbol, String companyName, String sector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
    }

    public String getDetails() {
        return String.format("%s (%s) - Sector: %s", companyName, symbol, sector);
    }
}

class StockMetadataFactory {
    private static final Map<String, StockMetadata> metadataCache = new HashMap<>();

    public static StockMetadata getStockMetadata(String symbol, String companyName, String sector) {
        // Use symbol as cache key
        return metadataCache.computeIfAbsent(symbol,
                k -> new StockMetadata(symbol, companyName, sector));
    }

    public static int getCacheSize() {
        return metadataCache.size();
    }
}

// Extrinsic state (unique per stock instance)
class StockTicker {
    private final StockMetadata metadata; // Shared flyweight
    private double currentPrice;
    private long volume;

    public StockTicker(String symbol, String companyName, String sector) {
        this.metadata = StockMetadataFactory.getStockMetadata(symbol, companyName, sector);
    }

    public void update(double price, long volume) {
        this.currentPrice = price;
        this.volume = volume;
    }

    public String display() {
        return String.format("%s | Price: $%.2f | Volume: %,d",
                metadata.getDetails(), currentPrice, volume);
    }
}

public class StockMarketClientDemo {
    public static void main(String[] args) {
        // Create multiple stock tickers
        StockTicker apple = new StockTicker("AAPL", "Apple Inc.", "Technology");
        apple.update(175.50, 12_000_000);

        StockTicker apple2 = new StockTicker("AAPL", "Apple Inc.", "Technology");
        apple2.update(176.25, 15_000_000);

        StockTicker google = new StockTicker("GOOG", "Alphabet Inc.", "Technology");
        google.update(135.75, 8_000_000);

        StockTicker tesla = new StockTicker("TSLA", "Tesla Inc.", "Automotive");
        tesla.update(200.80, 10_500_000);

        // Display stocks
        System.out.println(apple.display());
        System.out.println(apple2.display());
        System.out.println(google.display());
        System.out.println(tesla.display());

        // Verify flyweight usage
        System.out.println("\nMetadata cache size: " + StockMetadataFactory.getCacheSize());
        System.out.println("Total stock instances: 4");
    }
}
