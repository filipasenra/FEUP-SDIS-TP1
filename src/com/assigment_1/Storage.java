package com.assigment_1;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.HashMap;
import javafx.util.Pair;
import java.io.File;

public class Storage implements Serializable {
    private int overallSpace;
    private int occupiedSpace = 0;
    private HashMap<Pair<String, Integer>, Chunk> storedChunks = new HashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> storedChunksCounter = new ConcurrentHashMap<>();

    public Storage(int overallSpace) {
        this.overallSpace = overallSpace;
    }

    public void addChunkToStorage(Chunk chunk) {

        if ((this.overallSpace - this.occupiedSpace) < chunk.data.length) {
            System.out.println("Peer doesn't have space for chunk number " + chunk.chunkNo + " of " + chunk.fileId + " from " + chunk.senderId);
            return;
        }

        Pair<String, Integer> pair = new Pair<>(chunk.fileId, chunk.chunkNo);

        if (!storedChunks.containsKey(pair)) {
            this.storedChunks.put(pair, chunk);
            this.occupiedSpace += chunk.data.length;

            String filename = PeerClient.getId() + "/" + chunk.fileId + "_" + chunk.chunkNo;

            File file = new File(filename);
            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    return;
                }

                FileOutputStream fos = new FileOutputStream(filename);
                fos.write(chunk.data);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //SEND CHUNK STORAGE CONFIRMATION MESSAGE
        PeerClient.getMC().confirmStore(chunk.version, PeerClient.getId(), chunk.fileId, chunk.chunkNo);
    }

    public ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> getStoredChunksCounter() {
        return storedChunksCounter;
    }

    public void updateStoredChunksCounter(String fileId, int chunkNo, String senderId) {

        Pair<String, Integer> pair = new Pair<>(fileId, chunkNo);
        ArrayList<String> aux = new ArrayList<>();
        aux.add(senderId);

        if (!this.storedChunksCounter.containsKey(pair)) {
            this.storedChunksCounter.put(pair, aux);
        } else {
            ArrayList<String> curr = this.storedChunksCounter.get(pair);

            if (!curr.contains(senderId))
                curr.add(senderId);
        }

    }
}
