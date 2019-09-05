public class AssignNode implements RobotProgramNode {

    private VariableNode variable;
    private ExpressionNode expression;

    public AssignNode(VariableNode variable, ExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void execute(Robot robot) {
        robot.setVariable(variable, expression.evaluate(robot));
    }

    @Override
    public String toString(){
        return variable.toString() + " = " + expression.toString() + ";";
    }
}
