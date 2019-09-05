import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Road{
	private int ID;
	private String name;
	private String city;
	private Direction direction;
	private boolean forCar;
	private boolean forPed;
	private boolean forByc;
	private int speedLimit;
	private Class roadClass;

	public List<Segment> segments = new ArrayList<>();


	public Road(int ID, String name, String city, int direction, int speed, int rClass, int forCar, int forPed, int forByc){
		this.ID = ID;
		this.name = name;
		this.city = city;
		this.direction = Direction.values()[direction];
		this.speedLimit = speed;
		this.roadClass = Class.values()[rClass];
		this.forCar = forCar == 0;
		this.forPed = forPed == 0;
		this.forByc = forByc == 0;

	}

	/**
	 * Draws the road (the segments associated to the road) to the graphics pane provided, locations based off the scale
	 * and origin of the rendering. Roads will be highlighted in red if the boolean selected is true, as the user has
	 * selected the road.
	 * @param g         The graphics pane in which to render to
	 * @param scale     The numbers of pixels per kilometer
	 * @param origin    The origin of the rendering
	 * @param selected  Whether or not the road has been selected
	 */
	public void redraw(Graphics g, double scale, Location origin, boolean selected){
		for(Segment segment : segments){
			segment.redraw(g, scale, origin, selected);
		}
	}

	@Override
	public String toString(){
		return name;
	}

	/**
	 * @return The ID of the road
	 */
	public int getID(){
		return ID;
	}

	/**
	 * @return The name of the road.
	 */
	public String getName(){
		return name;
	}

	/**
	 * @return The direction of the road
	 */
	public Direction getDirection(){
		return direction;
	}
}
