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

  public void runEventLoop()
  {
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

    int msglen = packet.length;
    byte[] msgbytes = new byte[msglen];
    for(int i=0; i<msglen; i++)
      msgbytes[i] = packet.payload[i];

    System.out.println(new String(msgbytes));
  }

  public static void main(String argsp[]) throws Exception
  {
    Bob b = new Bob();
    b.runEventLoop();
  }
}
