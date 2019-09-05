public class ExpressionNode implements RobotValueNode {

    private RobotValueNode valueNode;

    public ExpressionNode(RobotValueNode valueNode) {
        this.valueNode = valueNode;
    }

    @Override
    public int evaluate(Robot robot) {
        return valueNode.evaluate(robot);
    }

    @Override
    public String toString() {
        return valueNode.toString();
    }
}
