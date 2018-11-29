/*
 * Author: Wenbing Zhao
 * Last Modified: 3/31/2007
 * For EEC484/584 Project 
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LinkStateConfig {
    private static final String PROPERTY_FILE = "LinkState.properties";
    private Properties m_properties = null;
    private static LinkStateConfig instance = new LinkStateConfig();
    private DenseRoutesMap m_routeMap = null;
    private int m_networkSize = 0;
    private Node m_aliceRouter = null;
    private Node m_bobRouter = null;
    private int m_alicePort = 0;
    private int m_bobPort = 0;

    private LinkStateConfig() {
	m_properties = new Properties();
	loadProperties();
    }

    public static LinkStateConfig getInstance() {
	return instance;
    }

    public int getNetworkSize() { return m_networkSize; }
    public DenseRoutesMap getRoutesMap() { return m_routeMap; }
    public Node getAliceRouter() {return m_aliceRouter; }
    public Node getBobRouter() {return m_bobRouter; }
    public int getAlicePort() { return m_alicePort; }
    public int getBobPort() { return m_bobPort; }

    private void loadProperties() {
	InputStream in = getClass().getClassLoader().getResourceAsStream(
	    PROPERTY_FILE);
	
	try {
	    m_properties.load(in);
	    in.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
	
	String networkSizeStr = m_properties.getProperty("network.size");
	if(null == networkSizeStr) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}

	try {
	    m_networkSize = Integer.parseInt(networkSizeStr);
	}catch(Exception e) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}

	if(m_networkSize <= 1 || m_networkSize > 25) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}

	System.out.println("newtork size "+m_networkSize);
	m_routeMap = new DenseRoutesMap(m_networkSize);

	String topo = m_properties.getProperty("topology");
	if(null == topo) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}
	String[] links = topo.split(",");
	for(int i=0; i<links.length; i++) {
	    String[] items = links[i].split("-");
	    // must contains 3 items 
	    if(items.length != 3) {
		System.err.println("Illegal linkstate config");
		System.exit(1);
	    }
	    
	    char src = items[0].charAt(0);
	    char dst = items[1].charAt(0);
	    int linkcost = 0;
	    try {
		linkcost = Integer.parseInt(items[2]);
	    }catch(Exception e) {
		System.err.println("Illegal linkstate config");
		System.exit(1);
	    }

	    System.out.println("src="+src+", dst="+dst+", cost="+linkcost);
	    
	    // Add links to routemap. Note that RouteMap assume single direction link,
	    // and our config assumes dual-direction link.
	    m_routeMap.addDirectRoute(Node.valueOf(src), Node.valueOf(dst), linkcost);
	    m_routeMap.addDirectRoute(Node.valueOf(dst), Node.valueOf(src), linkcost);
   
	}

	String appSrc = m_properties.getProperty("alice.addr");
	if(null == appSrc) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}
	String appDst = m_properties.getProperty("bob.addr");
	if(null == appDst) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}
	String alicePort = m_properties.getProperty("alice.port");
	if(null == alicePort) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}
	String bobPort = m_properties.getProperty("bob.port");
	if(null == bobPort) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}
	
	m_aliceRouter = Node.valueOf(appSrc.charAt(0));
	m_bobRouter = Node.valueOf(appDst.charAt(0));
	try {
	    m_alicePort = Integer.parseInt(alicePort);
	    m_bobPort = Integer.parseInt(bobPort);
	}catch(Exception e) {
	    System.err.println("Illegal linkstate config");
	    System.exit(1);
	}

    }
}
