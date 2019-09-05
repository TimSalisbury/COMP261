/**
 * Used for defining the road type of a road
 */
public enum Class{
    RESIDENTIAL (0),
    COLLECTOR (5),
    ARTERIAL (10),
    PRINCIPAL_HW (15),
    MAJOR_HW (20);

    private int increasedSpeed;

    Class(int increasedSpeed){
        this.increasedSpeed = increasedSpeed;
    }

    public int getIncreasedSpeed() {
        return increasedSpeed;
    }
}
