package renderer;

import java.awt.*;

public class ColorHelper {
    /**
     * Calculates the light intensity of the provided colour [0,1)
     * @param colour The colour to calculate intensity of
     * @return The intensity values of each of the colour components
     */
    public static float[] calculateLightIntensities(Color colour) {
        return new float[]{colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f};
    }

    /**
     * Multiplies a float array by a value, the float array represents a colour
     * @param colour The colour values, represented by a float array
     * @param value The value to multiple the colour by
     * @return      The multipled colour
     */
    public static float[] multiplyColor(float[] colour, float value){
        return new float[]{colour[0] * value, colour[1] * value, colour[2] * value};
    }

    /**
     * Adds two float arrays together, each array represents a colour
     * @param colourA The first colour value represented by a float array
     * @param colourB The second colour value represented by a float array
     * @return The new colour represented by a float array
     */
    public static float[] addColor(float[] colourA, float[] colourB){
        return new float[]{colourA[0] + colourB[0], colourA[1] + colourB[1], colourA[2] + colourB[2]};
    }

    /**
     * Divides a float array by a value, the float array represents a colour
     * @param colour The colour values
     * @param value The value to divide by
     * @return The new colour after division
     */
    public static Color divideColor(float[] colour, float value){
        return new Color((int)MathUtil.constrain(colour[0]/value, 0, 255), (int)MathUtil.constrain(colour[1]/value, 0, 255),
                (int)MathUtil.constrain(colour[2]/value, 0, 255));
    }

    /**
     * Converts a colour to a float array
     * @param colour The colour to convert
     * @return The float array representing the colour
     */
    public static float[] convertColor(Color colour){
        return new float[]{colour.getRed(), colour.getGreen(), colour.getBlue()};
    }
}
