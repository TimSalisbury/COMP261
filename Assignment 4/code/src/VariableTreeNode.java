import java.util.*;

public class VariableTreeNode implements Iterable<VariableTreeNode>{

	private Set<VariableTreeNode> children;

	private Map<VariableNode, Integer> variables;

	private VariableTreeNode parent;

	public VariableTreeNode(List<VariableNode> variables, VariableTreeNode parent){
		this.children = new HashSet<>();
		this.variables = new HashMap<>();
		this.parent = parent;

		for(VariableNode key : variables){
			this.variables.put(key, 0);
		}
	}
	public VariableTreeNode(List<VariableNode> variables){
		this.children = new HashSet<>();
		this.variables = new HashMap<>();

		for(VariableNode key : variables){
			this.variables.put(key, 0);
		}
	}

	public void setParent(VariableTreeNode parent){
		this.parent = parent;
	}

	public VariableTreeNode getParent(){
		return parent;
	}

	public void addChild(VariableTreeNode node){
		children.add(node);
	}

	public void addVariable(VariableNode key, int value){
		variables.put(key, value);
	}

	public int getVariable(VariableNode key){
		if(!variables.containsKey(key)){
			return parent.getVariable(key);
		}
		return variables.get(key);
	}

	public void setVariable(VariableNode key, int value){
		if(!variables.containsKey(key)){
			if(parent == null){
				throw new RobotInterruptedException();
			}
			parent.setVariable(key, value);
			return;
		}
		variables.put(key, value);
	}

	@Override
	public Iterator<VariableTreeNode> iterator(){
		return children.iterator();
	}
}
