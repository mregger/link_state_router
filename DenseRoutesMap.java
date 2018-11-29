import java.util.ArrayList;
import java.util.List;

/**
 * This map stores routes in a matrix, a nxn array. It is most useful when
 * there are lots of routes, otherwise using a sparse representation is
 * recommended.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: DenseRoutesMap.java,v 1.1 2002/11/16 20:37:52 renaud Exp $
 */

class DenseRoutesMap
	implements RoutesMap
{
	private final int[][] distances;
	
	DenseRoutesMap(int numNodes)
	{
		distances = new int[numNodes][numNodes];
	}
	
	/**
	 * Link two nodes by a direct route with the given distance.
	 */
	public void addDirectRoute(Node start, Node end, int distance)
	{
		distances[start.getIndex()][end.getIndex()] = distance;
	}
	
	/**
	 * @return the distance between the two nodes, or 0 if no path exists.
	 */
	public int getDistance(Node start, Node end)
	{
		return distances[start.getIndex()][end.getIndex()];
	}
	
	/**
	 * @return the list of all valid destinations from the given node.
	 */
	public List getDestinations(Node node)
	{
		List list = new ArrayList();
		
		for (int i = 0; i < distances.length; i++)
		{
			if (distances[node.getIndex()][i] > 0)
			{
				list.add( Node.valueOf(i) );
			}
		}
		
		return list;
	}

	/**
	 * @return the list of all nodes leading to the given node.
	 */
	public List getPredecessors(Node node)
	{
		List list = new ArrayList();
		
		for (int i = 0; i < distances.length; i++)
		{
			if (distances[i][node.getIndex()] > 0)
			{
				list.add( Node.valueOf(i) );
			}
		}
		
		return list;
	}
	
	/**
	 * @return the transposed graph of this graph, as a new RoutesMap instance.
	 */
	public RoutesMap getInverse()
	{
		DenseRoutesMap transposed = new DenseRoutesMap(distances.length);
		
		for (int i = 0; i < distances.length; i++)
		{
			for (int j = 0; j < distances.length; j++)
			{
				transposed.distances[i][j] = distances[j][i];
			}
		}
		
		return transposed;
	}
}
