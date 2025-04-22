package factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface FinancialInstruments{
    void displayInfo();
}

class Stock implements FinancialInstruments{

    private static Logger logger = LoggerFactory.getLogger(Stock.class);

    String company;
    String symbol;
    double price;

    public Stock(String company, String symbol, double price) {
        this.company = company;
        this.symbol = symbol;
        this.price = price;
    }

    @Override
    public void displayInfo() {
        logger.info("Stock: {} ({}) - ${}", symbol, company, price);
    }
}

class Bond implements FinancialInstruments{

    private static Logger logger = LoggerFactory.getLogger(Bond.class);

    private String issuer;
    private double faceValue;
    private double couponRate;

    public Bond(String issuer, double faceValue, double couponRate) {
        this.issuer = issuer;
        this.faceValue = faceValue;
        this.couponRate = couponRate;
    }

    @Override
    public void displayInfo() {
        logger.info("Bond: {} - Face Value: ${}, Coupon Rate: {}%", issuer, faceValue, couponRate);
    }
}

// Factory interface
interface FinancialInstrumentFactory {
    FinancialInstruments createInstrument();
}

// Concrete factories
class StockFactory implements FinancialInstrumentFactory {
    private String symbol;
    private double price;
    private String company;

    public StockFactory(String symbol, double price, String company) {
        this.symbol = symbol;
        this.price = price;
        this.company = company;
    }

    @Override
    public FinancialInstruments createInstrument() {
        return new Stock(company, symbol, price);
    }
}

class BondFactory implements FinancialInstrumentFactory {
    private String issuer;
    private double faceValue;
    private double couponRate;

    public BondFactory(String issuer, double faceValue, double couponRate) {
        this.issuer = issuer;
        this.faceValue = faceValue;
        this.couponRate = couponRate;
    }

    @Override
    public FinancialInstruments createInstrument() {
        return new Bond(issuer, faceValue, couponRate);
    }
}

public class FactoryPatternInStockMarket {
    public static void main(String[] args) {
        FinancialInstrumentFactory stockFactory = new StockFactory("AAPL", 175.50, "Apple Inc.");
        FinancialInstruments stock = stockFactory.createInstrument();
        stock.displayInfo();

        FinancialInstrumentFactory bondFactory = new BondFactory("US Treasury", 1000, 3.5);
        FinancialInstruments bond = bondFactory.createInstrument();
        bond.displayInfo();
    }
}

//Output:
//        22:36:03.634 [main] INFO factory.Stock - Stock: AAPL (Apple Inc.) - $175.5
//        22:36:03.644 [main] INFO factory.Bond - Bond: US Treasury - Face Value: $1000.0, Coupon Rate: 3.5%
