import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Polygon implements Comparable{
	private int type;
	private int zoomLevel;
	private Color colour;
	private String label;
	private int renderPriority;

	private List<List<Location>> polygonPoints;

	private static Map<Integer, Color> COLOUR_MAP = new HashMap<>();                        //Used for obtaining the different colours for different types of polygons
	private static Map<Integer, Integer> RENDER_PRIORITIES = new HashMap<>();               //Used for obtaining the rendering priority for different types of polygons
	static{
		//Statically declare the members of COLOUR_MAP HashMap
		COLOUR_MAP.put(64, new Color(73, 134, 205));                               //Lake
		COLOUR_MAP.put(65, new Color(73, 134, 205));                               //Lake
		COLOUR_MAP.put(2, Color.GRAY);                                                      //City?
		COLOUR_MAP.put(69, Color.BLUE);                                                     //Blue-Unknown
		COLOUR_MAP.put(7, Color.GRAY);                                                      //Airport
		COLOUR_MAP.put(71, new Color(73, 134, 205));                               //River
		COLOUR_MAP.put(72, new Color(73, 134, 205));                               //River
		COLOUR_MAP.put(40, new Color(73, 134, 205));                               //Ocean
		COLOUR_MAP.put(8, Color.ORANGE);                                                    //Shopping-Centre
		COLOUR_MAP.put(10, Color.GREEN);                                                    //University
		COLOUR_MAP.put(14, Color.DARK_GRAY);                                                //Airport-Runway
		COLOUR_MAP.put(80, new Color(204, 102, 0));                                //Woods
		COLOUR_MAP.put(19, Color.ORANGE);                                                   //Man made area
		COLOUR_MAP.put(22, Color.GREEN);                                                    //National Park
		COLOUR_MAP.put(23, Color.GREEN);                                                    //City Park
		COLOUR_MAP.put(24, Color.GREEN);                                                    //Golf
		COLOUR_MAP.put(25, Color.pink);                                                     //Sport
		COLOUR_MAP.put(26, Color.DARK_GRAY);                                                //Cemetery
		COLOUR_MAP.put(11, Color.PINK);                                                     //Hospital
		COLOUR_MAP.put(5, Color.DARK_GRAY);                                                 //Car Park
		COLOUR_MAP.put(60, new Color(73, 134, 205));                               //Lake
		COLOUR_MAP.put(62, new Color(73, 134, 205));                               //Lake
		COLOUR_MAP.put(30, Color.GREEN);                                                    //State Park

		//Statically declare members of RENDER_PRIORITIES HashMap
		RENDER_PRIORITIES.put(64, 1);
		RENDER_PRIORITIES.put(65, 1);
		RENDER_PRIORITIES.put(2, 1);
		RENDER_PRIORITIES.put(40, 1);
		RENDER_PRIORITIES.put(60, 1);
		RENDER_PRIORITIES.put(62, 1);
		RENDER_PRIORITIES.put(30, 1);
		RENDER_PRIORITIES.put(22, 1);
		RENDER_PRIORITIES.put(7, 2);
		RENDER_PRIORITIES.put(69, 2);
		RENDER_PRIORITIES.put(8, 2);
		RENDER_PRIORITIES.put(10, 2);
		RENDER_PRIORITIES.put(80, 2);
		RENDER_PRIORITIES.put(24, 2);
		RENDER_PRIORITIES.put(25, 2);
		RENDER_PRIORITIES.put(23, 2);
		RENDER_PRIORITIES.put(72, 2);
		RENDER_PRIORITIES.put(71, 2);
		RENDER_PRIORITIES.put(26, 2);
		RENDER_PRIORITIES.put(11, 2);
		RENDER_PRIORITIES.put(5, 2);
		RENDER_PRIORITIES.put(14, 3);
		RENDER_PRIORITIES.put(19, 3);
	}

	public Polygon(int type, int zoomLevel){
		this.type = type;
		this.zoomLevel = zoomLevel;
		this.polygonPoints = new ArrayList<>();
	}

	public Polygon(){
		this.polygonPoints = new ArrayList<>();
	}

	/**
	 * @return The label of this polygon
	 */
	public String getLabel(){
		return label;
	}

	/**
	 * Assigns this polygons label
	 * @param label The label
	 */
	public void setLabel(String label){
		this.label = label;
	}

	/**
	 * Adds a list of points to the polygon points, as according to the Polish Format provided polygons defined can have
	 * multiple different polygons inside of them (for polygons that have holes in them)
	 * @param points The list of points to add
	 */
	public void addPoint(List<Location> points){
		polygonPoints.add(points);
	}

	/**
	 * Renders the polygon to the graphics pane based off the scale and origin provided.
	 * @param g             The graphics pane in which to render the node
	 * @param scale         The the number of pixels per kilometer
	 * @param origin        The origin of which to render based off
	 */
	public void redraw(Graphics g, double scale, Location origin){
		for(List<Location> polygon : polygonPoints){
			int[] xPoints = new int[polygon.size()];
			int[] yPoints = new int[polygon.size()];
			boolean contains = false;
			for(int i = 0; i < polygon.size(); i++){
				Point point = polygon.get(i).asPoint(origin, scale);

				//If any one of the polygon's points lie inside of the graphics pane then we should render the entire polygon.
				if(g.getClipBounds().contains(point)) contains = true;
				xPoints[i] = point.x;
				yPoints[i] = point.y;
			}

			if(contains){
				g.setColor(colour);
				g.fillPolygon(xPoints, yPoints, polygon.size());
			}
		}
	}

	/**
	 * Sets the polygons type and also based off that gets the colour and render priority
	 * @param type The type to assign to this polygon.
	 */
	public void setType(int type){
		this.type = type;
		colour = COLOUR_MAP.get(type);
		renderPriority = RENDER_PRIORITIES.get(type);
	}

	/**
	 * @return The zoom level of this polygon
	 */
	public int getZoomLevel(){
		return zoomLevel;
	}

	/**
	 * @param zoomLevel The zoomLevel to assign to this polygon
	 */
	public void setZoomLevel(int zoomLevel){
		this.zoomLevel = zoomLevel;
	}

	/**
	 * @return Returns true if this polygon has a type, zoomLevel and any polygon points
	 */
	public boolean hasValues(){
		return type != 0 && zoomLevel != 0 && polygonPoints != null && polygonPoints.size() != 0;
	}

	@Override
	public String toString(){
		return "Polygon{" +
				"type=" + type +
				", zoomLevel=" + zoomLevel +
				", colour=" + colour +
				", label='" + label + '\'' +
				'}';
	}

	@Override
	public int compareTo(Object o){
		if(o instanceof Polygon){
			Polygon other = (Polygon) o;
			if(other.renderPriority == this.renderPriority) return 0;
			return other.renderPriority > this.renderPriority ? -1 : 1;
		}
		return 0;
	}
}
