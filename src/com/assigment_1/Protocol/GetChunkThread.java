package com.assigment_1.Protocol;

import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

        if(!recoveredChunks.containsKey(pair)) {
            System.out.println(recoveredChunks.keySet() + "tem?  " + pair);

            Chunk chunk = storedChunks.get(new Pair<>(fileId, chunkNo));
            byte[] data = new byte[sizeOfChunks];
            try {
                data = chunk.getData();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] message = MessageFactory.createMessage(version, "CHUNK", senderId, fileId, chunkNo, data);
            PeerClient.getExec().execute(new Thread(() -> PeerClient.getMDR().sendMessage(message)));
        }
    }
}
