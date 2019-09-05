import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class VariableNode implements RobotValueNode{

    static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");


    private String text;

    public VariableNode(String text) {
        this.text = text;
    }

    @Override
    public int evaluate(Robot s) {
        return s.getVariable(this);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableNode that = (VariableNode) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
