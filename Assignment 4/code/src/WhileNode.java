import java.util.Scanner;
import java.util.regex.Pattern;

public class WhileNode implements RobotProgramNode {

    static final Pattern WHILE_PATTERN = Pattern.compile("while");

    private ConditionNode condition;
    private BlockNode block;

    WhileNode(ConditionNode condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while(condition.evaluate(robot)){
            block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return "while(" + condition + ")" + block;
    }
}
