import java.util.*;

/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */
public class HuffmanCoding {

	private Map<Character, String> mappings;
	private TreeNode rootNode;

	/**
	 * This would be a good place to compute and store the tree.
	 */
	public HuffmanCoding(String text) {
		mappings = new HashMap<>();
		Queue<TreeNode> queue = generateQueue(text);
		while(queue.size() != 1){
			TreeNode first = queue.poll();
			TreeNode second = queue.poll();
			queue.add(first.combine(second));   //Combine the two smallest frequency nodes together until only one node is left
		}

		TreeNode root = queue.poll();

		rootNode = root;

		generateCodes(root, "");
		// TODO fill this in.
	}


	private void generateCodes(TreeNode node, String builder){
		if(node.isLeaf()){  //If we're a leaf node then assign it a code
			String item = node.getItem();
			mappings.put(item.charAt(0), builder);
		}else{
			generateCodes(node.getRight(), builder + '1');
			generateCodes(node.getLeft(), builder + '0');
		}
	}


	private Queue<TreeNode> generateQueue(String text){
		Map<Character, Integer> frequencies = new HashMap<>();
		char[] tArray = text.toCharArray();
		for (char c : tArray) {
			if (!frequencies.containsKey(c)) {
				frequencies.put(c, 1);
			} else {
				frequencies.put(c, frequencies.get(c) + 1);
			}
		}

		PriorityQueue<TreeNode> queue = new PriorityQueue<>();
		for(Character character : frequencies.keySet()){
			queue.offer(new TreeNode(Character.toString(character), frequencies.get(character)));
		}
		return queue;
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		char[] tArray = text.toCharArray();
		StringBuilder builder = new StringBuilder();
		for(char c : tArray){
			builder.append(mappings.get(c));
		}
		return builder.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		StringBuilder builder = new StringBuilder();
		int i = 0;

		int length = encoded.length();

		while(i != length){
			TreeNode node = rootNode;

			int index = 0;

			while(!node.isLeaf()){
				if(encoded.charAt(i + index) == '1'){
					node = node.getRight();
				}else{
					node = node.getLeft();
				}
				index++;
			}

			i += index;

			builder.append(node.getItem());
		}
		return builder.toString();
	}


	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}



	private static class TreeNode implements Comparable<TreeNode>{
		private String item;
		private int count;
		private String code;

		private TreeNode left;
		private TreeNode right;

		public TreeNode(String item, int count){
			this.item = item;
			this.count = count;
		}

		public TreeNode getLeft() {
			return left;
		}

		public void setLeft(TreeNode left) {
			this.left = left;
		}

		public TreeNode getRight() {
			return right;
		}

		public void setRight(TreeNode right) {
			this.right = right;
		}

		public String getItem(){
			return item;
		}

		public TreeNode combine(TreeNode other){
			TreeNode node = new TreeNode(this.item + other.item, this.count + other.count);

			node.setLeft(this.compareTo(other) == -1 ? other : this);
			node.setRight(this.compareTo(other) == -1 ? this : other);
			return node;
		}

		public boolean isLeaf(){
			return (left == null || right == null) && item.length() == 1;
		}

		@Override
		public int compareTo(TreeNode o){
			if(o.count == count) return 0;
			return o.count > count ? -1 : 1;
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			TreeNode queueItem = (TreeNode) o;
			return Objects.equals(item, queueItem.item);
		}

		@Override
		public int hashCode(){
			return Objects.hash(item);
		}

		@Override
		public String toString(){
			return "TreeNode{" +
					"item=" + item +
					", count=" + count +
					'}';
		}
	}
}
