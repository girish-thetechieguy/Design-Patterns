package command;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

interface OrderCommand {
    void execute();
    void undo();  // For rollback capability
}

// Buy Command
class BuyStockCommand implements OrderCommand {
    private TradingSystem tradingSystem;
    private String symbol;
    private int quantity;

    public BuyStockCommand(TradingSystem tradingSystem, String symbol, int quantity) {
        this.tradingSystem = tradingSystem;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        tradingSystem.buyStock(symbol, quantity);
    }

    @Override
    public void undo() {
        tradingSystem.sellStock(symbol, quantity);  // Reverts the buy
    }
}

// Sell Command
class SellStockCommand implements OrderCommand {
    private TradingSystem tradingSystem;
    private String symbol;
    private int quantity;

    public SellStockCommand(TradingSystem tradingSystem, String symbol, int quantity) {
        this.tradingSystem = tradingSystem;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    // Similar to BuyStockCommand but for selling
    @Override
    public void execute() {
        tradingSystem.sellStock(symbol, quantity);
    }

    @Override
    public void undo() {
        tradingSystem.buyStock(symbol, quantity);  // Reverts the sell
    }
}

class TradingSystem {
    public void buyStock(String symbol, int quantity) {
        System.out.println("[BUY] " + quantity + " shares of " + symbol);
        // Actual market buy logic...
    }

    public void sellStock(String symbol, int quantity) {
        System.out.println("[SELL] " + quantity + " shares of " + symbol);
        // Actual market sell logic...
    }
}

class OrderProcessor {
    private Queue<OrderCommand> orderQueue = new LinkedList<>();
    private Stack<OrderCommand> undoStack = new Stack<>();

    public void addOrder(OrderCommand command) {
        orderQueue.add(command);
    }

    public void processOrders() {
        while (!orderQueue.isEmpty()) {
            OrderCommand command = orderQueue.poll();
            command.execute();
            undoStack.push(command);  // Track for undo
        }
    }

    public void undoLastOrder() {
        if (!undoStack.isEmpty()) {
            OrderCommand command = undoStack.pop();
            command.undo();
        }
    }
}

public class StockMarketClientDemo {
    public static void main(String[] args) {
        // Setup
        TradingSystem tradingSystem = new TradingSystem();
        OrderProcessor processor = new OrderProcessor();

        // Create commands
        OrderCommand buyApple = new BuyStockCommand(tradingSystem, "AAPL", 100);
        OrderCommand sellMicrosoft = new SellStockCommand(tradingSystem, "MSFT", 50);

        // Queue orders
        processor.addOrder(buyApple);
        processor.addOrder(sellMicrosoft);

        // Execute all
        processor.processOrders();

        // Undo last order (sell MSFT)
        System.out.println("\nUndoing last order:");
        processor.undoLastOrder();
    }
}
