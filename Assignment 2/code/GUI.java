import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

/**
 * This is a template GUI that you can use for your mapping program. It is an
 * *abstract class*, which means you'll need to extend it in your own program.
 * For a simple example of how to do this, have a look at the SquaresExample
 * class.
 * <p>
 * This GUI uses Swing, not the first-year UI library. Swing is not the focus of
 * this course, but it would be to your benefit if you took some time to
 * understand how this class works.
 *
 * @author tony
 */
public abstract class GUI{
	/**
	 * defines the different types of movement the user can perform, the
	 * appropriate one is passed to your code when the move(Move) method is
	 * called.
	 */
	public enum Move{
		NORTH, SOUTH, EAST, WEST, ZOOM_IN, ZOOM_OUT
	}

	;

	// these are the methods you need to implement.

	protected abstract void onDrag(MouseEvent e);

	protected abstract void onPress(MouseEvent e);

	protected abstract void onScroll(MouseWheelEvent e);

	protected abstract void onSetStart();

	protected abstract void onSetEnd();

	protected abstract void calculateAPs();

	protected abstract void calculateAllAps();

	protected abstract void resetAPs();

	protected abstract boolean renderAPs();

	protected abstract boolean renderQuadNodes();

	protected abstract boolean renderPolygons();

	protected abstract void onSetMinimiseValue(boolean minimiseValue);

	/**
	 * Is called when the drawing area is redrawn and performs all the logic for
	 * the actual drawing, which is done with the passed Graphics object.
	 */
	protected abstract void redraw(Graphics g);

	/**
	 * Is called when the mouse is clicked (actually, when the mouse is
	 * released), and is passed the MouseEvent object for that click.
	 */
	protected abstract void onRelease(MouseEvent e);

	/**
	 * Is called whenever the search box is updated. Use getSearchBox to get the
	 * JTextField object that is the search box itself.
	 */
	protected abstract void onSearch();

	/**
	 * Is called whenever a navigation button is pressed. An instance of the
	 * Move enum is passed, representing the button clicked by the user.
	 */
	protected abstract void onMove(Move m);

	/**
	 * Is called when the user has successfully selected a directory to load the
	 * data files from. File objects representing the four files of interested
	 * are passed to the method. The fourth File, polygons, might be null if it
	 * isn't present in the directory.
	 *
	 * @param nodes    a File for nodeID-lat-lon.tab
	 * @param roads    a File for roadID-roadInfo.tab
	 * @param segments a File for roadSeg-roadID-length-nodeID-nodeID-coords.tab
	 * @param polygons a File for polygon-shapes.mp
	 */
	protected abstract void onLoad(File nodes, File roads, File segments,
	                               File polygons, File restrictions, File trafficLights);

	// here are some useful methods you'll need.

	/**
	 * @return the JTextArea at the bottom of the screen for output.
	 */
	public JTextArea getTextOutputArea(){
		return textOutputArea;
	}

	/**
	 * @return the JTextField used as a search box in the top-right, which can
	 * be queried for the string it contains.
	 */
	public JComboBox getSearchBox(){
		return search;
	}

	/**
	 * @return the dimensions of the drawing area.
	 */
	public Dimension getDrawingAreaDimension(){
		return drawing.getSize();
	}

	/**
	 * Redraws the window (including drawing pane). This is already done
	 * whenever a button is pressed or the search box is updated, so you
	 * probably won't need to call this.
	 */
	public void redraw(){
		frame.repaint();
	}

	public void setStartText(Node node){
		startText.setText(String.valueOf(node.getID()));
	}

	public void setEndText(Node node){
		endText.setText(String.valueOf(node.getID()));
	}

	public String getMinimiseValue(){
		return minimiseValue.getSelectedObjects()[0].toString();
	}

	// --------------------------------------------------------------------
	// Everything below here is Swing-related and, while it's worth
	// understanding, you don't need to look any further to finish the
	// assignment up to and including completion.
	// --------------------------------------------------------------------

	private static final boolean UPDATE_ON_EVERY_CHARACTER = true;

	public static final int DEFAULT_DRAWING_HEIGHT = 400;
	public static final int DEFAULT_DRAWING_WIDTH = 400;
	public static final int TEXT_OUTPUT_ROWS = 5;
	public static final int SEARCH_COLS = 15;

	private static final String NODES_FILENAME = "nodeID-lat-lon.tab";
	private static final String ROADS_FILENAME = "roadID-roadInfo.tab";
	private static final String SEGS_FILENAME = "roadSeg-roadID-length-nodeID-nodeID-coords.tab";
	private static final String POLYS_FILENAME = "polygon-shapes.mp";
	private static final String RESTR_FILENAME = "restrictions.tab";
	private static final String TRAFFIC_FILENAME = "NZtrafficLightCoords.txt";

	/*
	 * In Swing, everything is a component; buttons, graphics panes, tool tips,
	 * and the window frame are all components. This is implemented by
	 * JComponent, which sits at the top of the component inheritance hierarchy.
	 * A JFrame is a component that represents the outer window frame (with the
	 * minimise, maximise, and close buttons) of your program. Every swing
	 * program has to have one somewhere. JFrames can, of course, have other
	 * components inside them. JPanels are your bog-standard container component
	 * (can have other components inside them), that are used for laying out
	 * your UI.
	 */

	private JFrame frame;

	private JPanel controls;
	private JComponent drawing; // we customise this to make it a drawing pane.
	private JTextArea textOutputArea;

	private JTextField startText;
	private JTextField endText;

	private JComboBox<String> minimiseValue;

	private JComboBox search;
	private JFileChooser fileChooser;

	public GUI(){
		initialise();
	}

	@SuppressWarnings("serial")
	private void initialise(){

		/*
		 * first, we make the buttons etc. that go along the top bar.
		 */

		// action listeners give you a hook to perform when the button is
		// pressed. the horrible thing being passed to addActionListener is an
		// anonymous class, covered in SWEN221. these are useful when working
		// with swing. the quit button isn't really necessary, as you can just
		// press the frame's close button, but it serves as a nice example.
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				System.exit(0); // cleanly end the program.
			}
		});

		fileChooser = new JFileChooser();
		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				File nodes = null, roads = null, segments = null, polygons = null, restrictions = null, trafficLights = null;

				// set up the file chooser
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setDialogTitle("Select input directory");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// run the file chooser and check the user didn't hit cancel
				if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					// get the files in the selected directory and match them to
					// the files we need.
					File directory = fileChooser.getSelectedFile();
					File[] files = directory.listFiles();

					for(File f : files){
						if(f.getName().equals(NODES_FILENAME)){
							nodes = f;
						}else if(f.getName().equals(ROADS_FILENAME)){
							roads = f;
						}else if(f.getName().equals(SEGS_FILENAME)){
							segments = f;
						}else if(f.getName().equals(POLYS_FILENAME)){
							polygons = f;
						}else if(f.getName().equals(RESTR_FILENAME)){
							restrictions = f;
						}else if(f.getName().equals(TRAFFIC_FILENAME)){
							trafficLights = f;
						}
					}

					// check none of the files are missing, and call the load
					// method in your code.
					if(nodes == null || roads == null || segments == null){
						JOptionPane.showMessageDialog(frame,
								"Directory does not contain correct files",
								"Error", JOptionPane.ERROR_MESSAGE);
					}else{
						onLoad(nodes, roads, segments, polygons, restrictions, trafficLights);
						redraw();
					}
				}
			}
		});

		JButton west = new JButton("\u2190");
		west.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.WEST);
				redraw();
			}
		});

		JButton east = new JButton("\u2192");
		east.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.EAST);
				redraw();
			}
		});

		JButton north = new JButton("\u2191");
		north.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.NORTH);
				redraw();
			}
		});

		JButton south = new JButton("\u2193");
		south.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.SOUTH);
				redraw();
			}
		});

		JButton in = new JButton("+");
		in.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.ZOOM_IN);
				redraw();
			}
		});

		JButton out = new JButton("\u2012");
		out.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				onMove(Move.ZOOM_OUT);
				redraw();
			}
		});

		// next, make the search box at the top-right. we manually fix
		// it's size, and add an action listener to call your code when
		// the user presses enter.
		search = new JComboBox();
		search.setEditable(true);

		search.setMaximumSize(new Dimension(0, 25));
		search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onSearch();
				redraw();
			}
		});

		if(UPDATE_ON_EVERY_CHARACTER){
			// this forces an action event to fire on every key press, so the
			// user doesn't need to hit enter for results.
			search.getEditor().getEditorComponent().addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					// don't fire an event on backspace or delete
					if(e.getKeyCode() == 8 || e.getKeyCode() == 127 || e.getKeyCode() == 17)
						return;
//					search.actionPerformed(new ActionEvent(search, ));
					onSearch();
					redraw();
//					search.getEditor().getEditorComponent()
				}
			});
		}

		/*
		 * next, make the top bar itself and arrange everything inside of it.
		 */

		// almost any component (JPanel, JFrame, etc.) that contains other
		// components inside it needs a LayoutManager to be useful, these do
		// exactly what you expect. three common LayoutManagers are the BoxLayout,
		// GridLayout, and BorderLayout. BoxLayout, contrary to its name, places
		// components in either a row (LINE_AXIS) or a column (PAGE_AXIS).
		// GridLayout is self-describing. BorderLayout puts a single component
		// on the north, south, east, and west sides of the outer component, as
		// well as one in the centre. google for more information.
		controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.LINE_AXIS));

		// make an empty border so the components aren't right up against the
		// frame edge.
		Border edge = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		controls.setBorder(edge);

		JPanel loadquit = new JPanel();
		loadquit.setLayout(new GridLayout(2, 1));
		// manually set a fixed size for the panel containing the load and quit
		// buttons (doesn't change with window resize).
		loadquit.setMaximumSize(new Dimension(50, 100));
		loadquit.add(load);
		loadquit.add(quit);
		controls.add(loadquit);


		// rigid areas are invisible components that can be used to space
		// components out.
		controls.add(Box.createRigidArea(new Dimension(15, 0)));

		JPanel navigation = new JPanel();
		navigation.setMaximumSize(new Dimension(150, 60));
		navigation.setLayout(new GridLayout(2, 3));
		navigation.add(out);
		navigation.add(north);
		navigation.add(in);
		navigation.add(west);
		navigation.add(south);
		navigation.add(east);
		controls.add(navigation);
		controls.add(Box.createRigidArea(new Dimension(15, 0)));

		//Pathfinding options
		JLabel pathOptionsLabel = new JLabel("<html><div style='text-align: justify;'>Minimise:</div></html>");
		String[] options = new String[]{"Distance", "Time"};
		minimiseValue = new JComboBox<>(options);

		JPanel pathOptionsPanel = new JPanel();
		pathOptionsPanel.setLayout(new GridLayout(2, 1));
		pathOptionsPanel.setMaximumSize(new Dimension(50, 100));
		pathOptionsPanel.add(pathOptionsLabel);
		pathOptionsPanel.add(minimiseValue);
		controls.add(pathOptionsPanel);


		minimiseValue.addActionListener((e) ->{
			this.onSetMinimiseValue(minimiseValue.getItemAt(0).equals("Time"));
		});

		//Create pathfinding controls
		JButton pathStart = new JButton("Start Node");
		JButton pathEnd = new JButton("End Node");

		pathStart.addActionListener((e)->onSetStart());
		pathEnd.addActionListener((e)->onSetEnd());

		startText = new JTextField();
		startText.setEditable(false);

		endText = new JTextField();
		endText.setEditable(false);

		JPanel pathpanel = new JPanel();
		pathpanel.setLayout(new GridLayout(2, 2));
		pathpanel.setMaximumSize(new Dimension(100, 100));
		pathpanel.add(pathStart);
		pathpanel.add(startText);
		pathpanel.add(pathEnd);
		pathpanel.add(endText);
		controls.add(pathpanel);

		JPanel APPanel = new JPanel();
		APPanel.setLayout(new GridLayout(2, 1));
		APPanel.setMaximumSize(new Dimension(100, 100));

		JButton calcAP = new JButton("Calculate APs");
		calcAP.addActionListener((e)->calculateAPs());
		JButton calcAllAps = new JButton("Calculate all APs");
		calcAllAps.addActionListener((e)->calculateAllAps());

		JButton resetAP = new JButton("Reset APs");
		resetAP.addActionListener((e)->resetAPs());


		APPanel.add(calcAP);
		APPanel.add(resetAP);
		APPanel.add(calcAllAps);

		controls.add(APPanel);

		JPanel togglePanel = new JPanel();
		togglePanel.setLayout(new GridLayout(2, 2));
		togglePanel.setMaximumSize(new Dimension(100, 100));

		JButton tAPs = new JButton("Render APs");
		tAPs.setBackground(Color.GREEN);
		tAPs.addActionListener((e)->{
			tAPs.setBackground(renderAPs() ? Color.GREEN : Color.RED);
		});
		togglePanel.add(tAPs);

		JButton tPoly = new JButton("Render Polygons");
		tPoly.setBackground(Color.GREEN);
		tPoly.addActionListener((e)->{
			tPoly.setBackground(renderPolygons() ? Color.GREEN : Color.RED);
		});
		togglePanel.add(tPoly);

		JButton tQuadNodes = new JButton("Render QuadNodes");
		tQuadNodes.setBackground(Color.RED);
		tQuadNodes.addActionListener((e)->{
			tQuadNodes.setBackground(renderQuadNodes() ? Color.GREEN : Color.RED);
		});
		togglePanel.add(tQuadNodes);

		controls.add(togglePanel);

		JPanel epicPanel = new JPanel();
		epicPanel.setLayout(new GridLayout(1, 1));
		epicPanel.setMaximumSize(new Dimension(100, 100));
		JButton epicBtn = new JButton("Epic Button");

		epicBtn.addActionListener((e)->{
			epicBtn.setBackground(new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));

		});

		epicPanel.add(epicBtn);

//		controls.add(epicPanel);




		// glue is another invisible component that grows to take up all the
		// space it can on resize.
		controls.add(Box.createHorizontalGlue());

		controls.add(new JLabel("Search"));
		controls.add(Box.createRigidArea(new Dimension(5, 0)));
		controls.add(search);



		/*
		 * then make the drawing canvas, which is really just a boring old
		 * JComponent with the paintComponent method overridden to paint
		 * whatever we like. this is the easiest way to do drawing.
		 */

		drawing = new JComponent(){
			protected void paintComponent(Graphics g){
				redraw(g);
			}
		};
		drawing.setPreferredSize(new Dimension(DEFAULT_DRAWING_WIDTH,
				DEFAULT_DRAWING_HEIGHT));
		// this prevents a bug where the component won't be
		// drawn until it is resized.
		drawing.setVisible(true);

		drawing.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				onRelease(e);
				redraw();
			}

			@Override
			public void mousePressed(MouseEvent e){
				onPress(e);
				redraw();
			}
		});

		drawing.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				onDrag(e);
				redraw();
			}
		});

		drawing.addMouseWheelListener(new MouseAdapter(){
			public void mouseWheelMoved(MouseWheelEvent e){
				onScroll(e);
				redraw();
			}
		});

		/*
		 * then make the JTextArea that goes down the bottom. we put this in a
		 * JScrollPane to get scroll bars when necessary.
		 */

		textOutputArea = new JTextArea(TEXT_OUTPUT_ROWS, 0);
		textOutputArea.setLineWrap(true);
		textOutputArea.setWrapStyleWord(true); // pretty line wrap.
		textOutputArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textOutputArea);
		// these two lines make the JScrollPane always scroll to the bottom when
		// text is appended to the JTextArea.
		DefaultCaret caret = (DefaultCaret) textOutputArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		/*
		 * finally, make the outer JFrame and put it all together. this is more
		 * complicated than it could be, as we put the drawing and text output
		 * components inside a JSplitPane so they can be resized by the user.
		 * the JScrollPane and the top bar are then added to the frame.
		 */

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerSize(5); // make the selectable area smaller
		split.setContinuousLayout(true); // make the panes resize nicely
		split.setResizeWeight(1); // always give extra space to drawings
		// JSplitPanes have a default border that makes an ugly row of pixels at
		// the top, remove it.
		split.setBorder(BorderFactory.createEmptyBorder());
		split.setTopComponent(drawing);
		split.setBottomComponent(scroll);

		frame = new JFrame("Mapper");
		// this makes the program actually quit when the frame's close button is
		// pressed.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(controls, BorderLayout.NORTH);
		frame.add(split, BorderLayout.CENTER);


		// always do these two things last, in this order.
		frame.pack();
		frame.setVisible(true);
	}
}

// code for COMP261 assignments