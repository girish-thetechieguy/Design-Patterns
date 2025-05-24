package template;

abstract class StockOrderProcessor {
  // The template method - defines the order processing skeleton
  public final void processOrder() {
    validateOrder();
    checkAccountBalance();
    executeTrade();
    updatePortfolio();
    sendConfirmation();
  }

  // Steps that must be implemented by subclasses
  protected abstract void validateOrder();
  protected abstract void executeTrade();

  // Common steps with default implementation
  protected void checkAccountBalance() {
    System.out.println("Checking account balance...");
  }

  protected void updatePortfolio() {
    System.out.println("Updating portfolio holdings...");
  }

  protected void sendConfirmation() {
    System.out.println("Sending trade confirmation email...");
  }
}

// Market Order Processor
class MarketOrderProcessor extends StockOrderProcessor {
  private final String stockSymbol;
  private final int quantity;

  public MarketOrderProcessor(String stockSymbol, int quantity) {
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
  }

  @Override
  protected void validateOrder() {
    System.out.println("Validating market order for " + quantity +
        " shares of " + stockSymbol);
  }

  @Override
  protected void executeTrade() {
    System.out.println("Executing market order immediately at current price for " +
        quantity + " shares of " + stockSymbol);
  }
}

// Limit Order Processor
class LimitOrderProcessor extends StockOrderProcessor {
  private final String stockSymbol;
  private final int quantity;
  private final double limitPrice;

  public LimitOrderProcessor(String stockSymbol, int quantity, double limitPrice) {
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
    this.limitPrice = limitPrice;
  }

  @Override
  protected void validateOrder() {
    System.out.println("Validating limit order for " + quantity +
        " shares of " + stockSymbol + " at $" + limitPrice);
  }

  @Override
  protected void executeTrade() {
    System.out.println("Placing limit order in order book - will execute when " +
        stockSymbol + " reaches $" + limitPrice);
  }

  @Override
  protected void sendConfirmation() {
    super.sendConfirmation();
    System.out.println("Adding note: Limit order remains active until filled or cancelled");
  }
}

// Stop Loss Order Processor
class StopLossOrderProcessor extends StockOrderProcessor {
  private final String stockSymbol;
  private final int quantity;
  private final double stopPrice;

  public StopLossOrderProcessor(String stockSymbol, int quantity, double stopPrice) {
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
    this.stopPrice = stopPrice;
  }

  @Override
  protected void validateOrder() {
    System.out.println("Validating stop loss order for " + quantity +
        " shares of " + stockSymbol + " triggered at $" + stopPrice);
  }

  @Override
  protected void executeTrade() {
    System.out.println("Monitoring price - will convert to market order if " +
        stockSymbol + " falls to $" + stopPrice);
  }
}


public class StockMarketClientDemo {
  public static void main(String[] args) {
    System.out.println("Processing Market Order:");
    StockOrderProcessor marketOrder = new MarketOrderProcessor("AAPL", 100);
    marketOrder.processOrder();

    System.out.println("\nProcessing Limit Order:");
    StockOrderProcessor limitOrder = new LimitOrderProcessor("GOOGL", 50, 2750.50);
    limitOrder.processOrder();

    System.out.println("\nProcessing Stop Loss Order:");
    StockOrderProcessor stopLossOrder = new StopLossOrderProcessor("TSLA", 30, 650.75);
    stopLossOrder.processOrder();
  }
}
