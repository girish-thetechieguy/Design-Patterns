package proxy;

import java.util.HashSet;
import java.util.Set;

interface StockService {
    double getStockPrice(String symbol);
    void subscribe(String user);
}

class RealStockService implements StockService {
    // Connects to actual stock exchange API

    @Override
    public double getStockPrice(String symbol) {
        // Simulate expensive API call
        System.out.println("Fetching real-time price for " + symbol + " from exchange...");

        // Mock prices
        return switch (symbol) {
            case "AAPL" -> 175.50;
            case "GOOG" -> 135.25;
            case "TSLA" -> 200.75;
            default -> throw new IllegalArgumentException("Unknown stock symbol");
        };
    }

    @Override
    public void subscribe(String user) {
        System.out.println(user + " subscribed to premium stock service");
    }
}

class StockServiceProxy implements StockService {
    private RealStockService realService;
    private Set<String> subscribedUsers = new HashSet<>();

    @Override
    public double getStockPrice(String symbol) {
        // Lazy initialization
        if (realService == null) {
            realService = new RealStockService();
        }

        // Check for free tier access (delayed prices)
        if (!subscribedUsers.contains(getCurrentUser())) {
            System.out.println("Returning cached price (15 min delay) for free user");
            return getCachedPrice(symbol); // Free users get delayed data
        }

        // Premium users get real-time data
        return realService.getStockPrice(symbol);
    }

    @Override
    public void subscribe(String user) {
        subscribedUsers.add(user);
        if (realService == null) {
            realService = new RealStockService();
        }
        realService.subscribe(user);
    }

    private double getCachedPrice(String symbol) {
        // Simulate cached prices
        return switch (symbol) {
            case "AAPL" -> 174.80;
            case "GOOG" -> 134.90;
            case "TSLA" -> 199.50;
            default -> throw new IllegalArgumentException("Unknown stock symbol");
        };
    }

    private String getCurrentUser() {
        // In real app, get from security context
        return "current_user";
    }
}

public class TradingApplicationWithProxyPatternClient {
    public static void main(String[] args) {
        // Client interacts with proxy
        StockService stockService = new StockServiceProxy();

        // Free user gets cached prices
        System.out.println("AAPL price: $" + stockService.getStockPrice("AAPL"));

        // User subscribes
        stockService.subscribe("john_doe");

        // Now gets real-time prices
        System.out.println("GOOG price: $" + stockService.getStockPrice("GOOG"));
    }
}
