package com.assigment_1.Protocol;

import com.assigment_1.PeerClient;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class MulticastControlChannel extends MulticastChannel {

    public MulticastControlChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void confirmStore(double version, String senderId, String fileID, int chunkNo) {
        byte[] message = MessageFactory.createMessage(version, "STORED", senderId, fileID, chunkNo);

        Random random = new Random();

        this.exec.schedule(new Thread(() -> this.sendMessage(message)), random.nextInt(401), TimeUnit.MILLISECONDS);
    }

    public void deleteFile(double version, String senderId, String filepath) {

        File file = new File(filepath);

        String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());

        PeerClient.getStorage().deleteStoredChunksCounter(fileID);

        byte[] message = MessageFactory.createMessage(version, "DELETE", senderId, fileID);

        PeerClient.getExec().schedule(new Thread(() -> this.sendMessage(message)), 1, TimeUnit.SECONDS);
    }

    public void restoreFile(double version, String senderId, String filepath) {
        File file = new File(filepath);

        String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());

        byte[] message = MessageFactory.createMessage(version, "RESTORE", senderId, fileID);

        PeerClient.getExec().schedule(new Thread(() -> this.sendMessage(message)), 1, TimeUnit.SECONDS);
    }
}
