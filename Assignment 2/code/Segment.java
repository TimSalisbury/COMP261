import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Segment{
	private Road road;
	private List<Location> coords;      //Coordinates that the segment runs through, used for rendering
	private double length;
	private Node start;
	private Node end;

	public Segment(Road road, List<Location> coords, double length, Node start, Node end){
		this.road = road;
		this.coords = coords;
		this.length = length;
		this.start = start;
		this.end = end;
	}

	/**
	 * Draws the segment to the given graphics pane, based off the scale and origin provided. The the selected boolean
	 * is true then we render the segment as red as it has been selected as part of a road by the user.
	 * @param g         The graphics pane in which to render to
	 * @param scale     The numbers of pixels per kilometer
	 * @param origin    The origin of the rendering
	 * @param selected  Whether or not the road has been selected
	 */
	public void redraw(Graphics g, double scale, Location origin, boolean selected, boolean partOfPath){
		//Ensures that the polygon is actually visible in the graphics pane, if it isn't then we don't want to render it.
		if(g.getClipBounds().contains(start.getLocation().asPoint(origin, scale)) || g.getClipBounds().contains(end.getLocation().asPoint(origin, scale))){
			g.setColor(selected ? Color.RED : partOfPath ? Color.BLUE : Color.BLACK);
			for(int i = 0; i < coords.size() - 1; i++){
				g.drawLine(coords.get(i).asPoint(origin, scale).x, coords.get(i).asPoint(origin, scale).y,
						coords.get(i + 1).asPoint(origin, scale).x, coords.get(i + 1).asPoint(origin, scale).y);
			}
		}
	}

	/**
	 * @return The road object associated with the segment
	 */
	public Road getRoad(){
		return road;
	}

	/**
	 * @return The start node of the segment
	 */
	public Node getStart(){
		return start;
	}

	/**
	 * @return Then end node of the segment
	 */
	public Node getEnd(){
		return end;
	}

	/**
	 * @return The length of this segment
	 */
	public double getLength() {
		return length;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Segment segment = (Segment) o;
		return Double.compare(segment.length, length) == 0 &&
				Objects.equals(road, segment.road) &&
				(Objects.equals(start, segment.start) || Objects.equals(start, segment.end)) &&
				(Objects.equals(end, segment.end) || Objects.equals(end, segment.start));
	}

	@Override
	public int hashCode() {
		return Objects.hash(road, length, start, end);
	}
}
