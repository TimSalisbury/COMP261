import java.util.*;

public class ArticulationPointManager {

    private static Set<Node> APS = new HashSet<>();

    /**
     * The entry point of the Articulation Points Algorithm.
     * @param root The root that we are going to expand from
     * @return  The set of Articulation points calculated.
     */
    public static Set<Node> getAPS(Node root){
        APS.clear();
        root.setDepth(0);
        int numSubTrees = 0;
        for(Node neighbour : root.getNeighbours()){
            neighbour.setParent(root);
            if(neighbour.getDepth() == Integer.MAX_VALUE){
                calculateArticulationPoints(neighbour, root);
                numSubTrees++;
            }
        }
        if(numSubTrees > 1){
            APS.add(root);
        }
        return APS;
    }

    public static Set<Node> getAllAps(Collection<Node> nodes){
        APS.clear();
        for(Node root : nodes){
            root.setDepth(0);
            int numSubTrees = 0;
            for(Node neighbour : root.getNeighbours()){
                if(neighbour.getDepth() == Integer.MAX_VALUE){
                    neighbour.setParent(root);
                    calculateArticulationPoints(neighbour, root);
                    numSubTrees++;
                }
            }
            if(numSubTrees > 1){
                APS.add(root);
            }
        }

        return APS;
    }

    /**
     * Iterative algorithm for calculating ArticulationPoints
     * @param firstNode The first child node
     * @param root      The root node
     */
    private static void calculateArticulationPoints(Node firstNode, Node root){
        Stack<Node> frontier = new Stack<>();
        firstNode.setParent(root);
        firstNode.setDepth(0);
        frontier.push(firstNode);
        while(!frontier.isEmpty()){
            Node node = frontier.peek();
            if(node.getDepth() == Integer.MAX_VALUE){   //If this node is unvisited
                node.setDepth(0);
                node.setReachBack(0);
                node.removeParent();
            }else if(!node.getSearchChildren().isEmpty()){    //If we have child nodes to process
                Node child = node.getSearchChild();
                if(child.getDepth() < Integer.MAX_VALUE){       //If the child node has been visited before
                    node.setReachBack(Math.min(child.getDepth(), node.getReachBack()));
                }else{
                    child.setDepth(node.getDepth() + 1);
                    child.setParent(node);
                    frontier.push(child);
                }
            }else{
                if(!node.equals(firstNode)){
                    Node parent = node.getParent();
                    parent.setReachBack(Math.min(node.getReachBack(), parent.getReachBack()));
                    if(node.getReachBack() >= parent.getDepth()){   //If our reachback is larger than our parents depth then we know this is an articulation point.
                        APS.add(parent);
                    }
                }

                frontier.pop();
            }
        }
    }
}