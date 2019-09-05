import java.util.List;
import java.util.regex.Pattern;

public class DeclarationNode implements RobotProgramNode{

	public static Pattern DECLARATION_PATTERN = Pattern.compile("vars");

	private List<VariableNode> variables;

	private boolean root = false;

	public DeclarationNode(List<VariableNode> variables){
		this.variables = variables;
	}

	public void addVariable(VariableNode key){
		variables.add(key);
	}

	public void setRoot(boolean root){
		this.root = root;
	}

	@Override
	public void execute(Robot robot){
		if(root){
			robot.setRootVariable(new VariableTreeNode(variables, null));
		}else{
			robot.addScope(new VariableTreeNode(variables));
		}
	}

	public void closeScope(Robot robot){
		robot.closeScope();
	}


}
