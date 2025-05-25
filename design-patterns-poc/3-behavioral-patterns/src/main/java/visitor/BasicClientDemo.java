package visitor;

interface ComputerPart {
	void accept(ComputerPartVisitor computerPartVisitor);
}

interface ComputerPartVisitor {
	void visit(Computer computer);

	void visit(Mouse mouse);

	void visit(Keyboard keyboard);

	void visit(Monitor monitor);
}

class Keyboard implements ComputerPart {

	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		computerPartVisitor.visit(this);
	}
}


class Monitor implements ComputerPart {

	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		computerPartVisitor.visit(this);
	}
}


class Mouse implements ComputerPart {

	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		computerPartVisitor.visit(this);
	}
}

class Computer implements ComputerPart {

	ComputerPart[] parts;

	public Computer() {
		parts = new ComputerPart[]{new Mouse(), new Keyboard(), new Monitor()};
	}


	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		for (ComputerPart part : parts) {
			part.accept(computerPartVisitor);
		}
		computerPartVisitor.visit(this);
	}
}

class ComputerPartDisplayVisitor implements ComputerPartVisitor {

	@Override
	public void visit(Computer computer) {
		System.out.println("Displaying Computer.");
	}

	@Override
	public void visit(Mouse mouse) {
		System.out.println("Displaying Mouse.");
	}

	@Override
	public void visit(Keyboard keyboard) {
		System.out.println("Displaying Keyboard.");
	}

	@Override
	public void visit(Monitor monitor) {
		System.out.println("Displaying Monitor.");
	}
}


public class BasicClientDemo {
	public static void main(String[] args) {

		ComputerPart computer = new Computer();
		computer.accept(new ComputerPartDisplayVisitor());
	}
}
