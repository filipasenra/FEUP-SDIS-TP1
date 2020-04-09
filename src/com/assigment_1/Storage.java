package com.assigment_1;

import java.io.*;
import javafx.util.Pair;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    private int overallSpace;
    private int occupiedSpace = 0;
    private HashMap<Pair<String, Integer>, Chunk> storedChunks = new HashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> storedChunksCounter = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, byte[]> recoveredChunks = new ConcurrentHashMap<>();

    public Storage(int overallSpace) {
        this.overallSpace = overallSpace;
    }

    public ConcurrentHashMap<Pair<String, Integer>, byte[]> getRecoveredChunks() {
        return recoveredChunks;
    }

    public ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> getStoredChunksCounter() {
        return storedChunksCounter;
    }

    public HashMap<Pair<String, Integer>, Chunk> getStoredChunks(){
        return storedChunks;
    }

    public void addRecoveredChunk(String fileId, int chunkNo, byte[] data) {
        Pair<String, Integer> pair = new Pair<>(fileId, chunkNo);
        if(!recoveredChunks.containsKey(pair)) {
            recoveredChunks.put(pair, data);
        }
    }

    public void addChunkToStorage(Chunk chunk) {

        System.out.println("ESPAÇO OCUPADO ANTES BACKUP: " + this.occupiedSpace);
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

                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(chunk.data);

                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println("ESPAÇO OCUPADO DEPOIS BACKUP: " + this.occupiedSpace);

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

        System.out.println("ESPAÇO OCUPADO ANTES: " + occupiedSpace);
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

        System.out.println("ESPAÇO OCUPADO DEPOIS: " + occupiedSpace);

         System.out.println(storedChunks);
    }
}
