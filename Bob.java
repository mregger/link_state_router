/*
* Author: Wenbing Zhao
* Last Modified: 3/31/2007
* For EEC484/584 Project
*/

import java.util.*;
import java.net.*;
import java.io.*;

public class Bob
{
  private DatagramSocket m_socket = null;
  private Node m_bob = null;
  private Node m_alice = null;
  private String message = null;

  public void runEventLoop()
  {
    try
    {
      LinkStateConfig config = LinkStateConfig.getInstance();
      m_alice = config.getAliceRouter();
      m_bob = config.getBobRouter();
      int myport = config.getBobPort();
      m_socket = new DatagramSocket(myport);
    }
    catch(Exception e)
    {
      System.out.println("Cannot create UDP socket: "+e);
    }

    while(true)
    {
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
      echoPacket(p);
    }
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

    message = new String(msgbytes);
    System.out.println(message);
  }

  public void echoPacket(DatagramPacket packet)
  {
    Random gen = new Random();
    Packet p = new Packet();
    p.dest = m_alice.getName();
    p.src = m_bob.getName();
    p.seq = gen.nextInt(1000);

    //String msg = "Hello World";
    p.length = message.length();
    p.payload = message.getBytes();

    // Note that we are sending the packet to the router
    // that Alice is attached to, not the one that Bob is
    // attached to. We certainly could send to Bob's router,
    // but we will lose the opportunity to exercise routing!
    sendToNode(m_bob, p);
    System.out.println("Echoed to Alice");
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
    Bob b = new Bob();
    b.runEventLoop();
  }
}
