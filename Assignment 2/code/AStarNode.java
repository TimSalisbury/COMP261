import java.util.Objects;

public class AStarNode implements Comparable{

    public Node node;
    public double hCost;
    public double gCost;

    public AStarNode(Node node) {
        this.node = node;
    }

    @Override
    public int compareTo(Object o) {
        AStarNode other = (AStarNode)o;
        if (other.hCost + other.gCost == hCost + gCost) return 0;
        return other.hCost + other.gCost > hCost + gCost ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AStarNode aStarNode = (AStarNode) o;
        return Objects.equals(node, aStarNode.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}
