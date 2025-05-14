package chainOfResponsibility;

/**
 * The Chain of Responsibility Pattern lets you pass requests along a chain of handlers,
 * where each handler decides either to process the request or pass it to the next handler.
 * In stock trading, this is perfect for order validation workflows.
 * Without this pattern, you'd need complex nested conditionals:
 * if (validator.checkFunds(order)) {
 *     if (validator.checkCompliance(order)) {
 *         if (validator.checkMarketHours(order)) {
 *             executeOrder(order);
 *         }
 *     }
 * }
 *
 * Solution:
 *
 */

interface OrderHandler {
    void setNext(OrderHandler next);
    void process(Order order);
}

// Order.java - The request object passed through the chain
class Order {
    private String stockSymbol;
    private int quantity;
    private double price;
    private Account account;
    private Stock stock;

    public Order(String stockSymbol, int quantity, double price) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.account = new Account(10000); // Sample account with $10,000 balance
        this.stock = new Stock(stockSymbol);
    }

    // Getters
    public String getStockSymbol() { return stockSymbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getAmount() { return quantity * price; }
    public Account getAccount() { return account; }
    public Stock getStock() { return stock; }
}

// Supporting classes
class Account {
    private double balance;

    public Account(double balance) {
        this.balance = balance;
    }

    public double getBalance() { return balance; }
}

class Stock {
    private String symbol;
    private boolean restricted;

    public Stock(String symbol) {
        this.symbol = symbol;
        // In reality, this would check a database
        this.restricted = symbol.equalsIgnoreCase("RESTRICTED_STOCK");
    }

    public boolean isRestricted() { return restricted; }
}

class Market {
    public static boolean isOpen() {
        // Simplified - in reality would check market hours
        return true;
    }
}

// Handler 1: Check Sufficient Funds
class FundsHandler implements OrderHandler {
    private OrderHandler next;

    public void setNext(OrderHandler next) {
        this.next = next;
    }

    public void process(Order order) {
        if (order.getAccount().getBalance() >= order.getAmount()) {
            System.out.println("Funds check passed");
            if (next != null) next.process(order);
        } else {
            System.out.println("Error: Insufficient funds");
        }
    }
}

// Handler 2: Check Compliance (e.g., restricted stocks)
class ComplianceHandler implements OrderHandler {
    private OrderHandler next;

    public void setNext(OrderHandler next) {
        this.next = next;
    }

    public void process(Order order) {
        if (!order.getStock().isRestricted()) {
            System.out.println("Compliance check passed");
            if (next != null) next.process(order);
        } else {
            System.out.println("Error: Restricted stock");
        }
    }
}

// Handler 3: Check Market Hours
class MarketHoursHandler implements OrderHandler {
    public void setNext(OrderHandler next) {} // Last in chain

    public void process(Order order) {
        if (Market.isOpen()) {
            System.out.println("Market is open. Executing order!");
            executeOrder(order);
        } else {
            System.out.println("Error: Market closed");
        }
    }

    private void executeOrder(Order order) {
        // Order execution logic
    }
}

class OrderProcessor {
    private OrderHandler chain;

    public OrderProcessor() {
        // Create handlers
        OrderHandler funds = new FundsHandler();
        OrderHandler compliance = new ComplianceHandler();
        OrderHandler hours = new MarketHoursHandler();

        // Build chain: Funds → Compliance → Market Hours
        funds.setNext(compliance);
        compliance.setNext(hours);

        this.chain = funds;
    }

    public void processOrder(Order order) {
        chain.process(order);
    }
}

public class StockMarketClientDemo {
    public static void main(String[] args) {
        OrderProcessor processor = new OrderProcessor();

        Order order1 = new Order("AAPL", 10, 150.0);
        System.out.println("Processing order 1:");
        processor.processOrder(order1);

        System.out.println("\nProcessing order 2:");
        Order order2 = new Order("RESTRICTED_STOCK", 100, 50.0);
        processor.processOrder(order2);
    }
}
