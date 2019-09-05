import java.util.Scanner;
import java.util.regex.Pattern;

public class ActNode implements RobotProgramNode {

    static final Pattern ACT_PATTERN = Pattern.compile("move|turnL|turnR|takeFuel|wait|turnAround|shieldOn|shieldOff");

    enum Action {
        MOVE("move"),
        TURNLEFT("turnL"),
        TURNRIGHT("turnR"),
        TAKEFUEL("takeFuel"),
        WAIT("wait"),
        TURNAROUND("turnAround"),
        SHIELDON("shieldOn"),
        SHIELDOFF("shieldOff");

        private String text;

        Action(String text){ this.text = text; }

        public String toString(){
            return this.text;
        }
    }

    private Action action;

    private ExpressionNode expression;

    ActNode(Action action, ExpressionNode expression) {
        this.action = action;
        this.expression = expression;
    }

    ActNode(Action action) {
        this.action = action;
    }

    @Override
    public void execute(Robot robot) {
        switch (action){
            case MOVE:
                if(expression != null){
                    for(int i = 0; i < expression.evaluate(robot); i++){
                        robot.move();
                    }
                }else{
                    robot.move();
                }
                break;
            case TURNLEFT:
                robot.turnLeft();
                break;
            case TURNRIGHT:
                robot.turnRight();
                break;
            case TAKEFUEL:
                robot.takeFuel();
                break;
            case WAIT:
                if(expression != null){
                    for(int i = 0; i < expression.evaluate(robot); i++){
                        robot.idleWait();
                    }
                }else{
                    robot.idleWait();
                }
                break;
            case TURNAROUND:
                robot.turnAround();
                break;
            case SHIELDON:
                robot.setShield(true);
                break;
            case SHIELDOFF:
                robot.setShield(false);
                break;
        }
    }


    @Override
    public String toString() {
        String string =  action.toString();
        if(expression != null){
            string += "(" + expression.toString() + ")";
        }
        return string + ";";
    }
}
