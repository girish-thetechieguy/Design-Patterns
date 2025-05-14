package chainOfResponsibility;

interface TransactionHandler {
    void setNextHandler(TransactionHandler nextHandler);
    void handleRequest(String request);
}

class DepositHandler implements TransactionHandler {
    private TransactionHandler nextHandler;

    @Override
    public void setNextHandler(TransactionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("deposit")) {
            System.out.println("Handling deposit.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

class WithdrawalHandler implements TransactionHandler {
    private TransactionHandler nextHandler;

    @Override
    public void setNextHandler(TransactionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("withdrawal")) {
            System.out.println("Handling withdrawal.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

class LoanApprovalHandler implements TransactionHandler {
    private TransactionHandler nextHandler;

    @Override
    public void setNextHandler(TransactionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("loan")) {
            System.out.println("Handling loan approval.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

public class BankClientDemo {
    public static void main(String[] args) {
        TransactionHandler depositHandler = new DepositHandler();
        TransactionHandler withdrawalHandler = new WithdrawalHandler();
        TransactionHandler loanHandler = new LoanApprovalHandler();

        depositHandler.setNextHandler(withdrawalHandler);
        withdrawalHandler.setNextHandler(loanHandler);

        // Test the chain
        depositHandler.handleRequest("deposit");
        depositHandler.handleRequest("withdrawal");
        depositHandler.handleRequest("loan");
        depositHandler.handleRequest("transfer"); // No handler for this request
    }
}
