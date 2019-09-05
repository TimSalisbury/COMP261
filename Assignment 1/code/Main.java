import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends GUI{

	private Map<Integer, Node> nodeMap = new HashMap<>();
	private Map<Integer, Road> roadMap = new HashMap<>();
	private List<Polygon> polygons = new ArrayList<>();

	private double scale;       //AKA pixels per kilometer
	private Location origin;    //Origin for rendering from
	private double width;
	private double height;

	private Node selectedNode;
	private Set<Road> selectedRoads = new HashSet<>();

	private TrieNode<Road, Character> trieRoot;
	private QuadNode quadRoot;

	private Location dragStart;

	private static final double ZOOM_FACTOR = 1.05;
	private static final double ZOOM_LEVEL_STEP = 50;

	private static final boolean RENDER_QUADNODES = true;

	private Set<QuadNode> searched = new HashSet<>();

	private Main(){
	}

	/**
	 * Draws all of the nodes, roads and polygons associated with our graph to the graphics object passed to it.
	 * It does this in the order of Polygons, nodes, then roads (and in connection to roads all of the segments)
	 *
	 * @param g The graphics object in which to draw the graph to
	 */
	@Override
	protected void redraw(Graphics g){
		for(Polygon polygon : polygons){
			if(ZOOM_LEVEL_STEP / scale < polygon.getZoomLevel()){   //Checks if our current zoom level is not larger
				polygon.redraw(g, scale, origin);                   //than the maximum provided by the polygon.
			}
		}
		for(Node node : nodeMap.values()){
			if(node == selectedNode) node.redraw(g, scale, origin, true);
			else node.redraw(g, scale, origin, false);
		}
		for(Road road : roadMap.values()){
			if(selectedRoads.contains(road)) road.redraw(g, scale, origin, true);
			else road.redraw(g, scale, origin, false);
		}

		if(RENDER_QUADNODES && quadRoot != null)
			quadRoot.redraw(g, origin, scale);
	}

	/**
	 * Processes a mouse button release event based off the passed on mouse event, which holds key information about the
	 * actual event such as position and event type. Selects the node closest to the mouse click (provided by the QuadTree
	 * structure), highlighting it and also printing details about said node to the text pane.
	 *
	 * @param e The mouse event that needs to be processed
	 */
	@Override
	protected void onRelease(MouseEvent e){
		Location loc = Location.newFromPoint(e.getPoint(), origin, scale);

		QuadNode selectedQuadNode = quadRoot.descend(loc.x, loc.y);
		selectedNode = selectedQuadNode.getValue();
		searched.clear();
		searched.add(selectedQuadNode);
		checkClosest(loc.x, loc.y, selectedQuadNode.getParent());
		println(selectedNode.getInformation());
	}

	/**
	 * Checks that the QuadTree descending done in onRelease actually returned the closest node, if it didn't then this
	 * function will eventually find the closest one. This is done by ascending up the tree, looping through the parents
	 * children, checking if they've been searched before or not, if they haven't then we calculate the shortest distance
	 * between the child and the mouse position. If this distance is greater than the already calculated distance between
	 * the selected node and the mouse, then we don't both checking the children of this node. If that distance is shorter
	 * then we move down into it, check children and potential values held within this branch and check if they are closer
	 * to the mouse than the the selected node we reassign selected node and keep searching. Eventually we ascend up to
	 * the root quad node, where the search stops.
	 * @param x     The x position of the click
	 * @param y     The y position of the click
	 * @param node  The current node that holds the current selected value
	 */
	private void checkClosest(double x, double y, QuadNode node){
		double distance = MathUtil.distance(selectedNode.getX(), selectedNode.getY(), x, y);
		if(node.hasChildren()){
			for(QuadNode child : node.children){
				double quadNodeDistance = MathUtil.distance(x, y, MathUtil.constrain(x, child.getX(), child.getX() + child.getWidth()),
						MathUtil.constrain(y, child.getY(), child.getY() + child.getHeight()));     //Calculate closest distance between child and the mouse
				if(quadNodeDistance > distance){                                                         //position.
					searched.add(child);    //If this distance is larger than the distance between selected node and mouse then we
					continue;               //don't bother checking it
				}
				if(child.hasValue() && !searched.contains(child)){
					double childDistance = MathUtil.distance(child.getValue().getX(), child.getValue().getY(), x, y);   //Calculate distance between child value and mouse
					if(childDistance < distance) selectedNode = child.getValue();       //If it's shorter than distance to the current selected node we reassign selected node
					searched.add(child);
				}else if(child.hasChildren() && !searched.contains(child)){             //If the child has children, check them
					checkClosest(x, y, child);
				}
			}
		}
		searched.add(node);
		if(node.getParent() != null){       //Ascend up the tree
			checkClosest(x, y, node.getParent());
		}
	}

	/**
	 * Processes all mouse wheel scrolling events that may happen on the graphics pane. Changes the rendering scale
	 * based on the direction of the scroll and how far it scrolled, giving the scroll wheel effective zooming in/out
	 * abilities.
	 *
	 * @param e The MouseWheel event - Holds key information such as direction and amount of scrolling to by processed.
	 */
	@Override
	protected void onScroll(MouseWheelEvent e){
		if(e.getWheelRotation() == -1){
			onMove(Move.ZOOM_IN);
		}else{
			onMove(Move.ZOOM_OUT);
		}
	}

	/**
	 * Processes drag events happening on the graphics pane. Moves the origin based on how far the the user is dragging.
	 *
	 * @param e The dragging event parameters - Holds key information such as mouse position
	 */
	@Override
	protected void onDrag(MouseEvent e){
		Location loc = Location.newFromPoint(e.getPoint(), origin, scale);
		origin = origin.moveBy(dragStart.x - loc.x, dragStart.y - loc.y);
	}

	/**
	 * Processes mouse button press events, all it does is set the global field dragStart to the location of the mouse,
	 * this is so when onDrag is called the program can calculate how far the user is dragging the screen to move the
	 * origin appropriately.
	 *
	 * @param e Holds key information about the event, such as the position of the mouse at the events time.
	 */
	@Override
	protected void onPress(MouseEvent e){
		dragStart = Location.newFromPoint(e.getPoint(), origin, scale);
	}

	/**
	 * Called whenever the user edits the search box (except for when backspace or delete events), starts by
	 * obtaining the text in the text boxed based off whether or not it is a selected road from the drop down
	 * or a genuine inputted string, then descends down the Trie Structure created for quick name and prefix
	 * searching. It descends down this tree as far as it can (or stops early if it can't go any further), then
	 * prints out the children of the end node if it has any, if it does not it then prints everything below the
	 * said node in the tree to the user. Also updates the JComboBox that the user typed into to show suggestions
	 * based off the inputted text (once again uses the Trie Structure created at data load).
	 */
	@Override
	protected void onSearch(){
		selectedRoads.clear();

		String text = getSearchBox().getEditor().getItem() instanceof Road ? ((Road) getSearchBox().getEditor().getItem()).getName() : //Gets the text in the box, which could be based off a selected
				(String) getSearchBox().getEditor().getItem();                                                                         //road or a inputted string
		println("Searching roads with name: " + text);
		text = text.toLowerCase().replace(" ", "");
		char[] nameChar = text.toCharArray();

		TrieNode node = trieRoot;

		//Descends trie structure as far as it can
		for(int i = 0; i < nameChar.length; i++){
			if(node.containsChild(nameChar[i])){
				node = node.getChild(nameChar[i]);
			}else{  //If it finds a dead end while descending, report to the user
				println("No roads found.");
				break;
			}

			if(i == nameChar.length - 1){   //If we're currently on the end index then check if the node has any values
				if(node.hasValues()){   //If it does, add them to the selected values.
					selectedRoads.addAll(node.getValues());
				}else{  //If not, then print out everything below the said node to the user.
					recPrintTrie(node);
				}
				println("Found " + selectedRoads.size() + " road(s).");
				for(Road road : selectedRoads){
					println(road.toString());
				}
			}
		}

		//Updates the JComboBox by adding everything in the selectedRoads array as an option
		Object pre = getSearchBox().getEditor().getItem();
		getSearchBox().setModel(new DefaultComboBoxModel(selectedRoads.toArray()));
		getSearchBox().getEditor().setItem(pre);
		getSearchBox().setPopupVisible(true);
	}

	/**
	 * Called when the user presses one of the buttons on the GUI, moves the origin or adjusts the scale accordingly
	 * to the button pressed.
	 *
	 * @param m An enum containing the type of button pressed
	 */
	@Override
	protected void onMove(Move m){
		//Width and height pre-adjusting
		double _width = width;
		double _height = height;

		switch(m){
			case WEST:
				origin = origin.moveBy(-width * 0.05, 0);
				break;
			case EAST:
				origin = origin.moveBy(width * 0.05, 0);
				break;
			case SOUTH:
				origin = origin.moveBy(0, width * 0.05);
				break;
			case NORTH:
				origin = origin.moveBy(0, -width * 0.05);
				break;
			case ZOOM_IN:
				scale *= ZOOM_FACTOR;
				width /= ZOOM_FACTOR;
				height /= ZOOM_FACTOR;
				break;
			case ZOOM_OUT:
				scale /= ZOOM_FACTOR;
				width *= ZOOM_FACTOR;
				height *= ZOOM_FACTOR;
				break;
		}

		//Adjusts the origin based off the possible new data set width and height, this is to ensure when the user
		//zooms in and out it appears to be zooming in and out around the center of the graphics pane.
		origin = origin.moveBy((_width - width) / 2, -(_height - height) / 2);
	}

	/**
	 * Loads the graph information based on the passed on files, calculates the width and height of the dataset based
	 * off the smallest and largest x, y position in the set. Also calculates scale so that the entire width of the map
	 * fits into the window width, and positions the origin so the top left of the dataset is situated at the top left
	 * of the origin.
	 *
	 * @param nodes    a File for nodeID-lat-lon.tab
	 * @param roads    a File for roadID-roadInfo.tab
	 * @param segments a File for roadSeg-roadID-length-nodeID-nodeID-coords.tab
	 * @param polygons a File for polygon-shapes.mp
	 */
	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons){
		this.nodeMap.clear();
		this.roadMap.clear();
		this.polygons.clear();
		this.trieRoot = new TrieNode<>();

		loadNodes(nodes);
		loadRoads(roads);
		loadSegments(segments);

		if(polygons != null){       //Don't load polygons if the file doesn't exist
			loadPolygons(polygons);
		}

		//Comparators for obtaining max and min x,y positions
		Comparator<Node> comparatorX = (A, B)->{
			if(A.getX() == B.getX()) return 0;
			return A.getX() > B.getX() ? 1 : -1;
		};

		Comparator<Node> comparatorY = (A, B)->{        //Y gets inverted at rendering
			if(A.getY() == B.getY()) return 0;
			return A.getY() > B.getY() ? 1 : -1;
		};

		Node maxX = Collections.max(nodeMap.values(), comparatorX);
		Node minX = Collections.min(nodeMap.values(), comparatorX);

		Node maxY = Collections.max(nodeMap.values(), comparatorY);
		Node minY = Collections.min(nodeMap.values(), comparatorY);

		height = maxY.getY() - minY.getY();
		width = maxX.getX() - minX.getX();
		scale = getDrawingAreaDimension().width / width;
		origin = new Location(minX.getX(), maxY.getY());

		quadRoot = new QuadNode(new Location(minX.getX(), minY.getY()), width, height, null);

		for(Node node : nodeMap.values()){
			recAddQuad(quadRoot, node);
		}


		println("Loaded");
	}


	/**
	 * Loads all nodes (Intersections and road ends) from the provided file into the nodeMap HashMap.
	 *
	 * @param file The file containing information about nodes in the map
	 */
	private void loadNodes(File file){
		BufferedReader nodesIn; //Nodes in
		try{
			nodesIn = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			e.printStackTrace();
			println("Failed to read Nodes Information.");
			return;
		}
		try{
			String line;
			while((line = nodesIn.readLine()) != null){         //While we still have lines to read
				String[] values = line.split("\t");
				Node node = new Node(Integer.valueOf(values[0]), Double.valueOf(values[1]),
						Double.valueOf(values[2]));
				nodeMap.put(Integer.valueOf(values[0]), node);

			}
		}catch(IOException e){
			e.printStackTrace();
			println("Failed to read line in Nodes Information.");
		}
	}

	/**
	 * Loads all roads from the file into the roadMap HashMap, also stores them into the TrieStructure for ease of
	 * searching names (root being trieRoot)
	 *
	 * @param file The file containing information about the roads in the map
	 */
	private void loadRoads(File file){
		BufferedReader roadsIn;         //Roads in
		try{
			roadsIn = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			e.printStackTrace();
			println("Failed to read Roads Information.");
			return;
		}
		try{
			String line;
			roadsIn.readLine();                         //Skip the header line of the file
			while((line = roadsIn.readLine()) != null){
				String[] values = line.split("\t");
				Road road = new Road(Integer.valueOf(values[0]), values[2], values[3], Integer.valueOf(values[4]),
						Integer.valueOf(values[5]), Integer.valueOf(values[6]), Integer.valueOf(values[7]), Integer.valueOf(values[8]),
						Integer.valueOf(values[9]));
				roadMap.put(Integer.valueOf(values[0]), road);

				//Change to lowercase and remove all spaces, then construct the trie structure for
				//quickly searching roads and road prefixes.
				String text = road.getName().toLowerCase().replace(" ", "");
				char[] characters = text.toCharArray();
				TrieNode<Road, Character> node = trieRoot;
				for(char letter : characters){       //Descend down/create children nodes that we need as we go
					if(!node.containsChild(letter)){
						node.addChild(new TrieNode<>(letter), letter);
					}
					node = node.getChild(letter);
				}
				node.addValue(road);
			}
		}catch(IOException e){
			e.printStackTrace();
			println("Failed to read line in Roads Information.");
		}
	}

	/**
	 * Loads all segments from the file and constructs the edges of the graph by adding them to the incoming and outgoing
	 * of the segments start and end nodes.
	 *
	 * @param file The file containing information about the segments in the map
	 */
	private void loadSegments(File file){
		BufferedReader segmentsIn;
		try{
			segmentsIn = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			e.printStackTrace();
			println("Failed to read Segments Information.");
			return;
		}
		try{
			String line;
			segmentsIn.readLine();
			while((line = segmentsIn.readLine()) != null){
				String[] values = line.split("\t");
				List<Location> locations = new ArrayList<>();
				for(int i = 4; i < values.length; i += 2){
					locations.add(Location.newFromLatLon(Double.valueOf(values[i]), Double.valueOf(values[i + 1])));
				}
				Segment segment = new Segment(roadMap.get(Integer.valueOf(values[0])), locations, Double.valueOf(values[1]),
						nodeMap.get(Integer.valueOf(values[2])), nodeMap.get(Integer.valueOf(values[3])));

				//Based off whether the edge is one way or both ways add the edges to the start and end nodes appropriately
				if(segment.getRoad().getDirection() == Direction.BOTH){
					segment.getStart().incoming.add(segment);
					segment.getStart().outgoing.add(segment);

					segment.getEnd().incoming.add(segment);
					segment.getEnd().outgoing.add(segment);
				}else{
					segment.getStart().outgoing.add(segment);
					segment.getEnd().incoming.add(segment);
				}
				//Add edge to respective road
				roadMap.get(segment.getRoad().getID()).segments.add(segment);
			}
		}catch(IOException e){
			e.printStackTrace();
			println("Failed to read line in Segments Information.");
		}
	}

	/**
	 * Loads all the polygons provided in polygon-shapes.mp file that may be provided into the polygons list,
	 * then sorts said list so rendering order is correct (based on the static map in polygon class).
	 *
	 * @param file The file containing information about the polygons in the map
	 */
	private void loadPolygons(File file){
		BufferedReader polygonsIn;
		try{
			polygonsIn = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			e.printStackTrace();
			println("Failed to read Polygon Information.");
			return;
		}
		try{
			String line;
			while((line = polygonsIn.readLine()) != null){
				Polygon polygon = new Polygon();
				while((line = polygonsIn.readLine()) != null && !line.contains("[END]")){
					if(line.contains("[POLYGON]")) continue;
					String lType = line.substring(0, line.lastIndexOf("="));        //Gets the lines data type
					String value = line.substring(line.lastIndexOf("=") + 1);       //And the actual value assigned to type
					switch(lType){
						case "Type":
							polygon.setType(Integer.decode(value));
							break;
						case "Label":
							polygon.setLabel(value);
							break;
						case "EndLevel":
							polygon.setZoomLevel(Integer.valueOf(value));
							break;
						case "Data0":
							Pattern pattern = Pattern.compile("\\(([+-]?[0-9]*\\.?[0-9]+),([+-]?[0-9]*\\.?[0-9]+)\\)"); //Regex for extracting polygon point coordinates.
							Matcher matcher = pattern.matcher(value);
							List<Location> locations = new ArrayList<>();
							while(matcher.find()){
								locations.add(Location.newFromLatLon(Double.valueOf(matcher.group(1)), Double.valueOf(matcher.group(2))));
							}
							polygon.addPoint(locations);
							break;
					}
				}
				if(polygon.hasValues()){
					polygons.add(polygon);
				}
			}
			Collections.sort(polygons); //Finally sort the polygon list (based off rendering priority) to ensure the rendering order is correct
		}catch(IOException e){
			e.printStackTrace();
			println("Failed to read line in Polygon Information");
		}
	}

	/**
	 * Recursively prints all children below the root node provided in the trie structure.
	 *
	 * @param node Root (of the current printing) node, generally to initialise the processing of printing the trie
	 *             the TrieNode passed to begin with will be trieRoot or the node at the end of an inputted string
	 */
	private void recPrintTrie(TrieNode<Road, Character> node){
		if(node.hasValues()){
			selectedRoads.addAll(node.getValues());
		}
		if(node.hasChildren()){
			for(TrieNode<Road, Character> child : node){
				recPrintTrie(child);
			}
		}
	}

	/**
	 * Used for building of the QuadTree used to quickly find closest node to a click position. Works by first attempting
	 * to add the passed on root QuadNode a node value, and if unsuccessful (meaning it already has a value or has children
	 * QuadNodes) it moves down the tree and descends any values in it's way on the way down.
	 *
	 * @param root The QuadNode to check and descend any possible values associated with it
	 * @param node The node value we are attempting to place in the QuadTree structure
	 */
	private void recAddQuad(QuadNode root, Node node){
		if(!root.addValue(node)){
			root.descendValue();
			recAddQuad(root.getChildCreate(node.getX(), node.getY()), node);
		}
	}

	/**
	 * Helper function for printing text to the GUI's text pane at the bottom
	 *
	 * @param text The text to print to the text pane
	 */
	private void println(String text){
		getTextOutputArea().append(text + "\n");

	}

	public static void main(String[] args){
		new Main();
	}
}
