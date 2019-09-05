import java.util.*;

/**
 * A class to manage and carry out A* Searches
 */
public class AStarManager {

    private static Node start;
    private static Node end;
    private static boolean miniseTime = false;

    /**
     * Preforms a search for the shortest route between the start and end node using the
     * A* Pathfinding algorithm
     * @return      The path calculated to be the shortest
     */
    public static List<Node> pathfind(boolean miniseTime){
        if(start == null || end == null) return new ArrayList<>();
        AStarManager.miniseTime = miniseTime;
        Set<Node> searched = new HashSet<>();
        Queue<AStarNode> frontier = new PriorityQueue<>();
        Map<Node, Node> path = new HashMap<>();     //Maps node to parent node
        path.put(start, null);
        frontier.add(new AStarNode(start));

        while(!frontier.isEmpty()){
            AStarNode node = frontier.poll();

            if(node.node.equals(end)){
                return reconstructPath(path, end, start);
            }

            searched.add(node.node);
            for(Node neighbour : node.node.getOutgoingNodes()){
                if(!searched.contains(neighbour)){
                    if(node.node.isRestricted(path.get(node.node), neighbour)) continue;
                    AStarNode sNode = new AStarNode(neighbour);
                    sNode.gCost = node.gCost + calculateGCost(node.node, neighbour);
                    sNode.hCost = calculateHeuristic(neighbour, end);
                    frontier.offer(sNode);
                    path.put(neighbour, node.node);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Reconstructs the path provided by A*
     * @param pathMap   The mapping of node to parent
     * @param end       The end node of the path
     * @param start     The start node of the path
     * @return          A list of nodes that the path is
     */
    private static List<Node> reconstructPath(Map<Node, Node> pathMap, Node end, Node start){
        List<Node> path = new ArrayList<>();
        Stack<Node> pathStack = new Stack<>();
        Node node = end;
        while(node != null){
            pathStack.push(node);
            node = pathMap.get(node);
        }

        path.add(start);
        while(!pathStack.isEmpty()){
            path.add(pathStack.pop());
        }

        return path;
    }

    /**
     * Sets the start of the path
     * @param start The start of the path
     */
    public static void setStart(Node start) {
        AStarManager.start = start;
    }

    /**
     * Sets the end of the path
     * @param end The end of the path
     */
    public static void setEnd(Node end) {
        AStarManager.end = end;
    }


    /**
     * Calculates the heuristic value for two selected nodes based on our configuration of A*
     * @param start The start node
     * @param end   The end node
     * @return      The heuristic value calculated
     */
    private static double calculateHeuristic(Node start, Node end){
        if(miniseTime){
            return start.getLocation().distance(end.getLocation()) / Main.averageSpeed;
        }else{
            return start.getLocation().distance(end.getLocation());
        }
    }


    /**
     * Calculates the GCost value for any two nodes based on our configuration of A*
     * @param node          The start node
     * @param neighbour     The end node
     * @return              The calculated GCost
     */
    private static double calculateGCost(Node node, Node neighbour){
        Segment segment = node.getOutgoingSegment(neighbour);

        if(miniseTime){
            return (segment.getLength() / (segment.getRoad().getSpeedLimit() + segment.getRoad().getRoadClass().getIncreasedSpeed())) + (segment.getEnd().isIntersection() ? 0.05 : 0);
        }else{
            return segment.getLength();
        }
    }

    /**
     * Sets our configuration of A*
     * @param value Configuration value
     */
    public static void setMinimiseTime(boolean value){
        miniseTime = value;
    }

    public static boolean isStartOrEnd(Node node){
        if (start == null && end == null) return false;
        if(start == null) return end.equals(node);
        else if(end == null) return start.equals(node);
        return start.equals(node) || end.equals(node);
    }
}