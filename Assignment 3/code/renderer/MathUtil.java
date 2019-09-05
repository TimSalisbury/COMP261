package renderer;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathUtil{
    /**
     * Constrains x to the provided min and max values
     * @param value     Value to constrain
     * @param min   Minimum constraining value
     * @param max   Maximum constraining value
     * @return      The constrained value
     */
    public static float constrain(float value, float min, float max){
        return value <= min ? min : value >= max ? max : value;
    }

    /**
     * Constrains x to the provided min and max values
     * @param value     Value to constrain
     * @param min   Minimum constraining value
     * @param max   Maximum constraining value
     * @return      The constrained value
     */
    public static int constrain(int value, int min, int max){
        return value <= min ? min : value >= max ? max : value;
    }

    /**
     * Rounds the provided double to the amount of decimal places specified
     * @param value             The number to round
     * @param decimalPlaces Number of decimal places we round to
     * @return              The rounded number
     */
    public static double round(double value, int decimalPlaces){
        StringBuilder format = new StringBuilder("##.");
        for(int i = 0; i < decimalPlaces; i++)
            format.append("#");
        DecimalFormat df = new DecimalFormat(format.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.valueOf(df.format(value));
    }

    /**
     * Calculates a random number within the given range
     * @param min   The minimum value of the random number
     * @param max   The maximum value of the random number
     * @return      The random number calculated
     */
    public static float randomNumber(float min, float max){
         return (float)((Math.random() * (max - min) + min));
    }

    /**
     * Calculates the distance between two points
     * @param x1    First point x
     * @param y1    First point y
     * @param x2    Second point x
     * @param y2    Second point y
     * @return      The distance between the two points
     */
    public static double distance(double x1, double y1, double x2, double y2){
        double dY = Math.abs(y2 - y1);
        double dX = Math.abs(x2 - x1);

        return Math.hypot(dY, dX);
    }

    /**
     * Calculates and returns whether or not a value lies inbetween two other values (inclusive)
     * @param value         The value we are testing
     * @param start     The start of the range we are testing against
     * @param end       The end of the range we are testing against
     * @return          Whether or not x lies inbetween start and end
     */
    public static boolean valueWithin(double value, double start, double end){
        value = round(value, 5);
        start = round(start, 5);
        end = round(end, 5);
        return value >= start && value <= end;
    }
}
