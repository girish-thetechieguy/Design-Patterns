package mediator;

import java.util.ArrayList;
import java.util.List;

interface TradingMediator {
  void placeOrder(Trader trader, String type, String symbol, int shares);

  void addTrader(Trader trader);

  void addExchange(Exchange exchange);
}

class StockExchangeMediator implements TradingMediator {
  private final List<Trader> traders = new ArrayList<>();
  private final List<Exchange> exchanges = new ArrayList<>();

  @Override
  public void addTrader(Trader trader) {
    traders.add(trader);
  }

  @Override
  public void addExchange(Exchange exchange) {
    exchanges.add(exchange);
  }

  @Override
  public void placeOrder(Trader trader, String type, String symbol, int shares) {
    // Validate order
    if (shares <= 0) {
      System.out.println("[Mediator] Invalid order quantity");
      return;
    }

    // Route order to all exchanges
    for (Exchange exchange : exchanges) {
      exchange.executeOrder(type, symbol, shares);
      System.out.printf("[Mediator] %s's %s order for %d shares of %s routed%n",
          trader.getName(), type, shares, symbol);
    }

    // Notify other traders (e.g., for market impact analysis)
    traders.stream()
        .filter(t -> !t.equals(trader))
        .forEach(t -> t.notifyOrderPlaced(symbol));
  }
}

class Trader {
  private final String name;
  private final TradingMediator mediator;

  public Trader(String name, TradingMediator mediator) {
    this.name = name;
    this.mediator = mediator;
    mediator.addTrader(this);
  }

  public void buyStock(String symbol, int shares) {
    mediator.placeOrder(this, "BUY", symbol, shares);
  }

  public void notifyOrderPlaced(String symbol) {
    System.out.printf("[Trader %s] Notification: New order for %s%n", name, symbol);
  }

  public String getName() {
    return name;
  }
}

class Exchange {
  private final String name;
  TradingMediator mediator;

  public Exchange(String name, TradingMediator mediator) {
    this.name = name;
    this.mediator = mediator;
    mediator.addExchange(this);
  }

  public void executeOrder(String type, String symbol, int shares) {
    System.out.printf("[Exchange %s] Executed %s order for %d shares of %s%n",
        name, type, shares, symbol);
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    // Create mediator
    TradingMediator mediator = new StockExchangeMediator();

    // Create participants
    Trader alice = new Trader("Alice", mediator);
//    Trader bob = new Trader("Bob", mediator);
//    Exchange nasdaq = new Exchange("NASDAQ", mediator);
//    Exchange nyse = new Exchange("NYSE", mediator);

    // Alice places an order (automatically routed to all exchanges)
    alice.buyStock("AAPL", 100);
  }
}
