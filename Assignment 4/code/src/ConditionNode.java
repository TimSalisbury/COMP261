import java.util.regex.Pattern;

public class ConditionNode implements RobotConditionNode {

    enum Comparison {
        LESS_THAN("lt"),
        GREATER_THAN("gt"),
        EQUAL_TO("eq"),
        AND("and"),
        OR("or"),
        NOT("not");

        private String text;

        Comparison(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

    static final Pattern CONDITION_PATTERN = Pattern.compile("lt|gt|eq|and|or|not");

    private Comparison comparison;
    private RobotValueNode sensor;
    private RobotValueNode number;

    private RobotConditionNode conditionOne;
    private RobotConditionNode conditionTwo;

    ConditionNode(Comparison comparison, RobotConditionNode conditionOne, RobotConditionNode conditionTwo) {
        this.comparison = comparison;
        this.conditionOne = conditionOne;
        this.conditionTwo = conditionTwo;
    }

    ConditionNode(Comparison comparison, RobotValueNode sensor, RobotValueNode number) {
        this.comparison = comparison;
        this.sensor = sensor;
        this.number = number;
    }

    @Override
    public boolean evaluate(Robot robot) {
        switch (comparison) {
            case EQUAL_TO:
                return sensor.evaluate(robot) == number.evaluate(robot);
            case LESS_THAN:
                return sensor.evaluate(robot) < number.evaluate(robot);
            case GREATER_THAN:
                return sensor.evaluate(robot) > number.evaluate(robot);
            case AND:
                return conditionOne.evaluate(robot) && conditionTwo.evaluate(robot);
            case OR:
                return conditionOne.evaluate(robot) || conditionOne.evaluate(robot);
            case NOT:
                return !conditionOne.evaluate(robot);
        }

        throw new RobotInterruptedException();
    }

    @Override
    public String toString() {
        if(comparison == ConditionNode.Comparison.LESS_THAN || comparison == ConditionNode.Comparison.GREATER_THAN ||
                comparison == ConditionNode.Comparison.EQUAL_TO) {
            return comparison.toString() + "(" + sensor + "," + number.toString() + ")";
        }else{
            if(comparison == Comparison.NOT){
                return comparison.toString() + "(" + conditionOne.toString() + ")";
            }

            return comparison.toString() + "(" + conditionOne.toString() + "," + conditionTwo.toString();
        }
    }
}