import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProgramNode implements RobotProgramNode {

    private List<RobotProgramNode> statements;

    private DeclarationNode declarationNode;

    public ProgramNode(List<RobotProgramNode> statements, DeclarationNode declarationNode) {
        this.statements = statements;
        this.declarationNode = declarationNode;
    }

    @Override
    public void execute(Robot robot) {
        declarationNode.execute(robot);

        for(RobotProgramNode node : statements){
            node.execute(robot);
        }
    }

    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();

        for(RobotProgramNode statement : statements){
            string.append(statement);
        }

        return string.toString();
    }
}
