package com.assigment_1.Protocol;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.assigment_1.PeerClient;

public class MultiCastChannel implements Runnable {

    protected final static int sizeOfChunks = 64000;
    protected ScheduledThreadPoolExecutor exec;
    private int port;
    private InetAddress address;

    public MultiCastChannel(String INETaddress, int port) {
        try {

            this.port = port;
            this.address = InetAddress.getByName(INETaddress);
            this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(250);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void sendChunk(byte[] chunk) {

        try {
            //opening DatagramSocket to send chunk
            DatagramSocket senderSocket = new DatagramSocket();

            DatagramPacket msgPacket = new DatagramPacket(chunk, chunk.length, this.address, this.port);
            senderSocket.send(msgPacket);

            System.out.println("SENDING CHUNK");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendConfirmStore(byte[] message) {

        try {
            //opening DatagramSocket to send confirm message
            DatagramSocket senderSocket = new DatagramSocket();

            DatagramPacket msgPacket = new DatagramPacket(message, message.length, this.address, this.port);
            senderSocket.send(msgPacket);

            System.out.println("SENDING CONFIRM MESSAGE");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Listening
    @Override
    public void run() {

        //buffer to save the incoming bytes
        byte[] buffer = new byte[65000];

        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
        try {
            //Joint the Multicast group.

            MulticastSocket receiverSocket = new MulticastSocket(this.port);
            receiverSocket.joinGroup(address);

            while (true) {
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                receiverSocket.receive(msgPacket);
                
                PeerClient.getExec().execute(new ReceivedMessagesHandler(buffer));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
