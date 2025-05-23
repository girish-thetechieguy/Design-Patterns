package stratergy;

interface TradingStrategy {
  void executeOrder(String stockSymbol, int quantity, double price);
}

// Market Order - executes immediately at current price
class MarketOrderStrategy implements TradingStrategy {
  @Override
  public void executeOrder(String stockSymbol, int quantity, double price) {
    System.out.printf("Executing MARKET ORDER: %d shares of %s at current market price (requested at $%.2f)\n",
        quantity, stockSymbol, price);
    // Actual market order execution logic would go here
  }
}

// Limit Order - executes only at specified price or better
class LimitOrderStrategy implements TradingStrategy {
  @Override
  public void executeOrder(String stockSymbol, int quantity, double price) {
    System.out.printf("Placing LIMIT ORDER: %d shares of %s at $%.2f (will execute only at this price or better)\n",
        quantity, stockSymbol, price);
    // Limit order logic would monitor market and execute when price is right
  }
}

// Stop Loss Order - executes when price hits specified level
class StopLossOrderStrategy implements TradingStrategy {
  @Override
  public void executeOrder(String stockSymbol, int quantity, double price) {
    System.out.printf("Setting STOP LOSS ORDER: %d shares of %s will sell if price drops to $%.2f\n",
        quantity, stockSymbol, price);
    // Stop loss logic would monitor market and trigger sale when price drops
  }
}

class TradingSystem {
  private TradingStrategy strategy;
  private String traderName;

  public TradingSystem(String traderName) {
    this.traderName = traderName;
    // Default to market orders
    this.strategy = new MarketOrderStrategy();
  }

  public void setStrategy(TradingStrategy strategy) {
    this.strategy = strategy;
  }

  public void executeTrade(String stockSymbol, int quantity, double price) {
    System.out.printf("\n%s is attempting to trade %s:\n", traderName, stockSymbol);
    strategy.executeOrder(stockSymbol, quantity, price);
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    TradingSystem trader = new TradingSystem("John Doe");

    // Default market order
    trader.executeTrade("AAPL", 100, 150.25);

    // Switch to limit order
    trader.setStrategy(new LimitOrderStrategy());
    trader.executeTrade("GOOGL", 50, 2750.50);

    // Switch to stop loss order
    trader.setStrategy(new StopLossOrderStrategy());
    trader.executeTrade("TSLA", 30, 650.75);

    // Change back to market order
    trader.setStrategy(new MarketOrderStrategy());
    trader.executeTrade("AMZN", 10, 3250.00);
  }
}
