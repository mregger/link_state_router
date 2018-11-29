/**
 * A node is identified by its name, a single uppercase character.
 * Conversions to/from characters are handled by 
 * {@link #getName()} and {@link #valueOf(char)}, respectively.
 * <p>
 * Package members are also given access to an identity relationship between
 * nodes and numbers: they can converts between <code>Node</code>
 * instances and numbers using {@link #valueOf(int)} and {@link #getIndex()}.
 * This special relationship is used by the
 * {@link com.waldura.tw.DenseRoutesMap DensesRoutesMap} 
 * to store nodes in an array.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @author Wenbing Zhao; added networking information in each node
 * @version $Id: Node.java,v 1.1 2002/11/16 20:37:52 renaud Exp $
 */

import java.net.*;

public class Node implements Comparable
{
    /**
     * The largest possible number of nodes.
     */
    public static final int MAX_NUMBER = 26;
    
    private static final Node[] nodes = new Node[MAX_NUMBER];
    
    private InetAddress m_IPAddress;
    private int m_port;

    static
    {
        // initialize all Node objects
	for (char c = 'A'; c <= 'Z'; c++)
	    {
		int i = getIndexForName(c);
		nodes[i] = new Node(c);
		nodes[i].m_port = 4000+i;
		try {
		    nodes[i].m_IPAddress = InetAddress.getByName("localhost"); 
		}catch(Exception e) {}
	    }		
    }
    
    public void setNetworkInfo(InetAddress ip, int port) {
	m_IPAddress = ip;
	m_port = port;
    }

    public InetAddress getIPAddress() { return m_IPAddress; }
    public int getPort() { return m_port; }

    private static int getIndexForName(char name)
    {
	return name - 'A';
    }	
    
    private static char getNameForIndex(int index)
    {
	return (char)('A' + index);
    }	
    
    public static final Node A = Node.valueOf('A');
    public static final Node B = Node.valueOf('B');
    public static final Node C = Node.valueOf('C');
    public static final Node D = Node.valueOf('D');
    public static final Node E = Node.valueOf('E');
    public static final Node F = Node.valueOf('F');
    public static final Node G = Node.valueOf('G');
    public static final Node H = Node.valueOf('H');
    public static final Node I = Node.valueOf('I');
    public static final Node J = Node.valueOf('J');
    public static final Node K = Node.valueOf('K');
    public static final Node L = Node.valueOf('L');
    public static final Node M = Node.valueOf('M');
    public static final Node N = Node.valueOf('N');
    public static final Node O = Node.valueOf('O');
    public static final Node P = Node.valueOf('P');
    public static final Node Q = Node.valueOf('Q');
    public static final Node R = Node.valueOf('R');
    public static final Node S = Node.valueOf('S');
    public static final Node T = Node.valueOf('T');
    public static final Node U = Node.valueOf('U');
    public static final Node V = Node.valueOf('V');
    public static final Node W = Node.valueOf('W');
    public static final Node X = Node.valueOf('X');
    public static final Node Y = Node.valueOf('Y');
    public static final Node Z = Node.valueOf('Z');
	
    public static Node valueOf(char name)
    {
	if (name < 'A' || name > 'Z')
	    {
		throw new IllegalArgumentException("Invalid node name: " + name);	
	    }
	
	return nodes[getIndexForName(name)];
    }
	
    public static Node valueOf(int n)
    {
	if (n < 0 || n > 25)
	    {
		throw new IllegalArgumentException("Invalid node number: " + n);
	    }
	
	return valueOf( getNameForIndex(n) );		
    }
    
    private final char name;
    
    /**
     * Private constructor.
     * @param name
     */
    private Node(char name)
    {
	this.name = name;	
    }
    
    public char getName()
    {
	return name;	
    }
    
    /*
     * Package members only.
     */
    int getIndex()
    {
	return getIndexForName(name);
    }	
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
	return String.valueOf(name);
    }
    
    /**
     * Two nodes are considered equal if they are the same object,
     * or their names are the same.
     * 
     * @see java.lang.Object#equals(Object)
     */    
    public boolean equals(Object o)
    {
        return this == o || equals((Node) o);
    }
    
    private boolean equals(Node c)
    {
        return this.name == c.name;
    }
    
    /**
     * Compare two nodes by name.
     * 
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o)
    {
        return compareTo((Node) o);
    }
    
    public int compareTo(Node c)
    {
        return this.name - c.name;
    }
}
