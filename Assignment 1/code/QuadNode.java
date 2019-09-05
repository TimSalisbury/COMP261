import java.awt.*;

public class QuadNode{
	public QuadNode[] children = new QuadNode[4];
	private Node value;
	private Location location;
	private double width;
	private double height;
	public QuadNode parent;

	public QuadNode(Location location, double width, double height, QuadNode parent){
		this.location = location;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}


	public void redraw(Graphics g, Location origin, double scale){
		Point p = new Location(location.x, location.y + height).asPoint(origin, scale);
		g.setColor(Color.RED);
		g.drawRect(p.x, p.y, (int)(width * scale), (int)(height * scale));
		for(QuadNode child : children){
			if(child == null) continue;
			child.redraw(g, origin, scale);
		}
	}

	/**
	 * @return The value of this QuadNode
	 */
	public Node getValue(){
		return value;
	}

	/**
	 * Descends down the tree as far as it can following the x, y positions. If x, y happens to fall into a quadrant
	 * with no value associated with it then we select a random node nearby. First the function checks if the current
	 * QuadNode has a value, if it does then we return this, as we have hit the bottom of the tree. If that is not true,
	 * then we either return the child node associated with x,y if it has a value or, descend down.
	 * @param x The x position that we plan on finding a value associated to
	 * @param y The y position that we plan on finding a value associated to
	 * @return A QuadNode at the bottom of the tree that contains a value or null if there is none
	 */
	public QuadNode descend(double x, double y){
		if(this.hasValue()) return this;
		QuadNode nextLevel = getChild(x, y);
		if(nextLevel.hasValue()) return nextLevel;
		else if(nextLevel.hasChildren()) return nextLevel.descend(x, y);

		for(QuadNode child : children){
			if(child.hasValue()) return child;
			else if(child.hasChildren()) return child.descend();
		}
		return null;
	}

	/**
	 * Descends down the tree by descending down the first child we find that has a value.
	 * @return  The QuadNode at the bottom of the tree
	 */
	public QuadNode descend(){
		if(hasValue()) return this;
		for(QuadNode child : children){
			if(child.hasChildren() || child.hasValue()) return child.descend();
		}
		return null;
	}


	/**
	 * Subdivides this QuadNode by creating four children, each 1/2 of this nodes width and height in a 2x2 matrix.
	 * These subdivided nodes are the children of this node.
	 */
	private void subdivide(){
		for(int i = 0; i < 4; i++){
			children[i] = createNodeFromQuadrant(i);
		}
	}

	/**
	 * @return If this QuadNodes value is null or not - if it's null then we know for sure that there is no value associated
	 * with this QuadNode
	 */
	public boolean hasValue(){
		return value != null;
	}

	/**
	 * If this node has children or a value already then we won't reassign/assign a value, because it means we need to either
	 * descend the value or we need to descend further down
	 * @param node  The node to assign
	 * @return      Whether or not we actually assigned the value or not
	 */
	public boolean addValue(Node node){
		if(value != null || hasChildren()) return false;
		value = node;
		return true;
	}

	/**
	 * Returns the quadrant number that x and y are in relative to this QuadNodes position, width and height. If x,y lie
	 * in the top left quadrant of the 2x2 matrix return 0, 1 if it's in the top right, 2 for bottom left, and finally
	 * 3 for bottom right.
	 * @param x The x position we are returning the quadrant for.
	 * @param y The y position we are returning the quadrant for.
	 * @return  The actual quadrant number that x,y lie in
	 */
	public int getQuadrant(double x, double y){
		/*I had to MathUtil.round every single value, otherwise I was getting values that appeared to be exactly the same, but
			were actually off by 3*10^-14, meaning they were not equal to each other. Annoying.*/
		if(MathUtil.valueWithin(x, location.x, location.x + width/2) && MathUtil.valueWithin(y, location.y, location.y + height/2))
			return 0;
		else if(MathUtil.valueWithin(x, location.x + width/2, location.x + width) && MathUtil.valueWithin(y, location.y, location.y + height/2))
			return 1;
		else if(MathUtil.valueWithin(x, location.x, location.x + width/2) && MathUtil.valueWithin(y, location.y + height/2, location.y + height))
			return 2;
		else if(MathUtil.valueWithin(x, location.x + width/2, location.x + width) && MathUtil.valueWithin(y, location.y + height/2, location.y + height))
			return 3;
		return -1;
	}

	/**
	 * Creates a new QuadNode based off the inputted quadrant number, respective of this nodes position, width and height
	 * @param quadrant The quadrant number
	 * @return  The QuadNode we generated
	 */
	public QuadNode createNodeFromQuadrant(int quadrant){
		if(quadrant == 0){
			return new QuadNode(new Location(location.x, location.y), width / 2, height / 2, this);
		}else if(quadrant == 1){
			return new QuadNode(new Location(location.x + width / 2, location.y), width / 2, height / 2, this);
		}else if(quadrant == 2){
			return new QuadNode(new Location(location.x, location.y + height / 2), width / 2, height / 2, this);
		}else{
			return new QuadNode(new Location(location.x + width / 2, location.y + height / 2), width / 2, height / 2, this);
		}
	}

	/**
	 * Descends the value associated with this QuadNode, it will descend it down into the appropriate quadrant that it
	 * should go down into. If there is no value then we don't do anything.
	 */
	public void descendValue(){
		if(value != null){
			subdivide();
			QuadNode child = children[getQuadrant(value.getX(), value.getY())];
			child.addValue(value);
			value = null;
		}
	}

	/**
	 * @return The x position of the QuadNode
	 */
	public double getX(){
		return location.x;
	}

	/**
	 * @return The y position of the QuadNode
	 */
	public double getY(){
		return location.y;
	}

	/**
	 * @return The width of the QuadNode
	 */
	public double getWidth(){
		return width;
	}

	/**
	 * @return The height of the QuadNode
	 */
	public double getHeight(){
		return height;
	}

	/**
	 * Creates a child of this node in the respective quadrant x,y position, then assigns it as a child of this node.
	 * If a child already exists in that quadrant then we just return that.
	 * @param x The x position of the child we want to create
	 * @param y The y position of the child we want to create
	 * @return  The QuadNode child we create
	 */
	public QuadNode getChildCreate(double x, double y){
		int quadrant = getQuadrant(x, y);
		if(children[quadrant] == null){
			children[quadrant] = createNodeFromQuadrant(getQuadrant(x, y));
			return children[quadrant];
		}else{
			return children[quadrant];
		}
	}

	/**
	 * Returns the QuadNode child of this node based off provided x and y position, if there is none then we return null.
	 * @param x The x position we want to get the child associated with
	 * @param y The y position we want to get the child associated with
	 * @return The child QuadNode associated with the x,y position
	 */
	public QuadNode getChild(double x, double y){
		int index = getQuadrant(x, y);
		return index == -1 ? null : children[index];
	}

	/**
	 * @return The parent of this QuadNode (Root QuadNode will return null)
	 */
	public QuadNode getParent(){
		return parent;
	}

	@Override
	public String toString(){
		return "QuadNode{" +
				", location=" + location +
				", width=" + width +
				", height=" + height +
				'}';
	}

	/**
	 * @return  Returns true if this node has any children
	 */
	public boolean hasChildren(){
		for(QuadNode child : children){
			if(child != null) return true;
		}
		return false;
	}
}