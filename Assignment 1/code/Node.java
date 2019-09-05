import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Node implements Iterable<Segment>{
	private int ID;
	private Location location;
	public List<Segment> incoming = new ArrayList<>();
	public List<Segment> outgoing = new ArrayList<>();

	private static final int SIZE = 5;


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
	public void redraw(Graphics g, double scale, Location origin, boolean selected){
		if(g.getClipBounds().contains(location.asPoint(origin, scale))){    //Ensures we aren't rendering things that can't be seen
			g.setColor(selected ? Color.RED : Color.BLACK);
			Point point = location.asPoint(origin, scale);
			g.fillOval(point.x - SIZE / 2, point.y - SIZE / 2, SIZE, SIZE);
		}
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
		info.append("---------------\n");
		return info.toString();
	}

	/**
	 * @return Combination of incoming and outgoing stream as an iterator, used for enhanced for-loops
	 */
	@Override
	public Iterator iterator(){
		return Stream.concat(incoming.stream(), outgoing.stream()).collect(Collectors.toList()).iterator();
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
