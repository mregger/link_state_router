/*
* Author: Wenbing Zhao
* Last Modified: 3/31/2007
* For EEC484/584 Project
*/

import java.util.*;
import java.net.*;
import java.io.*;

public class Alice
{
  private DatagramSocket m_socket = null;
  private Node m_bob = null;
  private Node m_alice = null;

  public Alice()
  {
    LinkStateConfig config = LinkStateConfig.getInstance();
    m_alice = config.getAliceRouter();
    m_bob = config.getBobRouter();
    int myport = config.getAlicePort();

    try
    {
      m_socket = new DatagramSocket(myport);
    } catch(Exception e) {
      System.out.println("Error creating socket: "+e);
    }
  }

  public void run()
  {
    Random gen = new Random();
    Packet p = new Packet();
    p.dest = m_bob.getName();
    p.src = m_alice.getName();
    p.seq = gen.nextInt(1000);

    String msg = "Hello World";
    p.length = msg.length();
    p.payload = "Hello World".getBytes();

    // Note that we are sending the packet to the router
    // that Alice is attached to, not the one that Bob is
    // attached to. We certainly could send to Bob's router,
    // but we will lose the opportunity to exercise routing!
    sendToNode(m_alice, p);
    System.out.println("Sent to Bob: "+msg);
  }

  private void sendToNode(Node n, Packet packet)
  {
    InetAddress ip = n.getIPAddress();
    int port = n.getPort();
    byte[] payload = packet.toBytes();

    try
    {
      DatagramPacket p = new DatagramPacket(payload, payload.length, ip, port);
      m_socket.send(p);
    }
    catch(Exception e)
    {
      System.out.println("Error sending packet: "+e);
    }
  }

  public static void main(String argsp[]) throws Exception
  {
    Alice alice = new Alice();
    alice.run();
  }
}
