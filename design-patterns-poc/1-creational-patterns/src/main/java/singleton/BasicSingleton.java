package singleton;

import factory.FactroryPatternInBank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This pattern involves a single class which is responsible to create an object while
 * making sure that only single object gets created. This class provides a way to access
 * its only object which can be accessed directly
 * without need to instantiate the object of the class.
 */
class SingleObject {
    private static Logger logger = LoggerFactory.getLogger(FactroryPatternInBank.class);
    //create an object of SingleObject
    private static SingleObject instance = new SingleObject();

    //make the constructor private so that this class cannot be
    //instantiated
    private SingleObject() {
    }

    //Get the only object available
    public static SingleObject getInstance() {
        return instance;
    }

    public void showMessage() {
        logger.info("Singleton method ShowMessege called!!");
    }
}

public class BasicSingleton {
    public static void main(String[] args) {
        //Get the only object available
        SingleObject object = SingleObject.getInstance();

        //show the message
        object.showMessage();
    }
}
