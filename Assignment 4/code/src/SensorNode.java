import java.util.regex.Pattern;

public class SensorNode implements RobotValueNode {

    enum Sensor {
        FUEL_LEFT("fuelLeft"),
        OPPONENT_LEFT_RIGHT("oppLR"),
        OPPONENT_FORWARD_BACK("oppFB"),
        NUMBER_BARRELS("numBarrels"),
        BARREL_LEFT_RIGHT("barrelLR"),
        BARREL_FORWARD_BACK("barrelFB"),
        WALL_DISTANCE("wallDist");

        private String text;

        Sensor(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }
    }

    static final Pattern SENSOR_PATTERN = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");

    private ExpressionNode relativeValue;
    private Sensor sensor;

    SensorNode(Sensor sensor) {
        this.sensor = sensor;
    }

    public SensorNode(ExpressionNode relativeValue, Sensor sensor) {
        this.relativeValue = relativeValue;
        this.sensor = sensor;
    }

    @Override
    public int evaluate(Robot robot) {
        switch (sensor){
            case FUEL_LEFT:
                return robot.getFuel();
            case OPPONENT_LEFT_RIGHT:
                return robot.getOpponentLR();
            case OPPONENT_FORWARD_BACK:
                return robot.getOpponentFB();
            case NUMBER_BARRELS:
                return robot.numBarrels();
            case BARREL_LEFT_RIGHT:
                if(relativeValue != null){
                    return robot.getBarrelLR(relativeValue.evaluate(robot));
                }
                return robot.getClosestBarrelLR();
            case BARREL_FORWARD_BACK:
                if(relativeValue != null){
                    return robot.getBarrelFB(relativeValue.evaluate(robot));
                }
                return robot.getClosestBarrelFB();
            case WALL_DISTANCE:
                return robot.getDistanceToWall();
        }

        throw new RobotInterruptedException();
    }

    @Override
    public String toString(){
        return sensor.toString();
    }
}
