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
    System.out.println("Waiting for echo...");
    runEventLoop();
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

  public void runEventLoop()
  {
    /*
    try
    {
      LinkStateConfig config = LinkStateConfig.getInstance();
      int myport = config.getBobPort();
      m_socket = new DatagramSocket(myport);
    }
    catch(Exception e)
    {
      System.out.println("Cannot create UDP socket: "+e);
    }
    */

    byte[] buffer = new byte[Packet.MAX_PACKET_SIZE];
    DatagramPacket p = new DatagramPacket(buffer, buffer.length);

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

  private void handlePacket(DatagramPacket p)
  {
    // retrieve the packet received
    byte[] rawData = p.getData();
    // convert to our Packet object
    Packet packet = new Packet(rawData);

    int msglen = packet.length;
    byte[] msgbytes = new byte[msglen];
    for(int i=0; i<msglen; i++)
      msgbytes[i] = packet.payload[i];

    System.out.println(new String(msgbytes));
  }

  public static void main(String argsp[]) throws Exception
  {
    Alice alice = new Alice();
    alice.run();
  }
}
