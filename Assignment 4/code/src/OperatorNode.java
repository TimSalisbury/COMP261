import java.util.regex.Pattern;

public class OperatorNode implements RobotValueNode {

    enum Operator{
        ADD("add"),
        SUB("sub"),
        MUL("mul"),
        DIV("div");

        private String text;

        Operator(String text){
            this.text = text;
        }

        public String toString(){
            return this.text;
        }
    }

    public static final Pattern OPERATOR_PATTERN = Pattern.compile("add|sub|mul|div");

    private Operator operator;

    private ExpressionNode expressionOne;
    private ExpressionNode expressionTwo;

    public OperatorNode(Operator operator, ExpressionNode expressionOne, ExpressionNode expressionTwo) {
        this.operator = operator;
        this.expressionOne = expressionOne;
        this.expressionTwo = expressionTwo;
    }

    @Override
    public int evaluate(Robot robot) {
        switch (operator){
            case ADD:
                return expressionOne.evaluate(robot) + expressionTwo.evaluate(robot);
            case SUB:
                return expressionOne.evaluate(robot) - expressionTwo.evaluate(robot);
            case MUL:
                return expressionOne.evaluate(robot) * expressionTwo.evaluate(robot);
            case DIV:
                return expressionOne.evaluate(robot) / expressionTwo.evaluate(robot);
        }

        throw new RobotInterruptedException();
    }

    @Override
    public String toString() {
        return operator.toString() + "(" + expressionOne.toString() + "," + expressionTwo.toString() + ")";
    }
}
