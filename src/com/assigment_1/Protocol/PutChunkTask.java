package com.assigment_1.Protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PutChunkTask {

    String message;

    public PutChunkTask(double version, int senderId, int fileId, int chunkNo, int replicationDeg, byte[] body) {

        message = MessageFactory.createMessage(version, "PUTCHUNK", senderId, fileId, chunkNo, replicationDeg, body);

    }

    public void communicating(){
        //TODO send putchunk
    }
}
