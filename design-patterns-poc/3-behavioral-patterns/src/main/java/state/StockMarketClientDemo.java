package state;

interface StockState {
  void buyShares(int quantity, StockContext context);

  void sellShares(int quantity, StockContext context);

  void updatePrice(double priceChange, StockContext context);
}

// State when stock is rising
class RisingState implements StockState {
  @Override
  public void buyShares(int quantity, StockContext context) {
    System.out.println("Buying " + quantity + " shares - Market is bullish!");
    // Additional bullish market logic
  }

  @Override
  public void sellShares(int quantity, StockContext context) {
    System.out.println("Selling " + quantity + " shares - Profit taking opportunity");
  }

  @Override
  public void updatePrice(double priceChange, StockContext context) {
    if (priceChange < -2.0) {
      System.out.println("Significant drop! Transitioning to Falling state.");
      context.setState(new FallingState());
    } else if (priceChange < 0.5 && priceChange > -0.5) {
      System.out.println("Price stabilizing. Transitioning to Stable state.");
      context.setState(new StableState());
    }
  }
}

// State when stock is falling
class FallingState implements StockState {
  @Override
  public void buyShares(int quantity, StockContext context) {
    System.out.println("Buying " + quantity + " shares - Potential bargain hunting");
  }

  @Override
  public void sellShares(int quantity, StockContext context) {
    System.out.println("Selling " + quantity + " shares - Market is bearish!");
    // Additional bearish market logic
  }

  @Override
  public void updatePrice(double priceChange, StockContext context) {
    if (priceChange > 2.0) {
      System.out.println("Significant rise! Transitioning to Rising state.");
      context.setState(new RisingState());
    } else if (priceChange < 0.5 && priceChange > -0.5) {
      System.out.println("Price stabilizing. Transitioning to Stable state.");
      context.setState(new StableState());
    }
  }
}

// State when stock is stable
class StableState implements StockState {
  @Override
  public void buyShares(int quantity, StockContext context) {
    System.out.println("Buying " + quantity + " shares - Market is stable");
  }

  @Override
  public void sellShares(int quantity, StockContext context) {
    System.out.println("Selling " + quantity + " shares - Market is stable");
  }

  @Override
  public void updatePrice(double priceChange, StockContext context) {
    if (priceChange > 1.0) {
      System.out.println("Price starting to rise. Transitioning to Rising state.");
      context.setState(new RisingState());
    } else if (priceChange < -1.0) {
      System.out.println("Price starting to fall. Transitioning to Falling state.");
      context.setState(new FallingState());
    }
  }
}

class StockContext {
  private StockState currentState;
  private final String symbol;
  private double price;

  public StockContext(String symbol, double initialPrice) {
    this.symbol = symbol;
    this.price = initialPrice;
    // Start in stable state
    this.currentState = new StableState();
  }

  public void setState(StockState newState) {
    this.currentState = newState;
  }

  public void buyShares(int quantity) {
    currentState.buyShares(quantity, this);
  }

  public void sellShares(int quantity) {
    currentState.sellShares(quantity, this);
  }

  public void updatePrice(double priceChange) {
    this.price += priceChange;
    System.out.println(symbol + " price updated to: " + price + " (" +
        (priceChange >= 0 ? "+" : "") + priceChange + ")");
    currentState.updatePrice(priceChange, this);
  }

  public void showState() {
    System.out.println(symbol + " current state: " + currentState.getClass().getSimpleName());
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    StockContext appleStock = new StockContext("AAPL", 150.0);
    appleStock.showState();  // Shows initial state (Stable)

    // Price changes trigger state transitions
    appleStock.updatePrice(1.5);  // Transition to Rising
    appleStock.showState();

    appleStock.buyShares(100);    // Behavior depends on current state
    appleStock.sellShares(50);

    appleStock.updatePrice(-2.5); // Transition to Falling
    appleStock.showState();

    appleStock.buyShares(100);    // Different behavior now
    appleStock.sellShares(50);

    appleStock.updatePrice(0.2);  // Transition to Stable
    appleStock.showState();
  }
}
