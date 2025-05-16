package interpretator;

import java.util.HashMap;
import java.util.Map;

/**
 * The Interpreter Pattern defines a grammar for a language and provides an interpreter to evaluate expressions.
 * In stock trading, this is useful for parsing and executing rule-based trading strategies
 * (e.g., "Buy if AAPL > $150 AND volume > 1M").
 * Problem : if (stock.equals("AAPL") && price > 150 && volume > 1_000_000) {
 *     executeBuy();
 * }
 * Key Benefits:
 * Advantage	Stock Market Application
 * Flexible Rules	Add new conditions without code changes (e.g., OR, NOT).
 * Dynamic Strategies	Rules can be loaded from config files/databases.
 * Decoupled Logic	Separate rule definition from execution.
 * Real-World Uses
 * Algorithmic Trading
 * Parse rules like:
 * IF (RSI(14) < 30 AND MACD() > 0) THEN BUY
 * Risk Management
 * Evaluate: IF (PortfolioRisk() > 5%) THEN HEDGE
 * Alert Systems
 * Custom alerts: NOTIFY IF (VOLUME("TSLA") > 10M AND PRICE_CHANGE() > 5%)
 * When to Use?
 * ✅ You need to evaluate domain-specific languages (e.g., trading rules).
 * ✅ Rules are dynamic/changing frequently.
 * ✅ You want to avoid hardcoding conditionals.
 */

interface StockExpression {
  boolean interpret(Map<String, Double> context);
}

// Checks if stock price meets a condition
class StockPriceExpression implements StockExpression {
  private final String symbol;
  private final double targetPrice;

  public StockPriceExpression(String symbol, double targetPrice) {
    this.symbol = symbol;
    this.targetPrice = targetPrice;
  }

  @Override
  public boolean interpret(Map<String, Double> context) {
    Double currentPrice = context.get(symbol + "_PRICE");
    return currentPrice != null && currentPrice > targetPrice;
  }
}

// Checks if volume meets a condition
class VolumeExpression implements StockExpression {
  private final String symbol;
  private final double minVolume;

  public VolumeExpression(String symbol, double minVolume) {
    this.symbol = symbol;
    this.minVolume = minVolume;
  }

  @Override
  public boolean interpret(Map<String, Double> context) {
    Double currentVolume = context.get(symbol + "_VOLUME");
    return currentVolume != null && currentVolume > minVolume;
  }
}

// Logical AND
class StockAndExpression implements StockExpression {
  private final StockExpression expr1;
  private final StockExpression expr2;

  public StockAndExpression(StockExpression expr1, StockExpression expr2) {
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public boolean interpret(Map<String, Double> context) {
    return expr1.interpret(context) && expr2.interpret(context);
  }
}

// Logical OR (similar to AND)
class StockOrExpression implements StockExpression {
  private final StockExpression expr1;
  private final StockExpression expr2;

  public StockOrExpression(StockExpression expr1, StockExpression expr2) {
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public boolean interpret(Map<String, Double> context) {
    return expr1.interpret(context) || expr2.interpret(context);
  }
}

public class StockMarketClientDemo {
  public static void main(String[] args) {
    // Context: Current market data
    Map<String, Double> context = new HashMap<>();
    context.put("AAPL_PRICE", 155.0);    // AAPL at $155
    context.put("AAPL_VOLUME", 1_200_000.0); // Volume: 1.2M

    // Rule: "AAPL > $150 AND volume > 1M"
    StockExpression rule = new StockAndExpression(
        new StockPriceExpression("AAPL", 150),
        new VolumeExpression("AAPL", 1_000_000)
    );
    // Evaluate
    boolean shouldBuy = rule.interpret(context);
    System.out.println("Should buy AAPL? " + shouldBuy);

    // Rule: "AAPL > $150 OR volume > 1M"
    StockExpression rule2 = new StockOrExpression(
        new StockPriceExpression("AAPL", 150),
        new VolumeExpression("AAPL", 1_000_000)
    );
    // Evaluate
    boolean shouldBuy2 = rule2.interpret(context);
    System.out.println("Should buy AAPL? " + shouldBuy2);

  }
}
