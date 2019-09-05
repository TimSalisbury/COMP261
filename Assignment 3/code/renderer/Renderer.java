package renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends GUI {

    private Scene mainScene;

    private boolean shading = false;


    /**
     * Loads a scene from the provided file, the scene contains polygons and a light source
     * @param file Polygon data file
     */
    @Override
    protected void onLoad(File file) {
        List<Scene.Polygon> polygons = new ArrayList<>();

        BufferedReader fileIn;
        try {
            fileIn = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
            showDialog("Failed to read Polygon File");
            return;
        }

        try {
            String line = fileIn.readLine();
            int numPolygons = Integer.valueOf(line);
            for (int i = 0; i < numPolygons; i++) {
                String[] values = fileIn.readLine().split(",");
                int[] colours = new int[3];
                float[] points = new float[9];

                for (int j = 0; j < 3; j++) {
                    colours[j] = Integer.valueOf(values[j]);
                }

                for (int x = 0; x < 9; x++) {
                    points[x] = Float.valueOf(values[3 + x]);
                }

                polygons.add(new Scene.Polygon(points, colours));
            }

            line = fileIn.readLine();
            String[] values = line.split(",");
            Vector3D lightPos = new Vector3D(Float.valueOf(values[0]), Float.valueOf(values[1]), Float.valueOf(values[2]));

            mainScene = new Scene(polygons, lightPos);
        } catch (IOException e) {
            e.printStackTrace();
            showDialog("Failed to read Polygon data");
        }
    }

    /**
     * Toggles Gouraud shading of the scene
     * @return Whether gouraud shading is enabled or not
     */
    @Override
    protected boolean shadingToggle() {
        shading = !shading;
        return shading;
    }

    /**
     * Processes key events by rotating the view point around the scene
     * @param ev The key event
     */
    @Override
    protected void onKeyPress(KeyEvent ev) {
        if (ev.getKeyCode() == KeyEvent.VK_A || ev.getKeyCode() == KeyEvent.VK_LEFT) {
            mainScene = Pipeline.rotateScene(mainScene, 0, -0.1f);
        } else if (ev.getKeyCode() == KeyEvent.VK_D || ev.getKeyCode() == KeyEvent.VK_RIGHT) {
            mainScene = Pipeline.rotateScene(mainScene, 0, 0.1f);
        } else if (ev.getKeyCode() == KeyEvent.VK_W || ev.getKeyCode() == KeyEvent.VK_UP) {
            mainScene = Pipeline.rotateScene(mainScene, 0.1f, 0);
        } else if (ev.getKeyCode() == KeyEvent.VK_S || ev.getKeyCode() == KeyEvent.VK_DOWN) {
            mainScene = Pipeline.rotateScene(mainScene, -0.1f, 0);
        }
    }

    /**
     * Generates the image of the scene by processing each polygon through a z-buffer algorithm.
     * @return The buffered image to render to the screen
     */
    @Override
    protected BufferedImage render() {
        if (mainScene == null) return null;
        mainScene = Pipeline.scaleScene(mainScene);
        mainScene = Pipeline.translateScene(mainScene);
        Color[][] image = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
        float[][] zbuffer = new float[CANVAS_WIDTH][CANVAS_HEIGHT];

        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                zbuffer[x][y] = Float.POSITIVE_INFINITY;
                image[x][y] = Color.GRAY;
            }
        }

        for (Scene.Polygon polygon : mainScene.getPolygons()) {
            if (Pipeline.isHidden(polygon)) continue;   //Skip hidden polygons
            EdgeList list = shading ? Pipeline.computeEdgeList(polygon, mainScene, new Color(getAmbientLight()[0],  //If shading is not enabled use the old edgelist algorithm to save time
                    getAmbientLight()[1], getAmbientLight()[2])) : Pipeline.computeEdgeList(polygon);

            Color polyShading = shading ? null : Pipeline.getShading(polygon, mainScene.getLights(), mainScene.getLightColours(),   //If shading is enabled to calculate polygon colour to save time
                    new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]));
            Pipeline.computeZBuffer(image, zbuffer, list, polyShading, shading);
        }

        return convertBitmapToImage(image);
    }

    /**
     * Adds a new light source to the scene at a random location with the colour specified from the directional light sliders
     * @param color The colour derived from directional colour sliders
     */
    @Override
    protected void newLightSource(Color color) {
        Vector3D pos = new Vector3D(MathUtil.randomNumber(-1f, 1f),
                MathUtil.randomNumber(-1f, 1f),
                MathUtil.randomNumber(-1f, 1f));

        mainScene.addLight(pos, color);
        redraw();
    }

    /**
     * Removes a light source from the scene
     */
    @Override
    protected void removeLightSource() {
        if (!mainScene.getLights().isEmpty()) {
            mainScene.removeLight();
            redraw();
        }
    }

    /**
     * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
     * indexed by column then row and has imageHeight rows and imageWidth
     * columns. Note that image.setRGB requires x (col) and y (row) are given in
     * that order.
     */
    private BufferedImage convertBitmapToImage(Color[][] bitmap) {
        BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                image.setRGB(x, y, bitmap[x][y].getRGB());
            }
        }
        return image;
    }

    private void showDialog(String text) {
        JOptionPane.showMessageDialog(this.getFrame(), text);
    }

    public static void main(String[] args) {
        new Renderer();
    }
}

// code for comp261 assignments
