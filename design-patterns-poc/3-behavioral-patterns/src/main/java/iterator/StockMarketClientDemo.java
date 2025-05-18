package iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

interface StockIterator {
  boolean hasNext();

  Stock next();
}

class Stock {
  private final String symbol;
  private final double price;

  public Stock(String symbol, double price) {
    this.symbol = symbol;
    this.price = price;
  }

  @Override
  public String toString() {
    return symbol + ": $" + price;
  }
}

class PortfolioIterator implements StockIterator {
  private final List<Stock> stocks;
  private int position = 0;

  public PortfolioIterator(List<Stock> stocks) {
    this.stocks = stocks;
  }

  @Override
  public boolean hasNext() {
    return position < stocks.size();
  }

  @Override
  public Stock next() {
    if (!hasNext()) throw new NoSuchElementException();
    return stocks.get(position++);
  }
}

class WatchlistIterator implements StockIterator {
  private final Stock[] watchlist;
  private int index = 0;

  public WatchlistIterator(Stock[] watchlist) {
    this.watchlist = watchlist;
  }

  @Override
  public boolean hasNext() {
    return index < watchlist.length && watchlist[index] != null;
  }

  @Override
  public Stock next() {
    if (!hasNext()) throw new NoSuchElementException();
    return watchlist[index++];
  }
}

interface StockCollection {
  StockIterator createIterator();
}

class Portfolio implements StockCollection {
  private final List<Stock> stocks = new ArrayList<>();

  public void addStock(Stock stock) {
    stocks.add(stock);
  }

  @Override
  public StockIterator createIterator() {
    return new PortfolioIterator(stocks);
  }
}

class Watchlist implements StockCollection {
  private final Stock[] stocks = new Stock[10];
  private int count = 0;

  public void addStock(Stock stock) {
    if (count < stocks.length) {
      stocks[count++] = stock;
    }
  }

  @Override
  public StockIterator createIterator() {
    return new WatchlistIterator(stocks);
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    // Portfolio (List implementation)
    Portfolio portfolio = new Portfolio();
    portfolio.addStock(new Stock("AAPL", 175.0));
    portfolio.addStock(new Stock("MSFT", 330.0));

    System.out.println("Portfolio:");
    printStocks(portfolio.createIterator());

    // Watchlist (Array implementation)
    Watchlist watchlist = new Watchlist();
    watchlist.addStock(new Stock("TSLA", 850.0));
    watchlist.addStock(new Stock("NVDA", 650.0));

    System.out.println("\nWatchlist:");
    printStocks(watchlist.createIterator());
  }

  // Client works with abstract StockIterator
  private static void printStocks(StockIterator iterator) {
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }
}
