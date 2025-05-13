package bridge;

abstract class Order {
    protected TradingPlatform platform; // Bridge to implementation

    public Order(TradingPlatform platform) {
        this.platform = platform;
    }

    abstract void execute();
}

// Refined Abstractions
class MarketOrder extends Order {
    public MarketOrder(TradingPlatform platform) {
        super(platform);
    }

    @Override
    void execute() {
        platform.executeOrder("MARKET");
    }
}

class LimitOrder extends Order {
    private double price;

    public LimitOrder(TradingPlatform platform, double price) {
        super(platform);
        this.price = price;
    }

    @Override
    void execute() {
        platform.executeOrder("LIMIT@" + price);
    }
}

interface TradingPlatform {
    void executeOrder(String orderDetails);
}

// Concrete Implementations
class WebPlatform implements TradingPlatform {
    @Override
    public void executeOrder(String orderDetails) {
        System.out.println("[Web] Executing: " + orderDetails);
    }
}

class MobilePlatform implements TradingPlatform {
    @Override
    public void executeOrder(String orderDetails) {
        System.out.println("[Mobile] Executing: " + orderDetails);
    }
}

class APIPlatform implements TradingPlatform {
    @Override
    public void executeOrder(String orderDetails) {
        System.out.println("[API] Executing: " + orderDetails);
    }
}

public class StockMarketDemoClient {
    public static void main(String[] args) {
        // Platform implementations
        TradingPlatform web = new WebPlatform();
        TradingPlatform mobile = new MobilePlatform();

        // Orders with different platforms
        Order marketOrder = new MarketOrder(web);
        Order limitOrder = new LimitOrder(mobile, 150.50);

        marketOrder.execute(); // [Web] Executing: MARKET
        limitOrder.execute();  // [Mobile] Executing: LIMIT@150.5
    }
}
