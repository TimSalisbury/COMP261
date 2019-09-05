import java.util.Scanner;
import java.util.regex.Pattern;

public class LoopNode implements RobotProgramNode {

    static Pattern LOOP_PATTERN = Pattern.compile("loop");

    private RobotProgramNode block;

    LoopNode(RobotProgramNode block) {
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while(true){
            block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return "loop" + this.block.toString();
    }
}
