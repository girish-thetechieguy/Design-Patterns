package builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder pattern builds a complex object using simple objects and using a step by step approach.
 *
 */
interface Item {
    public String name();
    public Packing packing();
    public float price();
}

interface Packing {
    public String pack();
}

class Wrapper implements Packing {
    @Override
    public String pack() {
        return "Wrapper";
    }
}

class Bottle implements Packing {
    @Override
    public String pack() {
        return "Bottle";
    }
}

abstract class Burger implements Item {
    @Override
    public Packing packing() {
        return new Wrapper();
    }
    @Override
    public abstract float price();
}

abstract class ColdDrink implements Item {
    @Override
    public Packing packing() {
        return new Bottle();
    }
    @Override
    public abstract float price();
}

class VegBurger extends Burger {
    @Override
    public float price() {
        return 25.0f;
    }
    @Override
    public String name() {
        return "Veg Burger";
    }
}

class ChickenBurger extends Burger {
    @Override
    public float price() {
        return 50.5f;
    }
    @Override
    public String name() {
        return "Chicken Burger";
    }
}

class Coke extends ColdDrink {
    @Override
    public float price() {
        return 30.0f;
    }
    @Override
    public String name() {
        return "Coke";
    }
}

class Pepsi extends ColdDrink {
    @Override
    public float price() {
        return 35.0f;
    }
    @Override
    public String name() {
        return "Pepsi";
    }
}

class Meal {
    private static Logger logger = LoggerFactory.getLogger(Meal.class);
    private List<Item> items = new ArrayList<Item>();
    public void addItem(Item item) {
        items.add(item);
    }
    public float getCost() {
        float cost = 0.0f;
        for (Item item : items) {
            cost += item.price();
        }
        return cost;
    }

    public void showItems() {
        for (Item item : items) {
            logger.info("Item : {}", item.name());
            logger.info(", Packing : {}", item.packing().pack());
            logger.info(", Price : {}", item.price());
        }
    }
}

class MealBuilder {
    public Meal prepareVegMeal() {
        Meal meal = new Meal();
        meal.addItem(new VegBurger());
        meal.addItem(new Coke());
        return meal;
    }

    public Meal prepareNonVegMeal() {
        Meal meal = new Meal();
        meal.addItem(new ChickenBurger());
        meal.addItem(new Pepsi());
        return meal;
    }
}


public class BasicResturentPackagingDemo {
    private static Logger logger = LoggerFactory.getLogger(BasicResturentPackagingDemo.class);
    public static void main(String[] args) {
        logger.info("======Menu========");
        MealBuilder mealBuilder = new MealBuilder();

        Meal vegMeal = mealBuilder.prepareVegMeal();
        logger.info("Veg Meal");
        vegMeal.showItems();
        logger.info("Total Cost: {}", vegMeal.getCost());

        Meal nonVegMeal = mealBuilder.prepareNonVegMeal();
        logger.info("Non-Veg Meal");
        nonVegMeal.showItems();
        logger.info("Total Cost: {}", nonVegMeal.getCost());
        logger.info("======END========");
    }
}
