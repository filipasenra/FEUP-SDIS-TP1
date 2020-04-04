package com.assigment_1.Protocol;

import java.util.Arrays;

public class MulticastControlChannel extends MultiCastChannel {
    public MulticastControlChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void confirmStore(double version, String senderId, String fileID, int chunkNo) {
        byte[] message = MessageFactory.createMessage(version, "STORED", senderId, fileID, chunkNo);

        this.exec.execute(new Thread(() -> this.sendConfirmStore(message)));
    }
}
