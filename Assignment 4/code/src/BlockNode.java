import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockNode implements RobotProgramNode {

    private List<StatementNode> statements;

    private DeclarationNode declarationNode;

    BlockNode(List<StatementNode> statements, DeclarationNode declarationNode) {
        this.statements = statements;
        this.declarationNode = declarationNode;
    }

    @Override
    public void execute(Robot robot) {
        declarationNode.execute(robot);

        for(RobotProgramNode statement : statements){
            statement.execute(robot);
        }

        declarationNode.closeScope(robot);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("{");

        for(RobotProgramNode statement : statements){
            string.append(statement.toString());
        }

        string.append("}");
        return string.toString();
    }
}
