import java.util.List;

/**
 * This interface defines the object storing the graph of all routes in the
 * system.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: RoutesMap.java,v 1.1 2002/11/16 20:37:51 renaud Exp $
 */

public interface RoutesMap
{
	/**
	 * Enter a new segment in the graph.
	 */
	public void addDirectRoute(Node start, Node end, int distance);
	
	/**
	 * Get the value of a segment.
	 */
	public int getDistance(Node start, Node end);
	
	/**
	 * Get the list of cities that can be reached from the given node.
	 */
	public List getDestinations(Node node); 
	
	/**
	 * Get the list of cities that lead to the given node.
	 */
	public List getPredecessors(Node node);
	
	/**
	 * @return the transposed graph of this graph, as a new RoutesMap instance.
	 */
	public RoutesMap getInverse();
}
