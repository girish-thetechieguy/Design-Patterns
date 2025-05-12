package facade;

/**
 * Key Benefits
 * Advantage	Stock Market Example
 * Simplifies Client Code	Traders call purchaseStock() instead of 4 different classes.
 * Decouples Subsystems	Changes to OrderExecutor won’t affect clients.
 * Single Responsibility	Facade only coordinates, subsystems handle specifics.
 * Security Control	Facade can add validation before executing trades.
 *
 * Real-World Analogies
 * Stock Trading App – Users see a "Buy" button (Facade), not the 10+ microservices behind it.
 * Brokerage APIs – A single REST endpoint (Facade) hides complex order-routing logic.
 *
 * When to Use?
 * ✅ You need to provide a simple interface to a complex system (e.g., trading platforms).
 * ✅ Subsystems are tightly coupled and need abstraction.
 * ✅ You want to layer security/validation (e.g., pre-trade checks).
 */

// Subsystem 1: Stock Verification
class StockVerifier {
    public boolean isStockValid(String symbol) {
        System.out.println("Verifying stock: " + symbol);
        // API call to check stock existence
        return true; // Simplified
    }
}

// Subsystem 2: Portfolio Management
class PortfolioManager {
    public void addToPortfolio(String user, String stock, int shares) {
        System.out.println("Adding " + shares + " shares of " + stock + " to " + user + "'s portfolio");
    }
}

// Subsystem 3: Order Execution
class OrderExecutor {
    public void executeOrder(String user, String stock, int shares, String type) {
        System.out.println("Executing " + type + " order for " + shares + " shares of " + stock);
    }
}

// Subsystem 4: Notifications
class NotificationService {
    public void sendNotification(String user, String message) {
        System.out.println("Sending notification to " + user + ": " + message);
    }
}

class StockTradingFacade {
    private StockVerifier verifier;
    private PortfolioManager portfolio;
    private OrderExecutor executor;
    private NotificationService notifier;

    public StockTradingFacade() {
        this.verifier = new StockVerifier();
        this.portfolio = new PortfolioManager();
        this.executor = new OrderExecutor();
        this.notifier = new NotificationService();
    }

    // Unified method to handle stock purchase
    public void purchaseStock(String user, String stock, int shares) {
        if (verifier.isStockValid(stock)) {
            executor.executeOrder(user, stock, shares, "BUY");
            portfolio.addToPortfolio(user, stock, shares);
            notifier.sendNotification(user, "Purchased " + shares + " shares of " + stock);
        } else {
            notifier.sendNotification(user, "Stock " + stock + " is invalid!");
        }
    }
}

public class StockMarketFacadeClientDemo {
    public static void main(String[] args) {
        StockTradingFacade tradingFacade = new StockTradingFacade();

        // Simple interface for complex operations
        tradingFacade.purchaseStock("Alice", "AAPL", 10);
    }
}
