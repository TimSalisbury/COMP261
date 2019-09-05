package renderer;

import java.awt.*;
import java.util.*;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {


	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		return PolygonHelper.calculateNormal(poly).z > 0;
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirections
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColours
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	@SuppressWarnings("Duplicates")
	public static Color getShading(Polygon poly, List<Vector3D> lightDirections, List<Color> lightColours, Color ambientLight) {
		float[] AL = ColorHelper.calculateLightIntensities(ambientLight);	//Ambient light intensities
		float[][] IL = new float[lightDirections.size()][3];				//All incident light intensities

		for(int i = 0; i < lightDirections.size(); i++){	//Calculates all of the incident light intensities
			IL[i] = ColorHelper.calculateLightIntensities(lightColours.get(i));
		}

		double[] thetas = new double[lightDirections.size()];	//All of the theta values of the incident lights

		Vector3D normal = PolygonHelper.calculateNormal(poly);

		for(int i = 0; i < lightDirections.size(); i++){ //Calculates all of the theta values
			thetas[i] = normal.cosTheta(lightDirections.get(i));
		}

		float[] reflectance = new float[]{poly.getReflectance().getRed(), poly.getReflectance().getGreen(), poly.getReflectance().getBlue()};

		float[] lightValues = new float[3];

		for(int i = 0; i < 3; i++){	//Calculates all of the colour values of the shading based off the theta values, ambient light intensities, and incident light intensities
			float sourceValue = 0;
			for(int j = 0; j < lightDirections.size(); j++){
				sourceValue += IL[j][i] * Math.max(0, thetas[j]);
			}
			lightValues[i] = MathUtil.constrain(((AL[i] + sourceValue) * reflectance[i]), 0, 254);
		}

		return new Color((int)lightValues[0], (int)lightValues[1], (int)lightValues[2]);
	}

	/**
	 * Calculates the colour of at the point of a vertex, this is used for doing Gouraud shading. Only difference
	 * between this one and the one above is that the normal is an average of all neighbouring polygons normals
	 * @param vector	The vertex to get the colour at
	 * @param poly		Polygon we are calculating for
	 * @param scene		The scene
	 * @param ambientLight	Ambient light
	 * @return	The colour at the vertex
	 */
	@SuppressWarnings("Duplicates")
	public static Color getColorAtVertex(Vector3D vector, Polygon poly, Scene scene, Color ambientLight){
		List<Vector3D> lightDirections = scene.getLights();
		List<Color> lightColours = scene.getLightColours();
		float[] AL = ColorHelper.calculateLightIntensities(ambientLight);
		float[][] IL = new float[lightDirections.size()][3];

		for(int i = 0; i < lightDirections.size(); i++){
			IL[i] = ColorHelper.calculateLightIntensities(lightColours.get(i));
		}

		double[] thetas = new double[lightDirections.size()];

		Vector3D normal = PolygonHelper.calculateAverageNormal(scene, vector);

		for(int i = 0; i < lightDirections.size(); i++){
			thetas[i] = normal.cosTheta(lightDirections.get(i));
		}

		float[] reflectance = new float[]{poly.getReflectance().getRed(), poly.getReflectance().getGreen(), poly.getReflectance().getBlue()};

		float[] lightValues = new float[3];

		for(int i = 0; i < 3; i++){
			float sourceValue = 0;
			for(int j = 0; j < lightDirections.size(); j++){
				sourceValue += IL[j][i] * Math.max(0, thetas[j]);
			}
			lightValues[i] = MathUtil.constrain(((AL[i] + sourceValue) * reflectance[i]), 0, 254);
		}

		return new Color((int)lightValues[0], (int)lightValues[1], (int)lightValues[2]);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
k	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		if(scene == null) return null;
		Transform xTrans = Transform.newXRotation(xRot);
		Transform yTrans = Transform.newYRotation(yRot);

		Transform combo = yTrans.compose(xTrans);

		List<Polygon> polygons = new ArrayList<>(scene.getPolygons());

		for(Scene.Polygon polygon : polygons){	//Rotates all of the vertices of each polygon
			for(int i = 0; i < 3; i++){
				polygon.getVertices()[i] = combo.multiply(polygon.getVertices()[i]);
			}
		}

		List<Vector3D> lightPos = new ArrayList<>(scene.getLights());
		for(int i = 0; i < scene.getLights().size(); i++){	//Rotates all of the light sources
			lightPos.set(i, combo.multiply(lightPos.get(i)));
		}



		return new Scene(polygons, lightPos, scene.getLightColours());
	}

	/**
	 * Translates the scene into the center of the viewing window
	 * @param scene	The scene to translate
	 * @return		The new scene after translation
	 */
	@SuppressWarnings("Duplicates")
	public static Scene translateScene(Scene scene) {

		Rectangle bb = PolygonHelper.calculateBox(scene);

		Transform translation = Transform.newTranslation(-(bb.x + (float)bb.width/2) + GUI.CANVAS_WIDTH/2f, -(bb.y + (float)bb.height/2) + GUI.CANVAS_HEIGHT/2f, 0);

		List<Polygon> polygons = new ArrayList<>(scene.getPolygons());

		for(Polygon polygon : polygons){	//Translates all of the vertices of each polygon
			for(int i = 0; i < 3; i++){
				polygon.getVertices()[i] = translation.multiply(polygon.getVertices()[i]);
			}
		}

		return new Scene(polygons, scene.getLights(), scene.getLightColours());	//Each light source is at a distance of infinity, hence why we don't need to translate them
	}

	/**
	 * Scales the scene so that atleast one of the x or y axes of the scene is 75% of the viewing window
	 * @param scene	The scene to scale
	 * @return		The new scene to scale
	 */
	@SuppressWarnings("Duplicates")
	public static Scene scaleScene(Scene scene) {
		Rectangle bb = PolygonHelper.calculateBox(scene);

		float scale;
		if(bb.height > bb.width){
			scale = (GUI.CANVAS_HEIGHT * 0.75f)/bb.height;
		}else{
			scale = (GUI.CANVAS_WIDTH * 0.75f)/bb.width;
		}

		Transform scaler = Transform.newScale(scale, scale, scale);

		List<Polygon> polygons = new ArrayList<>(scene.getPolygons());

		for(Polygon polygon : polygons){	//Scales all of the vertices of each of the polygons
			for(int i = 0; i < 3; i++){
				polygon.getVertices()[i] = scaler.multiply(polygon.getVertices()[i]);
			}
		}

		return new Scene(polygons, scene.getLights(), scene.getLightColours()); //Light sources are at a distance of infinity, so we do not need to scale them
	}

	/**
	 * Calculates an edge list from the provided polygon
	 * @param poly	The polygon
	 * @return		The EdgeList calculated
	 */
	@SuppressWarnings("Duplicates")
	public static EdgeList computeEdgeList(Polygon poly) {
		Vector3D[] vertices = Arrays.copyOf(poly.getVertices(), 3);
		Arrays.sort(vertices, Comparator.comparing(Vector3D::getY));		//Sort from smallest Y to largest Y

		Vector3D[][] edges = new Vector3D[][]{
			new Vector3D[]{vertices[0], vertices[2]},	//This edge will the longest, as the first vertex has the smallest Y and the last vertex has the largest Y
			new Vector3D[]{vertices[0], vertices[1]},
			new Vector3D[]{vertices[1], vertices[2]}
		};

		Vector3D vectorA = vertices[2].minus(vertices[0]);
		Vector3D vectorB = vertices[1].minus(vertices[0]);

		boolean updatingLeft = (vectorA.x * -vectorB.y + vectorA.y * vectorB.x) > 0;	//Calculates whether or not the longest (or first) edge is to the left of the other two

		EdgeList edgeList = new EdgeList(Math.round(vertices[0].y), Math.round(vertices[2].y));

		for(int i = 0; i < edges.length; i++){
			Vector3D[] edge = edges[i];

			Vector3D pointA = edge[0];
			Vector3D pointB = edge[1];

			float xSlope = (pointB.x - pointA.x)/(pointB.y - pointA.y);
			float zSlope = (pointB.z - pointA.z)/(pointB.y - pointA.y);

			if(pointB.y - pointA.y <= 1){		//Difference in y is so small we don't worry about it
				xSlope = 0;
				zSlope = 0;
			}

			float x = pointA.x;
			float z = pointA.z;

			for(int y = Math.round(pointA.y); y <= Math.round(pointB.y); y++){
				if(updatingLeft){
					edgeList.addLeft(y, x, z, null);
				}else{
					edgeList.addRight(y, x, z, null);
				}

				x += xSlope;
				z += zSlope;
			}

			if(i == 0){
				updatingLeft = !updatingLeft;	//After calculating the side with the longest edge we need to calculate the other two
			}
		}

		return edgeList;
	}

	/**
	 * Calculates an EdgeList from the provided polygon, this function also calculates the colours for the polygon based
	 * off Gouraud shading algorithm
	 * @param poly		The polygon to calculate for
	 * @param scene		The scene we are calculating for
	 * @param ambient	The ambient light of the scene
	 * @return			The EdgeList calculated
	 */
	@SuppressWarnings("Duplicates")
	public static EdgeList computeEdgeList(Polygon poly, Scene scene, Color ambient) {
		Vector3D[] vertices = Arrays.copyOf(poly.getVertices(), 3);
		Arrays.sort(vertices, Comparator.comparing(Vector3D::getY));

		Map<Vector3D, float[]> vertexColors = new HashMap<>();		//Map vertices to colours calculated at them
		vertexColors.put(vertices[0], ColorHelper.convertColor(getColorAtVertex(vertices[0], poly, scene, ambient)));
		vertexColors.put(vertices[1], ColorHelper.convertColor(getColorAtVertex(vertices[1], poly, scene, ambient)));
		vertexColors.put(vertices[2], ColorHelper.convertColor(getColorAtVertex(vertices[2], poly, scene, ambient)));

		Vector3D[][] edges = new Vector3D[][]{
				new Vector3D[]{vertices[0], vertices[2]},
				new Vector3D[]{vertices[0], vertices[1]},
				new Vector3D[]{vertices[1], vertices[2]}
		};

		Vector3D longMid = vertices[2].minus(vertices[0]);
		Vector3D shortMid = vertices[1].minus(vertices[0]);

		boolean updatingLeft = (longMid.x * -shortMid.y + longMid.y * shortMid.x) > 0;

		EdgeList edgeList = new EdgeList(Math.round(vertices[0].y), Math.round(vertices[2].y));

		for(int i = 0; i < edges.length; i++){
			Vector3D[] edge = edges[i];

			Vector3D pointA = edge[0];
			Vector3D pointB = edge[1];

			float xSlope = (pointB.x - pointA.x)/(pointB.y - pointA.y);
			float zSlope = (pointB.z - pointA.z)/(pointB.y - pointA.y);

			if(pointB.y - pointA.y <= 1){
				xSlope = 0;
				zSlope = 0;
			}

			float x = pointA.x;
			float z = pointA.z;

			for(int y = Math.round(pointA.y); y <= Math.round(pointB.y); y++){
				float lengthOne = (float)y - pointA.y;
				float lengthTwo = pointB.y - (float)y;

				float[] top = ColorHelper.addColor(ColorHelper.multiplyColor(vertexColors.get(pointA), lengthTwo), ColorHelper.multiplyColor(vertexColors.get(pointB), lengthOne));
				Color color = ColorHelper.divideColor(top, pointB.y - pointA.y);

				if(updatingLeft){
					edgeList.addLeft(y, x, z, color);
				}else{
					edgeList.addRight(y, x, z, color);
				}

				x += xSlope;
				z += zSlope;
			}

			if(i == 0){
				updatingLeft = !updatingLeft;
			}
		}

		for(int i = 0; i < edges.length; i++){	//After calculating both left and right side of the polygon we need to go through and calculate the colour values
			Vector3D[] edge = edges[i];

			Vector3D pointA = edge[0];
			Vector3D pointB = edge[1];

			for(int y = Math.round(pointA.y); y <= Math.round(pointB.y); y++){
				edgeList.initalizeColorRow(y);
				for(int x = Math.round(edgeList.getLeftX(y)); x <= Math.round(edgeList.getRightX(y)); x++){
					float lengthOne = x - edgeList.getLeftX(y);
					float lengthTwo = edgeList.getRightX(y) - x;

					float[] top = ColorHelper.addColor(ColorHelper.multiplyColor(ColorHelper.convertColor(edgeList.getLeftColor(y)), lengthTwo),
							ColorHelper.multiplyColor(ColorHelper.convertColor(edgeList.getRightColor(y)), lengthOne));

					Color color = ColorHelper.divideColor(top, edgeList.getRightX(y) - edgeList.getLeftX(y));

					edgeList.addColor(x, y, color);
				}
			}
		}

		return edgeList;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param edge
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList edge, Color polyColor, boolean shading) {
		for(int y = edge.getStartY(); y <= edge.getEndY(); y++){
			if(y < 0){
				continue;
			}

			if(y >= GUI.CANVAS_HEIGHT){
				return;
			}
			float slope = (edge.getRightZ(y) - edge.getLeftZ(y))/(edge.getRightX(y) - edge.getLeftX(y));

			int x = Math.round(edge.getLeftX(y));
			float z = edge.getLeftZ(y) + slope * (x - edge.getLeftX(y));

			while(x <= Math.round(edge.getRightX(y)) && x < GUI.CANVAS_WIDTH){
				if(x >= 0 && z < zdepth[x][y]){
					zbuffer[x][y] = shading ? edge.getColor(x, y) : polyColor;
					zdepth[x][y] = z;
				}
				z += slope;
				x++;
			}
		}
	}
}

// code for comp261 assignments