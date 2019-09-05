package renderer;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PolygonHelper {

    /**
     * Calculates a BoundingBox of the scene
     * @param scene The scene to calculate a bounding box for
     * @return      The bounding box
     */
    @SuppressWarnings("Duplicates")
    public static Rectangle calculateBox(Scene scene){
        float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY,
                minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;

        for(Scene.Polygon polygon : scene.getPolygons()){
            Vector3D[] vertices = polygon.getVertices();
            for(Vector3D v : vertices){
                if(v.x < minX) 			minX = v.x;
                else if(v.x > maxX) 	maxX = v.x;

                if(v.y < minY) 			minY = v.y;
                else if(v.y > maxY) 	maxY = v.y;
            }
        }

        float width = maxX - minX;
        float height = maxY - minY;

        return new Rectangle(Math.round(minX), Math.round(minY), Math.round(width), Math.round(height));
    }

    /**
     * Calculates the normal of a polygon
     * @param poly  Polygon to calculate for
     * @return      The normal
     */
    public static Vector3D calculateNormal(Scene.Polygon poly){
        Vector3D[] vertices = poly.getVertices();
        Vector3D AB = vertices[1].minus(vertices[0]);
        Vector3D BC = vertices[2].minus(vertices[1]);

        return AB.crossProduct(BC);
    }

    /**
     * Calulate the average of all normals connected to a vertex
     * @param scene     The scene to calculate for
     * @param vector    The vertex to calculate for
     * @return          The average of all connecting polygon normals
     */
    public static Vector3D calculateAverageNormal(Scene scene, Vector3D vector) {
        Vector3D average = new Vector3D(0, 0, 0);
        Scene.Polygon[] neighbours = polygonsAtVertex(scene, vector);
        for (Scene.Polygon poly : neighbours) {
            average = average.plus(PolygonHelper.calculateNormal(poly));
        }
        return average.divide(neighbours.length);
    }

    /**
     * Gets an array of all polygons containing the vertex specified
     * @param scene     The scene to calculate for
     * @param vector    The vector to calculate for
     * @return          All of the neighbouring polygons
     */
    public static Scene.Polygon[] polygonsAtVertex(Scene scene, Vector3D vector) {
        Set<Scene.Polygon> polygons = new HashSet<>();
        for (Scene.Polygon polygon : scene.getPolygons()) {
            if (polygon.containsVertex(vector)) {
                polygons.add(polygon);
            }

        }
        return polygons.toArray(new Scene.Polygon[0]);
    }
}
