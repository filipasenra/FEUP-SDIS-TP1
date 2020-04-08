package com.assigment_1.Protocol;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MulticastDataRecoveryChannel extends MulticastChannel {

    public MulticastDataRecoveryChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void sendChunk(double version, String senderId, String fileID, int chunkNo) {
        Random random = new Random();

        byte[] message = MessageFactory.createMessage(version, "CHUNK", senderId, fileID, chunkNo);
        this.exec.schedule(new Thread(() -> this.sendMessage(message)),  random.nextInt(401), TimeUnit.MILLISECONDS);
    }
}
