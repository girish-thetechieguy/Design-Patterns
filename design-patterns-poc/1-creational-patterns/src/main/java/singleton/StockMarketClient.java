package singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MarketDataFeedManager {
    private static Logger logger = LoggerFactory.getLogger(MarketDataFeedManager.class);
    // Private static instance of the singleton
    private static MarketDataFeedManager instance;

    // Private constructor to prevent instantiation
    private MarketDataFeedManager() {
        // Initialize connection to market data feed
        logger.info("Initializing market data feed connection...");
    }

    // Public method to get the singleton instance
    public static synchronized MarketDataFeedManager getInstance() {
        if (instance == null) {
            instance = new MarketDataFeedManager();
        }
        return instance;
    }

    // Business methods
    public void subscribeToStock(String symbol) {
        logger.info("Subscribed to stock: " + symbol);
    }

    public double getLatestPrice(String symbol) {
        // In a real implementation, this would fetch from the actual feed
        logger.info("Fetching latest price for: {}", symbol);
        return Math.random() * 1000; // Mock price
    }

    public void disconnect() {
        logger.info("Disconnecting from market data feed...");
    }
}

public class StockMarketClient {
    private static Logger logger = LoggerFactory.getLogger(StockMarketClient.class);
    public static void main(String[] args) {
        // Get the singleton instance
        MarketDataFeedManager feedManager = MarketDataFeedManager.getInstance();

        // Use the feed manager
        feedManager.subscribeToStock("AAPL");
        double price = feedManager.getLatestPrice("AAPL");
        logger.info("Apple stock price: ${}", price);

        // Try to create another instance - will return the same one
        MarketDataFeedManager anotherFeedManager = MarketDataFeedManager.getInstance();
        logger.info("Same instance? {}", feedManager == anotherFeedManager);
    }
}


//OutPut:
//        23:58:12.628 [main] INFO singleton.MarketDataFeedManager - Initializing market data feed connection...
//        23:58:12.632 [main] INFO singleton.MarketDataFeedManager - Subscribed to stock: AAPL
//        23:58:12.632 [main] INFO singleton.MarketDataFeedManager - Fetching latest price for: AAPL
//        23:58:12.634 [main] INFO singleton.StockMarketClient - Apple stock price: $532.6161664349526
//        23:58:12.634 [main] INFO singleton.StockMarketClient - Same instance? true