import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APNode{
    public Node node;
    public int depth;
    public int reachBack;
    public Node parent;
    public List<Node> children;

    public APNode(Node node, int depth, Node parent, int reachback) {
        this.node = node;
        this.depth = depth;
        this.parent = parent;
        this.reachBack = reachback;
        this.children = new ArrayList<>(node.getNeighbours());
    }

    public Node getChild(){
        return children.remove(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APNode apNode = (APNode) o;
        return Objects.equals(node, apNode.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}
