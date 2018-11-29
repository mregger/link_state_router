/*
* Author: Wenbing Zhao
* Last Modified: 3/31/2007
* For EEC484/584 Project
*/

import java.util.*;
import java.net.*;
import java.io.*;

class Routed
{

  private DijkstraEngine m_engine = null;
  private List m_nodeList = null; // list of nodes in this network
  private Map m_forwardingTable = null;
  private Node m_me = null; // Who am I?
  private DatagramSocket m_socket = null;

  public Routed(Node self, List nodelist, DenseRoutesMap map)
  {
    m_me = self;
    m_nodeList = nodelist;
    m_engine = new DijkstraEngine(map);
    m_forwardingTable = new HashMap();
    // Run Dijkstra's Algorithm and determine my forwarding table
    populateForwardingTable();
    printForwardingTable();
  }

  public void printForwardingTable()
  {
    int size = m_nodeList.size();
    for(int i=0; i<size; i++)
    {
      // do nothing for self node
      Node n = (Node)m_nodeList.get(i);
      System.out.print(n);
      if(m_me.equals(n))
      System.out.println(": -");
      else
      System.out.println(": "+m_forwardingTable.get(n));
    }
  }

  // This method should return the Node to which the packet
  // should be forwarded to, based on the destination address
  // included in the packet.
  //
  // If the destination is this node itself, the self node
  // should be returned
  //
  public Node lookupForwardingTable(char destination)
  {
    // to be completed by students
    //TODO: this.
    return null;
  }

  public void runEventLoop()
  {
    try
    {
      m_socket = new DatagramSocket(m_me.getPort());
    } catch(Exception e)
    {
      System.out.println("Cannot create UDP socket: "+e);
    }

    while(true)
    {
      byte[] buffer = new byte[Packet.MAX_PACKET_SIZE];
      DatagramPacket p =
      new DatagramPacket(buffer, buffer.length);

      try
      {
        m_socket.receive(p);
      }
      catch(Exception e)
      {
        System.out.println("Cannot receive from socket: "+e);
      }

      handlePacket(p);
    }
  }

  private void handlePacket(DatagramPacket p)
  {
    // retrieve the packet received
    byte[] rawData = p.getData();
    // convert to our Packet object
    Packet packet = new Packet(rawData);

    // Decide which node to forward to
    // If we are the final destination, deliver to the application

    Node n = lookupForwardingTable(packet.dest);
    if(n.equals(m_me))
    {
      // deliver packet
      System.out.println("I'm the destination router for packet "+packet.seq);
      deliverToHost(packet);
    }
    else
    {
      // send the packet to the next node as indicated by our table
      forwardToNode(n, packet);
      System.out.println("Passed packet "+packet.seq+" to router "+n);
    }
  }

  private void deliverToHost(Packet packet)
  {
    try
    {
      InetAddress ip = InetAddress.getByName("localhost");
      int port = 	getApplicationPort();
      send(ip, port, packet);
    }
    catch(Exception e)
    {

    }
  }

  private void forwardToNode(Node n, Packet packet)
  {
    InetAddress ip = n.getIPAddress();
    int port = n.getPort();
    send(ip, port, packet);
  }

  private void send(InetAddress ip, int port, Packet packet)
  {
    byte[] payload = packet.toBytes();
    try
    {
      DatagramPacket p =
      new DatagramPacket(payload, payload.length, ip, port);
      m_socket.send(p);
    }
    catch(Exception e)
    {
      System.out.println("Error sending packet: "+e);
    }
  }

  private void populateForwardingTable()
  {
    // calculate shortest path
    m_engine.execute(m_me, null);

    // for every destination except myself, I should determine
    // the output link (i.e., to which of my direct neighbor
    // I should forward the message to for the destination)

    // to be completed by students
  }

  // this method should be used to complete the populateForwardingTable()
  // method
  private List getShortestPathToNode(Node n)
  {
    List l = new ArrayList();
    for(Node c = n; c!= null; c = m_engine.getPredecessor(c))
    l.add(c);
    Collections.reverse(l);
    System.out.println("path from "+m_me+" to "+n+": "+l);

    return l;
  }

  // Get the port of the application to which we can delivery packet
  private int getApplicationPort()
  {
    LinkStateConfig config = LinkStateConfig.getInstance();
    // if this router is what Alice is connected to, use Alice's port
    Node alice = config.getAliceRouter();
    int alicePort = config.getAlicePort();
    int bobPort = config.getBobPort();
    if(alice.equals(m_me))
    return alicePort;
    else // set to Bob's port
    return bobPort;
  }

  public static void main(String args[]) throws Exception
  {

    LinkStateConfig config = LinkStateConfig.getInstance();
    DenseRoutesMap testMap = config.getRoutesMap();
    int nwsize = config.getNetworkSize();

    List nlist = new ArrayList();
    for(int i=0; i<nwsize; i++)
    nlist.add(Node.valueOf(i));

    Node myNode = null;
    try
    {
      if(args.length > 0)
      {
        String me = args[0];
        char c = me.charAt(0);
        System.out.println("Node "+c);
        myNode = Node.valueOf(c);
        System.out.println("Node "+myNode);
      }
    }
    catch(Exception e)
    {
      System.err.println("Error parsing command line args");
      System.exit(1);
    }

    Routed d = new Routed(myNode, nlist, testMap);
    d.runEventLoop();
  }
}
