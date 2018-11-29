/*
* Author: Wenbing Zhao
* Last Modified: 3/31/2007
* For EEC484/584 Project
*/

public class Packet
{
  public static final int MAX_PACKET_PAYLOAD = 1024;
  public static final int HEADER_LEN = 16; // src/dst + size of 3 integers
  // note a char has size of 2 bytes in Java

  // max_packet_size = payload length + header fields
  public static final int MAX_PACKET_SIZE = MAX_PACKET_PAYLOAD +
  HEADER_LEN;

  public char dest; // name of the destination node
  public char src;  // sending node
  public int seq; // sequence number for duplicate detection
  public int ack; // acked sequence number
  public int length; // payload length
  public byte[] payload = new byte[MAX_PACKET_PAYLOAD];

  // constructor to be used when sending
  public Packet()
  {
    seq = -1;
    ack = -1;
    length = 0;
  }

  // constructor to be used when receiving
  public Packet(byte[] receivedData)
  {
    int index = 0;

    byte[] charArray = {receivedData[index], receivedData[index+1]};
    index += 2;
    this.dest = ByteArrayUtils.readChar(charArray);
    charArray[0] = receivedData[index++];
    charArray[1] = receivedData[index++];
    this.src = ByteArrayUtils.readChar(charArray);

    // set seq number
    byte[] intArray = {receivedData[index], receivedData[index+1], receivedData[index+2], receivedData[index+3]};
    seq = ByteArrayUtils.readInt(intArray);
    index += 4;

    // set ack sequence number
    intArray[0] = receivedData[index++];
    intArray[1] = receivedData[index++];
    intArray[2] = receivedData[index++];
    intArray[3] = receivedData[index++];
    ack = ByteArrayUtils.readInt(intArray);

    // payload length
    intArray[0] = receivedData[index++];
    intArray[1] = receivedData[index++];
    intArray[2] = receivedData[index++];
    intArray[3] = receivedData[index++];
    length = ByteArrayUtils.readInt(intArray);

    // the rest is the application payload
    //System.out.println("payload len: "+length);

    for(int i=0; i<length; i++)
    {
      payload[i] = receivedData[index+i];
      //System.out.print(""+payload[i]);
    }
    //System.out.println("$$$");
    //String recvd = new String(payload);
    //System.out.println("Packet::Received: "+recvd);
    //System.out.println("Got ack, try get another message to send");
  }

  public byte[] toBytes()
  {
    // we want to construct a byte array consisting
    // a sequence number, an ack,
    // a length field (4 bytes), and the payload
    int totalLen = length + HEADER_LEN;
    byte[] data = new byte[totalLen];
    //System.out.println("Send total length: "+totalLen);

    int i = 0;
    byte[] charArray = new byte[2];
    ByteArrayUtils.writeChar(charArray, this.dest);
    data[i++] = charArray[0];
    data[i++] = charArray[1];
    ByteArrayUtils.writeChar(charArray, this.src);
    data[i++] = charArray[0];
    data[i++] = charArray[1];

    byte[] intArray = new byte[4];
    ByteArrayUtils.writeInt(intArray, seq);
    int k = 0;
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];

    ByteArrayUtils.writeInt(intArray, ack);
    k = 0;
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];

    ByteArrayUtils.writeInt(intArray, length);
    k = 0;
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];
    data[i++] = intArray[k++];

    //System.out.println("Packet::toBytes: payload len="+length);

    for(int j=0; j<length; j++,i++)
    data[i] = payload[j];

    // self test on marshalling
    //System.out.println("----");
    //Packet test = new Packet(data);
    //System.out.println("----");

    return data;
  }

  public void printWithPayload(String comment)
  {
    System.out.println(comment+": seq="+seq+", ack="+ack+", len="+length+" \""+new String(payload)+"\"");
  }
  public void print(String comment)
  {
    System.out.println(comment+": seq="+seq+", ack="+ack+", len="+length);
  }
}
