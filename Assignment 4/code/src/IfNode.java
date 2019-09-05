import java.util.List;
import java.util.regex.Pattern;

public class IfNode implements RobotProgramNode {

    static final Pattern IF_PATTERN = Pattern.compile("if");
    static final Pattern ELSE_PATTERN = Pattern.compile("else");

    private BlockNode ifBlock;
    private List<ElseIfNode> elseIfNode;
    private BlockNode elseBlock;
    private ConditionNode condition;

    public IfNode(BlockNode ifBlock, List<ElseIfNode> elseIfNode, BlockNode elseBlock, ConditionNode condition) {
        this.ifBlock = ifBlock;
        this.elseIfNode = elseIfNode;
        this.elseBlock = elseBlock;
        this.condition = condition;
    }

    IfNode(BlockNode ifBlock, BlockNode elseBlock, ConditionNode condition) {
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
        this.condition = condition;
    }

    IfNode(BlockNode block, ConditionNode condition) {
        this.ifBlock = block;
        this.condition = condition;
    }


    @Override
    public void execute(Robot robot) {
        if(condition.evaluate(robot)){
            ifBlock.execute(robot);
        }else if(!executeElseIfs(robot) && elseBlock != null){
            elseBlock.execute(robot);
        }
    }

    private boolean executeElseIfs(Robot robot){
        if(elseIfNode == null) return false;
        for(ElseIfNode elseIf : elseIfNode){
            if(elseIf.evaluate(robot)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "if(" + condition + ")" + ifBlock;

        if(elseIfNode != null){
            for(ElseIfNode elseIf : elseIfNode){
                string += elseIf.toString();
            }
        }

        if(elseBlock != null){
            string +=  "else" + elseBlock;
        }
        return string;
    }


    public static class ElseIfNode implements RobotConditionNode{

        static final Pattern ELSEIF_PATTERN = Pattern.compile("elif");

        private ConditionNode condition;
        private BlockNode block;

        public ElseIfNode(ConditionNode condition, BlockNode block) {
            this.condition = condition;
            this.block = block;
        }

        @Override
        public boolean evaluate(Robot robot) {
            if(condition.evaluate(robot)){
                block.execute(robot);
                return true;
            }
            return false;
        }

        @Override
        public String toString(){
            return "elif(" + condition + ")" + block.toString();
        }
    }
}
