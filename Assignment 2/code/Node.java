import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Node implements Iterable<Segment>{
	private int ID;
	private Location location;

	private int depth = Integer.MAX_VALUE;
	private int reachBack = Integer.MAX_VALUE;
	private Node parent;
	private List<Node> searchChildren = new ArrayList<>();

	private boolean isIntersection = false;

	private Map<Node, Segment> incoming = new HashMap<>();
	private Map<Node, Segment> outgoing = new HashMap<>();

	private Map<Node, Node> restrictions = new HashMap<>();

	public static final int SIZE = 5;

	public Node(int ID, double x, double y){
		this.ID = ID;
		this.location = Location.newFromLatLon(x, y);
	}

	/**
	 * Renders this node to the passed graphics pane, the location of rendering is based off the nodes location,
	 * the scale and the origin location. If the boolean selected is true then we render the node as red, as it
	 * has been selected by the user
	 * @param g             The graphics pane in which to render the node
	 * @param scale         The the number of pixels per kilometer
	 * @param origin        The origin of which to render based off
	 * @param selected      Whether or not the node has been selected by the user
	 */
	public void redraw(Graphics g, double scale, Location origin, boolean selected, boolean partOfPath){
		if(g.getClipBounds().contains(location.asPoint(origin, scale))){    //Ensures we aren't rendering things that can't be seen
			g.setColor(selected ? Color.RED : partOfPath ? Color.BLUE : Color.BLACK);//isIntersection ? Color.MAGENTA : Color.BLACK);
			Point point = location.asPoint(origin, scale);
			int _size = AStarManager.isStartOrEnd(this) ? SIZE * 2 : SIZE;
			g.fillOval(point.x - _size / 2, point.y - _size / 2, _size, _size);
		}
	}

	/**
	 * Adds an incoming segment to the segment list
	 * @param segment The segment to add
	 */
	public void addIncoming(Segment segment){
		incoming.put(segment.getStart(), segment);
	}

	/**
	 * Adds an outgoing segment to the segment list
	 * @param segment The segment to add
	 */
	public void addOutgoing(Segment segment){
		outgoing.put(segment.getEnd(), segment);
	}


	public void addRestriction(Node comingFrom, Node goingTo){
		restrictions.put(comingFrom, goingTo);
	}


	public boolean isRestricted(Node comingFrom, Node goingTo){
		if(!incoming.containsKey(comingFrom) || !outgoing.containsKey(goingTo) || !restrictions.containsKey(comingFrom)) return false;
		return restrictions.get(comingFrom).equals(goingTo);
	}

	/**
	 * Gets the segment associated to the node passed on
	 * @param node	The node we are getting a segment that goes to
	 * @return		The segment
	 */
	public Segment getIncomingSegment(Node node){
		return incoming.get(node);
	}

	public Segment getOutgoingSegment(Node node){
		return outgoing.get(node);
	}

	public Set<Node> getOutgoingNodes(){
		return outgoing.keySet();
	}

	public Set<Node> getIncomingNodes(){
		return incoming.keySet();
	}

	public void setIntersection(boolean intersection) {
		isIntersection = intersection;
	}

	public int getID() {
		return ID;
	}

	/**
	 * @return x location of the node
	 */
	public double getX(){
		return location.x;
	}

	/**
	 * @return y location of the node
	 */
	public double getY(){
		return location.y;
	}

	/**
	 * @return location of the node.
	 */
	public Location getLocation(){
		return location;
	}

	/**
	 * @return Returns information about this node in an easy to read string, used for displaying information about
	 * selected nodes.
	 */
	public String getInformation(){
		StringBuilder info = new StringBuilder("Intersection ID: " + ID +
				"\nRoads Connected:\n");
		Set<String> roadNames = new HashSet<>();
		for(Segment segment : this){
			if(!roadNames.contains(segment.getRoad().getName())){
				roadNames.add(segment.getRoad().getName());
				info.append("   " + segment.getRoad().getName() + "\n");
			}
		}
		info.append("---------------");
		return info.toString();
	}

	/**
	 * @return Combination of incoming and outgoing stream as an iterator, used for enhanced for-loops
	 */
	@Override
	public Iterator<Segment> iterator(){
		return Stream.concat(incoming.values().stream(), outgoing.values().stream()).collect(Collectors.toSet()).iterator();
	}


	public Set<Node> getNeighbours(){
		return Stream.concat(incoming.keySet().stream(), outgoing.keySet().stream()).collect(Collectors.toSet());
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getReachBack() {
		return reachBack;
	}

	public void setReachBack(int reachBack) {
		this.reachBack = reachBack;
	}

	public List<Node> getSearchChildren() {
		return searchChildren;
	}

	public void resetAP(){
		this.depth = Integer.MAX_VALUE;
		this.reachBack = Integer.MAX_VALUE;
		this.searchChildren.clear();
		this.searchChildren.addAll(getNeighbours());
	}

	public boolean removeParent(){
		return searchChildren.remove(parent);
	}

	public Node getSearchChild(){
		return searchChildren.remove(0);
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isIntersection() {
		return isIntersection;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return ID == node.ID;
	}

	@Override
	public int hashCode(){
		return Objects.hash(ID);
	}

	@Override
	public String toString(){
		return "Node{" +
				"ID=" + ID +
				", location=" + location +
				'}';
	}
}
