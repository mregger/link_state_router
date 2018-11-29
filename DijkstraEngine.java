import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An implementation of Dijkstra's shortest path algorithm. It computes the shortest path (in distance)
 * to all nodes in the map. The output of the algorithm is the shortest distance from the start node 
 * to every other node, and the shortest path from the start node to every other.
 * <p>
 * Upon calling
 * {@link #execute(Node, Node)}, 
 * the results of the algorithm are made available by calling
 * {@link #getPredecessor(Node)}
 * and 
 * {@link #getShortestDistance(Node)}.
 * 
 * To get the shortest path between the node <var>destination</var> and
 * the source node after running the algorithm, one would do:
 * <pre>
 * List l = new ArrayList();
 *
 * for (Node node = destination; node != null; node = engine.getPredecessor(node))
 * {
 *     l.add(node);
 * }
 *
 * Collections.reverse(l);
 *
 * return l;
 * </pre>
 * 
 * @see #execute(Node, Node)
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: DijkstraEngine.java,v 1.4 2003/03/03 01:26:22 renaud Exp $
 */

public class DijkstraEngine
{
    /**
     * Infinity value for distances.
     */
    public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;
    
    /**
     * This comparator orders nodes according to their shortest distances,
     * in ascending fashion. If two nodes have the same shortest distance,
     * we compare the nodes themselves.
     */
    private final Comparator shortestDistanceComparator = new Comparator()
        {
            public int compare(Object left, Object right)
            {
                assert left instanceof Node && right instanceof Node : "invalid comparison";
                return compare((Node) left, (Node) right);
            }
            
            private int compare(Node left, Node right)
            {
                // note that this trick doesn't work for huge distances, close to Integer.MAX_VALUE
                int result = getShortestDistance(left) - getShortestDistance(right);
                
                return (result == 0) ? left.compareTo(right) : result;
            }
        };
    
    /**
     * The graph.
     */
    private final RoutesMap map;
    
    /**
     * The working set of nodes, kept ordered by shortest distance.
     */
    private final SortedSet unsettledNodes = new TreeSet(shortestDistanceComparator);
    
    /**
     * The set of nodes for which the shortest distance to the source
     * has been found.
     */
    private final Set settledNodes = new HashSet();
    
    /**
     * The currently known shortest distance for all nodes.
     */
    private final Map shortestDistances = new HashMap();

    /**
     * Predecessors list: maps a node to its predecessor in the spanning tree of
     * shortest paths.
     */
    private final Map predecessors = new HashMap();
    
    /**
     * Constructor.
     */
    public DijkstraEngine(RoutesMap map)
    {
        this.map = map;
    }

    /**
     * Initialize all data structures used by the algorithm.
     * 
     * @param start the source node
     */
    private void init(Node start)
    {
        settledNodes.clear();
        unsettledNodes.clear();
        
        shortestDistances.clear();
        predecessors.clear();
        
        // add source
        setShortestDistance(start, 0);
        unsettledNodes.add(start);
    }
    
    /**
     * Run Dijkstra's shortest path algorithm on the map.
     * The results of the algorithm are available through
     * {@link #getPredecessor(Node)}
     * and 
     * {@link #getShortestDistance(Node)}
     * upon completion of this method.
     * 
     * @param start the starting node
     * @param destination the destination node. If this argument is <code>null</code>, the algorithm is
     * run on the entire graph, instead of being stopped as soon as the destination is reached.
     */
    public void execute(Node start, Node destination)
    {
        init(start);
        
        // the current node
        Node u;
        
        // extract the node with the shortest distance
        while ((u = extractMin()) != null)
        {
            assert !isSettled(u);
            
            // destination reached, stop
            if (u == destination) break;
            
            markSettled(u);
            
            relaxNeighbors(u);
        }
    }

    /**
     * Extract the node with the currently shortest distance, and remove it from
     * the priority queue.
     * 
     * @return the minimum node, or <code>null</code> if the queue is empty.
     */
    private Node extractMin()
    {
    	if (unsettledNodes.isEmpty()) return null;
    	
        Node min = (Node) unsettledNodes.first();
        unsettledNodes.remove(min);
        
        return min;
    }
    
	/**
	 * Compute new shortest distance for neighboring nodes and update if a better
	 * distance is found.
	 * 
	 * @param u the node
	 */
    private void relaxNeighbors(Node u)
    {
        for (Iterator i = map.getDestinations(u).iterator(); i.hasNext(); )
        {
            Node v = (Node) i.next();
            
            // skip node already settled
            if (isSettled(v)) continue;
            
            if (getShortestDistance(v) > getShortestDistance(u) + map.getDistance(u, v))
            {
            	// assign new shortest distance and mark unsettled
                setShortestDistance(v, getShortestDistance(u) + map.getDistance(u, v));
                                
                // assign predecessor in shortest path
                setPredecessor(v, u);
            }
        }        
    }

	/**
	 * Mark a node as settled.
	 * 
	 * @param u the node
	 */
	private void markSettled(Node u)
	{
		settledNodes.add(u);    
	}

	/**
	 * Test a node.
	 * 
     * @param v the node to consider
     * 
     * @return whether the node is settled, ie. its shortest distance
     * has been found.
     */
    private boolean isSettled(Node v)
    {
        return settledNodes.contains(v);
    }

    /**
     * @return the shortest distance from the source to the given node, or
     * {@link DijkstraEngine#INFINITE_DISTANCE} if there is no route to the destination.
     */    
    public int getShortestDistance(Node node)
    {
        Integer d = (Integer) shortestDistances.get(node);
        return (d == null) ? INFINITE_DISTANCE : d.intValue();
    }

	/**
	 * Set the new shortest distance for the given node,
	 * and re-balance the set according to new shortest distances.
	 * 
	 * @param node the node to set
	 * @param distance new shortest distance value
	 */        
    private void setShortestDistance(Node node, int distance)
    {
        // this crucial step ensure no duplicates will be created in the queue
        // when an existing unsettled node is updated with a new shortest distance
        unsettledNodes.remove(node);

        shortestDistances.put(node, new Integer(distance));
        
		// re-balance the sorted set according to the new shortest distance found
		// (see the comparator the set was initialized with)
		unsettledNodes.add(node);        
    }
    
    /**
     * @return the node leading to the given node on the shortest path, or
     * <code>null</code> if there is no route to the destination.
     */
    public Node getPredecessor(Node node)
    {
        return (Node) predecessors.get(node);
    }
    
    private void setPredecessor(Node a, Node b)
    {
        predecessors.put(a, b);
    }

}
