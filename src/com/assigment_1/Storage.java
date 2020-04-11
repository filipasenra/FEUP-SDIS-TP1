package com.assigment_1;

import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    private int overallSpace;
    private int occupiedSpace;
    private HashMap<Pair<String, Integer>, Chunk> storedChunks = new HashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> storedChunksCounter = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, byte[]> recoveredChunks = new ConcurrentHashMap<>();

    //UNTIL THE CLIENT DEFINES THE MAXIMUM DISK SIZE THAT CAN BE USED FOR STORING CHUNKS, WE DON'T DEFINE THE LIMIT
    public Storage() {
        this.occupiedSpace = 0;
        this.overallSpace = -1;
    }

    public void setOverallSpace(int overallSpace) {
        this.overallSpace = overallSpace*1000;
    }

    public void setOccupiedSpace(int occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }

    public ConcurrentHashMap<Pair<String, Integer>, byte[]> getRecoveredChunks() {
        return recoveredChunks;
    }

    public Chunk removeStoredChunk(Pair<String, Integer> key) {
        Chunk chunk = storedChunks.remove(key);

        return chunk;
    }

    public int getOverallSpace() {
        return overallSpace;
    }

    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    public ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> getStoredChunksCounter() {
        return storedChunksCounter;
    }

    public HashMap<Pair<String, Integer>, Chunk> getStoredChunks() {
        return storedChunks;
    }

    public void addRecoveredChunk(String fileId, int chunkNo, byte[] data) {
        Pair<String, Integer> pair = new Pair<>(fileId, chunkNo);
        if (!recoveredChunks.containsKey(pair)) {
            recoveredChunks.put(pair, data);
        }
    }

    public void addChunkToStorage(Chunk chunk) {
        if ((this.overallSpace - this.occupiedSpace) < chunk.data.length && (this.overallSpace != -1)) {
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

                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(chunk.data);

                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //SEND CHUNK STORAGE CONFIRMATION MESSAGE
        PeerClient.getMC().confirmStore(chunk.version, PeerClient.getId(), chunk.fileId, chunk.chunkNo);
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

    public void deleteStoredChunksCounter(String fileId) {
        for (HashMap.Entry<Pair<String, Integer>, ArrayList<String>> entry : storedChunksCounter.entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            if (key.getKey().equals(fileId)) {
                storedChunksCounter.remove(key);
            }
        }
    }

    public void deleteFileChunks(String fileId) {
        ArrayList<Pair<String, Integer>> keys = new ArrayList<>(storedChunks.keySet());

        for (Pair<String, Integer> key : keys) {
            if (key.getKey().equals(fileId)) {
                String filename = PeerClient.getId() + "/" + fileId + "_" + key.getValue();
                System.out.println(filename);

                File file = new File(filename);

                if (file.delete()) {
                    Chunk chunk = storedChunks.remove(key);
                    this.occupiedSpace = this.occupiedSpace - chunk.data.length;
                }
            }
        }
    }
}
