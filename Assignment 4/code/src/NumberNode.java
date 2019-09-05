import java.util.regex.Pattern;

public class NumberNode implements RobotValueNode {

    private int value;

    static final Pattern NUMBER_PATTERN = Pattern.compile("-?[1-9][0-9]*|0");

    public NumberNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate(Robot robot) {
        return value;
    }


    @Override
    public String toString(){
        return "" + value;
    }
}
