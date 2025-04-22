package factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface Shape{
    void draw();
}

class Rectangle implements Shape{
    private static Logger logger = LoggerFactory.getLogger(Rectangle.class);
    @Override
    public void draw() {
        logger.info("Rectangle Draw () is called!!");
    }
}

class Square implements Shape{
    private static Logger logger = LoggerFactory.getLogger(Square.class);
    @Override
    public void draw() {
        logger.info("Square Draw() is called!!");
    }
}

class Circle implements Shape{
    private static Logger logger = LoggerFactory.getLogger(Circle.class);
    @Override
    public void draw() {
        logger.info("Circle Draw() is called!!");
    }
}


class ShapeFactory {
    public Shape getShape(String shape){
        if(shape ==  null){
            return null;
        } else if (shape.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shape.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        } else if (shape.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        return null;
    }
}

public class BasicFactoryPattern{
    public static void main(String[] args) {
        ShapeFactory shapeFactory = new ShapeFactory();

        //get an object of Circle and call its draw method.
        Shape shape1 = shapeFactory.getShape("CIRCLE");
        shape1.draw();

        //get an object of Rectangle and call its draw method.
        Shape shape2 = shapeFactory.getShape("RECTANGLE");
        shape2.draw();

        //get an object of Square and call its draw method.
        Shape shape3 = shapeFactory.getShape("SQUARE");
        shape3.draw();
    }
}

// Output:
// 00:11:06.537 [main] INFO factory.Rectangle - Rectangle Draw () is called!!
// 00:11:06.532 [main] INFO factory.Circle - Circle Draw() is called!!
// 00:11:06.538 [main] INFO factory.Square - Square Draw() is called!!