package com.assigment_1.Protocol;

import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MulticastDataRecoveryChannel extends MulticastChannel {

    public MulticastDataRecoveryChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void sendChunk(double version, String senderId, String fileID, int chunkNo) throws IOException {
        Random random = new Random();

        HashMap<Pair<String, Integer>, Chunk> storedChunks = PeerClient.getStorage().getStoredChunks();

        Chunk chunk = storedChunks.get(new Pair<String, Integer>(fileID, chunkNo));
        byte[] data = chunk.getData();

        byte[] message = MessageFactory.createMessage(version, "CHUNK", senderId, fileID, chunkNo, data);
        this.exec.schedule(new Thread(() -> this.sendMessage(message)),  random.nextInt(401), TimeUnit.MILLISECONDS);
    }
}
