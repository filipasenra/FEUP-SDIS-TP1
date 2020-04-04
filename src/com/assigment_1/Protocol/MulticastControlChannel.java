package com.assigment_1.Protocol;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MulticastControlChannel extends MultiCastChannel {
    public MulticastControlChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void confirmStore(double version, String senderId, String fileID, int chunkNo) {
        byte[] message = MessageFactory.createMessage(version, "STORED", senderId, fileID, chunkNo);

        Random random = new Random();

        this.exec.schedule(new Thread(() -> this.sendConfirmStore(message)), random.nextInt(401), TimeUnit.MILLISECONDS);
    }
}
