package momento;

import java.util.Stack;

// Originator: The object whose state we save/restore
class Stock {
  private final String symbol;
  private double price;
  private int volume;

  public Stock(String symbol, double price, int volume) {
    this.symbol = symbol;
    this.price = price;
    this.volume = volume;
  }

  // Save state to a memento
  public StockMemento save() {
    return new StockMemento(this.price, this.volume);
  }

  // Restore state from a memento
  public void restore(StockMemento memento) {
    this.price = memento.getPrice();
    this.volume = memento.getVolume();
  }

  // Simulate price change
  public void updatePrice(double newPrice, int newVolume) {
    this.price = newPrice;
    this.volume = newVolume;
  }

  // Getters
  public String getSymbol() { return symbol; }
  public double getPrice() { return price; }
  public int getVolume() { return volume; }
}

// Memento: Immutable snapshot of Stock's state
class StockMemento {
  private final double price;
  private final int volume;

  public StockMemento(double price, int volume) {
    this.price = price;
    this.volume = volume;
  }

  // Getters (no setters to ensure immutability)
  public double getPrice() { return price; }
  public int getVolume() { return volume; }
}

// Caretaker: Manages mementos (undo/redo stack)
class Portfolio {
  private final Stack<StockMemento> history = new Stack<>();

  public void saveState(Stock stock) {
    history.push(stock.save());
  }

  public void undo(Stock stock) {
    if (!history.isEmpty()) {
      stock.restore(history.pop());
    }
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    Stock apple = new Stock("AAPL", 170.50, 1000);
    Portfolio portfolio = new Portfolio();

    // Save initial state
    portfolio.saveState(apple);
    System.out.printf("Initial: %s - Price: $%.2f, Volume: %d\n",
        apple.getSymbol(), apple.getPrice(), apple.getVolume());

    // Update price (new state)
    apple.updatePrice(172.10, 1500);
    System.out.printf("Updated: %s - Price: $%.2f, Volume: %d\n",
        apple.getSymbol(), apple.getPrice(), apple.getVolume());

    // Undo to previous state
    portfolio.undo(apple);
    System.out.printf("Undone:  %s - Price: $%.2f, Volume: %d\n",
        apple.getSymbol(), apple.getPrice(), apple.getVolume());
  }
}
