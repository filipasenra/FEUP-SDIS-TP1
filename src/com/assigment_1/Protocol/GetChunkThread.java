package com.assigment_1.Protocol;

import javafx.util.Pair;

import java.util.HashMap;
import java.io.IOException;

import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;

import java.util.concurrent.ConcurrentHashMap;

import static com.assigment_1.Protocol.MulticastChannel.sizeOfChunks;

public class GetChunkThread implements Runnable {
    double version;
    String senderId;
    String fileId;
    int chunkNo;

    public GetChunkThread(double version, String fileId, int chunkNo) {
        this.version = version;
        this.senderId = PeerClient.getId();
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        HashMap<Pair<String, Integer>, Chunk> storedChunks = PeerClient.getStorage().getStoredChunks();
        Pair<String, Integer> pair = new Pair<>(fileId, chunkNo);
        ConcurrentHashMap<Pair<String, Integer>, byte[]> recoveredChunks = PeerClient.getStorage().getRecoveredChunks();

        if (!recoveredChunks.containsKey(pair)) {
            storedChunks.get(new Pair<>(fileId, chunkNo));
            byte[] message = MessageFactory.createMessage(version, "REMOVED", senderId, fileId, chunkNo);
            PeerClient.getExec().execute(new Thread(() -> PeerClient.getMC().sendMessage(message)));
        }
    }
}
