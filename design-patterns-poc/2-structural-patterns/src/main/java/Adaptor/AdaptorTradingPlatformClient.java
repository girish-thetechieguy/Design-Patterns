package Adaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.stream.Collectors;

interface ModernTradingPlatform {
    StockQuote getQuote(String symbol);
    void placeOrder(Order order);
    List<Position> getPortfolio(String clientId);
    MarketDepth getMarketDepth(String symbol);
    void subscribeToUpdates(String symbol, MarketDataListener listener);
}

interface MarketDataListener {
    void onPriceUpdate(PriceUpdate update);
    void onOrderBookUpdate(OrderBookUpdate update);
}

// Data transfer objects
record StockQuote(String symbol, double bid, double ask, double lastPrice) {}
record Order(String orderId, String symbol, OrderType type, double price, int quantity) {}
enum OrderType { MARKET, LIMIT, STOP }
record Position(String symbol, int quantity, double avgPrice) {}
record MarketDepth(String symbol, List<PriceLevel> bids, List<PriceLevel> asks) {}
record PriceLevel(double price, int quantity) {}
record PriceUpdate(String symbol, double price, long timestamp) {}
record OrderBookUpdate(String symbol, List<PriceLevel> bids, List<PriceLevel> asks) {}
// Legacy data structures
record LegacyQuote(String symbol, double bid, double ask) {}
record LegacyPosition(String symbol, int quantity, double avgPrice) {}

class LegacyMarketDataProvider {
    public LegacyQuote getLegacyQuote(String legacySymbol) {
        // Simulate slow legacy system
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        return new LegacyQuote(legacySymbol,
                Math.random() * 100,
                Math.random() * 100 + 100);
    }

    public String placeLegacyOrder(String clientCode, String legacySymbol,
                                   char side, int shares, double price) {
        String orderId = "LEG-" + UUID.randomUUID().toString().substring(0, 8);
        System.out.printf("Legacy order %s: %s %d shares of %s at %.2f%n",
                orderId, side == 'B' ? "BUY" : "SELL", shares, legacySymbol, price);
        return orderId;
    }

    public LegacyPosition[] getClientPositions(String clientCode) {
        return new LegacyPosition[] {
                new LegacyPosition("AAPL", 100, 150.25),
                new LegacyPosition("MSFT", 50, 300.50)
        };
    }
}

class LegacyMarketAdapter implements ModernTradingPlatform {
    private final LegacyMarketDataProvider legacyProvider;
    private final Map<String, String> symbolMapping;

    public LegacyMarketAdapter(LegacyMarketDataProvider legacyProvider) {
        this.legacyProvider = legacyProvider;
        this.symbolMapping = Map.of(
                "AAPL", "AAPL.XNAS",
                "MSFT", "MSFT.XNAS",
                "GOOGL", "GOOG.XNAS"
        );
    }

    @Override
    public StockQuote getQuote(String symbol) {
        String legacySymbol = convertToLegacySymbol(symbol);
        LegacyQuote legacyQuote = legacyProvider.getLegacyQuote(legacySymbol);
        return new StockQuote(
                symbol,
                legacyQuote.bid(),
                legacyQuote.ask(),
                (legacyQuote.bid() + legacyQuote.ask()) / 2
        );
    }

    @Override
    public void placeOrder(Order order) {
        String legacySymbol = convertToLegacySymbol(order.symbol());
        char side = order.type() == OrderType.STOP ? 'B' : // Simplified
                order.type() == OrderType.LIMIT ? 'L' : 'M';
        legacyProvider.placeLegacyOrder(
                "CLIENT123", // Hardcoded for demo
                legacySymbol,
                side,
                order.quantity(),
                order.price()
        );
    }

    @Override
    public List<Position> getPortfolio(String clientId) {
        LegacyPosition[] legacyPositions = legacyProvider.getClientPositions(clientId);
        return Arrays.stream(legacyPositions)
                .map(lp -> new Position(
                        convertFromLegacySymbol(lp.symbol()),
                        lp.quantity(),
                        lp.avgPrice()))
                .toList();
    }

    @Override
    public MarketDepth getMarketDepth(String symbol) {
        // Legacy system doesn't support market depth
        // Simulate with empty book
        return new MarketDepth(symbol, List.of(), List.of());
    }

    @Override
    public void subscribeToUpdates(String symbol, MarketDataListener listener) {
        // Legacy system doesn't support push updates
        // Would need to implement polling in real system
        throw new UnsupportedOperationException("Legacy system doesn't support real-time updates");
    }

    private String convertToLegacySymbol(String modernSymbol) {
        return symbolMapping.entrySet().stream()
                .filter(e -> modernSymbol.startsWith(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(modernSymbol);
    }

    private String convertFromLegacySymbol(String legacySymbol) {
        return symbolMapping.entrySet().stream()
                .filter(e -> legacySymbol.equals(e.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(legacySymbol);
    }
}

class FixProtocolHandler {
    public FixMessage getMarketData(String fixSymbol) {
        // Simulate FIX protocol response
        return new FixMessage(
                "35=W|55=" + fixSymbol +
                        "|134=100|135=200|132=150.50|133=151.25"
        );
    }

    public FixExecutionReport sendOrder(FixMessage order) {
        String orderId = "FIX-" + UUID.randomUUID().toString().substring(0, 8);
        return new FixExecutionReport(
                "8=FIX.4.4|35=8|37=" + orderId + "|39=2|54=1|55=" +
                        order.getField("55") + "|44=" + order.getField("44") +
                        "|38=" + order.getField("38") + "|150=F|151=0"
        );
    }
}


// FIX protocol data structures
record FixMessage(String rawMessage) {
    public String getField(String tag) {
        return Arrays.stream(rawMessage.split("\\|"))
                .filter(f -> f.startsWith(tag + "="))
                .findFirst()
                .map(f -> f.substring(tag.length() + 1))
                .orElse("");
    }
}

record FixExecutionReport(String rawMessage) {
    public boolean isSuccess() {
        return rawMessage.contains("39=2"); // 2 = Filled
    }
}

class FixProtocolAdapter implements ModernTradingPlatform {
    private final FixProtocolHandler fixHandler;

    public FixProtocolAdapter(FixProtocolHandler fixHandler) {
        this.fixHandler = fixHandler;
    }

    @Override
    public StockQuote getQuote(String symbol) {
        FixMessage fixData = fixHandler.getMarketData(convertToFixSymbol(symbol));
        return new StockQuote(
                symbol,
                Double.parseDouble(fixData.getField("132")), // bid
                Double.parseDouble(fixData.getField("133")), // ask
                (Double.parseDouble(fixData.getField("132")) +
                        Double.parseDouble(fixData.getField("133"))) / 2
        );
    }

    @Override
    public void placeOrder(Order order) {
        String fixMessage = "35=D|55=" + convertToFixSymbol(order.symbol()) +
                "|54=" + (order.type() == OrderType.STOP ? "2" : "1") +
                "|44=" + order.price() +
                "|38=" + order.quantity() +
                "|40=" + (order.type() == OrderType.MARKET ? "1" : "2");

        FixExecutionReport report = fixHandler.sendOrder(new FixMessage(fixMessage));
        if (!report.isSuccess()) {
            throw new RuntimeException("FIX order rejected");
        }
    }

    @Override
    public List<Position> getPortfolio(String clientId) {
        // FIX doesn't provide portfolio data directly
        // Would need to implement position reports in real system
        return List.of();
    }

    @Override
    public MarketDepth getMarketDepth(String symbol) {
        FixMessage fixData = fixHandler.getMarketData(convertToFixSymbol(symbol));
        // Simplified - real FIX would have more levels
        return new MarketDepth(
                symbol,
                List.of(new PriceLevel(
                        Double.parseDouble(fixData.getField("132")),
                        Integer.parseInt(fixData.getField("134"))
                )),
                List.of(new PriceLevel(
                        Double.parseDouble(fixData.getField("133")),
                        Integer.parseInt(fixData.getField("135"))
                ))
        );
    }

    @Override
    public void subscribeToUpdates(String symbol, MarketDataListener listener) {
        // FIX supports this via Market Data Request (35=V)
        // Implementation would require FIX session management
        throw new UnsupportedOperationException("FIX subscription not implemented");
    }

    private String convertToFixSymbol(String modernSymbol) {
        return modernSymbol.replace(".", "/");
    }
}

// Mock modern implementation for comparison
class ModernTradingPlatformImpl implements ModernTradingPlatform {
    private final Map<String, List<MarketDataListener>> subscribers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService marketDataScheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, StockQuote> lastQuotes = new HashMap<>();
    private final Map<String, MarketDepth> lastMarketDepths = new HashMap<>();

    // Mock data for demonstration
    private final Map<String, List<Position>> clientPortfolios = Map.of(
            "CLIENT123", List.of(
                    new Position("AAPL", 150, 145.30),
                    new Position("MSFT", 75, 305.20),
                    new Position("GOOGL", 50, 2750.80)
            )
    );

    public ModernTradingPlatformImpl() {
        // Initialize with some mock data
        lastQuotes.put("AAPL", new StockQuote("AAPL", 148.50, 148.60, 148.55));
        lastQuotes.put("MSFT", new StockQuote("MSFT", 307.25, 307.35, 307.30));
        lastQuotes.put("GOOGL", new StockQuote("GOOGL", 2760.40, 2760.60, 2760.50));

        lastMarketDepths.put("AAPL", new MarketDepth("AAPL",
                List.of(
                        new PriceLevel(148.45, 500),
                        new PriceLevel(148.40, 800),
                        new PriceLevel(148.35, 1200)
                ),
                List.of(
                        new PriceLevel(148.65, 600),
                        new PriceLevel(148.70, 900),
                        new PriceLevel(148.75, 1100)
                )
        ));

        // Simulate market data updates
        marketDataScheduler.scheduleAtFixedRate(this::simulateMarketUpdates, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public StockQuote getQuote(String symbol) {
        // In a real implementation, this would call a REST API
        StockQuote quote = lastQuotes.get(symbol);
        if (quote == null) {
            throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
        return quote;
    }

    @Override
    public void placeOrder(Order order) {
        // In a real implementation, this would call a REST API
        System.out.printf("Modern platform: Placing order %s - %s %d shares of %s at %.2f%n",
                order.orderId(),
                order.type(),
                order.quantity(),
                order.symbol(),
                order.price());

        // Simulate execution
        System.out.printf("Order %s executed at %.2f%n", order.orderId(),
                getQuote(order.symbol()).lastPrice());
    }

    @Override
    public List<Position> getPortfolio(String clientId) {
        // In a real implementation, this would call a REST API
        List<Position> portfolio = clientPortfolios.get(clientId);
        if (portfolio == null) {
            throw new IllegalArgumentException("Unknown client: " + clientId);
        }
        return portfolio;
    }

    @Override
    public MarketDepth getMarketDepth(String symbol) {
        // In a real implementation, this would call a REST API
        MarketDepth depth = lastMarketDepths.get(symbol);
        if (depth == null) {
            throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
        return depth;
    }

    @Override
    public void subscribeToUpdates(String symbol, MarketDataListener listener) {
        subscribers.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    private void simulateMarketUpdates() {
        lastQuotes.forEach((symbol, quote) -> {
            // Simulate small price changes
            double change = (Math.random() - 0.5) * 0.5;
            StockQuote newQuote = new StockQuote(
                    symbol,
                    quote.bid() + change,
                    quote.ask() + change,
                    quote.lastPrice() + change
            );
            lastQuotes.put(symbol, newQuote);

            // Notify subscribers
            List<MarketDataListener> symbolSubscribers = subscribers.get(symbol);
            if (symbolSubscribers != null) {
                PriceUpdate update = new PriceUpdate(
                        symbol,
                        newQuote.lastPrice(),
                        System.currentTimeMillis()
                );
                symbolSubscribers.forEach(l -> l.onPriceUpdate(update));
            }
        });

        // Simulate occasional order book updates
        if (Math.random() > 0.7) {
            lastMarketDepths.forEach((symbol, depth) -> {
                MarketDepth newDepth = simulateOrderBookChange(depth);
                lastMarketDepths.put(symbol, newDepth);

                List<MarketDataListener> symbolSubscribers = subscribers.get(symbol);
                if (symbolSubscribers != null) {
                    OrderBookUpdate update = new OrderBookUpdate(
                            symbol,
                            newDepth.bids(),
                            newDepth.asks()
                    );
                    symbolSubscribers.forEach(l -> l.onOrderBookUpdate(update));
                }
            });
        }
    }

    private MarketDepth simulateOrderBookChange(MarketDepth depth) {
        // Simulate small changes to the order book
        List<PriceLevel> newBids = depth.bids().stream()
                .map(level -> new PriceLevel(
                        level.price() + (Math.random() - 0.5) * 0.05,
                        (int)(level.quantity() * (0.9 + Math.random() * 0.2))
                ))
                .toList();

        List<PriceLevel> newAsks = depth.asks().stream()
                .map(level -> new PriceLevel(
                        level.price() + (Math.random() - 0.5) * 0.05,
                        (int)(level.quantity() * (0.9 + Math.random() * 0.2))
                ))
                .toList();
        return new MarketDepth(depth.symbol(), newBids, newAsks);
    }

    @Override
    protected void finalize() throws Throwable {
        marketDataScheduler.shutdown();
        super.finalize();
    }
}

public class AdaptorTradingPlatformClient {
    public static void main(String[] args) throws InterruptedException {
        ModernTradingPlatform platform = new ModernTradingPlatformImpl();

        // Subscribe to AAPL updates
        platform.subscribeToUpdates("AAPL", new MarketDataListener() {
            @Override
            public void onPriceUpdate(PriceUpdate update) {
                System.out.printf("AAPL Price Update: %.2f at %d%n",
                        update.price(), update.timestamp());
            }

            @Override
            public void onOrderBookUpdate(OrderBookUpdate update) {
                System.out.println("AAPL Order Book Update:");
                System.out.println("  Bids: " + update.bids());
                System.out.println("  Asks: " + update.asks());
            }
        });

        // Get initial quote
        StockQuote aaplQuote = platform.getQuote("AAPL");
        System.out.println("Initial AAPL Quote: " + aaplQuote);

        // Place an order
        platform.placeOrder(new Order(
                "ORD-001", "AAPL", OrderType.LIMIT, aaplQuote.bid(), 100
        ));

        // Get portfolio
        System.out.println("Portfolio:");
        platform.getPortfolio("CLIENT123").forEach(System.out::println);

        // Keep running to receive updates
        Thread.sleep(5000);
    }
}
