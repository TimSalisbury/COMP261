public class StatementNode implements RobotProgramNode {

    RobotProgramNode statement;

    StatementNode(RobotProgramNode statement) {
        this.statement = statement;
    }

    @Override
    public void execute(Robot robot) {
        statement.execute(robot);
    }

    @Override
    public String toString() {
        return statement.toString();
    }
}
