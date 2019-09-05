package renderer;

import java.awt.*;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {

	private int startY;
	private int endY;

	private float[] xLeft;
	private float[] xRight;
	private float[] zLeft;
	private float[] zRight;

	private Color[] colorLeft;
	private Color[] colorRight;
	private Color[][] colorValues;

	/**
	 * Constructs a new EdgeList with the startY and endY specified, initialises all of the arrays based on the expected
	 * size of them
	 * @param startY The start y of the polygon
	 * @param endY The end y of the polygon
	 */
	public EdgeList(int startY, int endY) {
		this.startY = startY;
		this.endY = endY;
		int size = endY - startY + 1;
		xLeft = new float[size];
		xRight = new float[size];
		zLeft = new float[size];
		zRight = new float[size];
		colorLeft = new Color[size];
		colorRight = new Color[size];
		colorValues = new Color[size][];
	}

	/**
	 * Adds left values for the EdgeList to the arrays used to represent them
	 * @param y		The row of each value
	 * @param x		The calculated x value
	 * @param z		The calculated z value
	 * @param color	The calculated colour, might be null
	 */
	public void addLeft(int y, float x, float z, Color color){
		xLeft[y - startY] = x;
		zLeft[y - startY] = z;
		colorLeft[y - startY] = color;
	}

	/**
	 * Adds right values for the EdgeList to the arrays used to represent them
	 * @param y		The row of each value
	 * @param x		The calculated x value
	 * @param z		The calculated z value
	 * @param color	The calculated colour, might be null
	 */
	public void addRight(int y, float x, float z, Color color){
		xRight[y - startY] = x;
		zRight[y - startY] = z;
		colorRight[y - startY] = color;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}

	public float getLeftX(int y) {
		return xLeft[y-startY];
	}

	public float getRightX(int y) {
		return xRight[y-startY];
	}

	public float getLeftZ(int y) {
		return zLeft[y-startY];
	}

	public float getRightZ(int y) {
		return zRight[y-startY];
	}

	public Color getRightColor(int y){return colorRight[y-startY];}

	public Color getLeftColor(int y){return colorLeft[y-startY];}

	/**
	 * Initialises a row of colour values so they can be specified
	 * @param y The row to initialises
	 */
	public void initalizeColorRow(int y){
		int size = Math.abs(Math.round(getRightX(y)) - Math.round(getLeftX(y))) + 1;
		colorValues[y-startY] = new Color[size];
	}

	public void addColor(int x, int y, Color color){
		colorValues[y-startY][x - Math.round(getLeftX(y))] = color;
	}

	public Color getColor(int x, int y){
		x = x - Math.round(getLeftX(y));
		y -= startY;
		if(x == 0) return colorLeft[y];
		if(x == colorValues[y].length - 1) return colorRight[y];
		return colorValues[y][x];
	}
}

// code for comp261 assignments
