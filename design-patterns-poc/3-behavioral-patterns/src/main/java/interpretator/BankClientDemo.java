package interpretator;

import java.util.HashMap;
import java.util.Map;

interface BankRuleExpression {
  boolean interpret(Map<String, Integer> context);
}

// Checks if income exceeds threshold
class IncomeExpression implements BankRuleExpression {
  private final int minIncome;

  public IncomeExpression(int minIncome) {
    this.minIncome = minIncome;
  }

  @Override
  public boolean interpret(Map<String, Integer> context) {
    return context.getOrDefault("INCOME", 0) > minIncome;
  }
}

// Checks credit score
class CreditScoreExpression implements BankRuleExpression {
  private final int minScore;

  public CreditScoreExpression(int minScore) {
    this.minScore = minScore;
  }

  @Override
  public boolean interpret(Map<String, Integer> context) {
    return context.getOrDefault("CREDIT_SCORE", 0) > minScore;
  }
}

// Checks employment duration (years)
class EmploymentExpression implements BankRuleExpression {
  private final int minYears;

  public EmploymentExpression(int minYears) {
    this.minYears = minYears;
  }

  @Override
  public boolean interpret(Map<String, Integer> context) {
    return context.getOrDefault("EMPLOYMENT", 0) > minYears;
  }
}

// AND operator
class BankAndExpression implements BankRuleExpression {
  private final BankRuleExpression left;
  private final BankRuleExpression right;

  public BankAndExpression(BankRuleExpression left, BankRuleExpression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean interpret(Map<String, Integer> context) {
    return left.interpret(context) && right.interpret(context);
  }
}

// OR operator (similar structure)
class BankOrExpression implements BankRuleExpression {
  private final BankRuleExpression expr1;
  private final BankRuleExpression expr2;

  public BankOrExpression(BankRuleExpression expr1, BankRuleExpression expr2) {
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public boolean interpret(Map<String, Integer> context) {
    return expr1.interpret(context) || expr2.interpret(context);
  }
}


public class BankClientDemo {
  public static void main(String[] args) {
    // Applicant data
    Map<String, Integer> applicant = new HashMap<>();
    applicant.put("INCOME", 65000);
    applicant.put("CREDIT_SCORE", 720);
    applicant.put("EMPLOYMENT", 3);

    // Rule: INCOME > 50000 AND CREDIT_SCORE > 700 AND EMPLOYMENT > 2
    BankRuleExpression rule = new BankAndExpression(
        new IncomeExpression(50000),
        new BankAndExpression(
            new CreditScoreExpression(700),
            new EmploymentExpression(2)
        )
    );

    // Evaluate
    boolean isApproved = rule.interpret(applicant);
    System.out.println("AND Loan approved? " + isApproved); // true

    // Rule: INCOME > 50000 AND CREDIT_SCORE > 700 OR EMPLOYMENT > 2
    BankRuleExpression rule2 = new BankOrExpression(
        new IncomeExpression(50000),
        new BankOrExpression(
            new CreditScoreExpression(700),
            new EmploymentExpression(2)
        )
    );

    // Evaluate
    boolean isApproved2 = rule2.interpret(applicant);
    System.out.println("OR Loan approved? " + isApproved2); // false
  }
}
