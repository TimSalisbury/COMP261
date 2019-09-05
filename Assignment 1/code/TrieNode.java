import java.util.*;

public class TrieNode<E, I> implements Iterable<TrieNode<E, I>>{
	private List<E> values = null;
	private I index;
	private Map<I, TrieNode<E, I>> children;

	public TrieNode(I index){
		this.index = index;
	}

	public TrieNode(){

	}

	/**
	 * Adds a child to this TrieNode, with the value passed and key passed.
	 * @param value The value to add a child
	 * @param key   The key to map the value to
	 */
	public void addChild(TrieNode<E, I> value, I key){
		if(children == null) children = new HashMap<>();
		children.put(key, value);
	}

	/**
	 * Adds a value to this Node. Note: Nodes may have multiple children, there can be multiple roads called the same thing
	 * @param value The road value to assign.
	 */
	public void addValue(E value){
		if(values == null) values = new ArrayList<>();
		values.add(value);
	}

	/**
	 * @return Returns true if value is not null and the values of this node is greater than 0
	 */
	public boolean hasValues(){
		return values != null && values.size() > 0;
	}

	/**
	 * @return Returns true if children is null and children size is larger than 0
	 */
	public boolean hasChildren(){
		return children != null && children.size() > 0;
	}

	/**
	 * @return Values of this node
	 */
	public List<E> getValues(){
		return values;
	}

	/**
	 * Checks if this node contains a child mapped to the key provided
	 * @param key The key we're checking
	 * @return Whether or not this node contains that child
	 */
	public boolean containsChild(I key){
		if(children == null) return false;
		return children.containsKey(key);
	}

	/**
	 * Returns the child mapped to the key provided.
	 * @param key The key we're returning the child to
	 * @return  The actual child associated to the key
	 */
	public TrieNode<E, I> getChild(I key){
		return children.get(key);
	}

	@Override
	public Iterator iterator(){
		return children.values().iterator();
	}
}
