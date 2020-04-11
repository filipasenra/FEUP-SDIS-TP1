package com.assigment_1;

import com.assigment_1.Protocol.MessageFactory;
import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    private int overallSpace;
    private int occupiedSpace;
    private HashMap<Pair<String, Integer>, Chunk> storedChunks = new HashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> storedChunksCounter = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Pair<String, Integer>, byte[]> recoveredChunks = new ConcurrentHashMap<>();

    public Storage() {
        this.overallSpace = -1;
        this.occupiedSpace = 0;
    }

    public void setOverallSpace(int overallSpace) {
        this.overallSpace = overallSpace * 1000;

        System.out.println("ESPAÇO OCUPADO ANTES DO RECLAIM: " + this.occupiedSpace);

        while(this.overallSpace < occupiedSpace) {
            Map.Entry<Pair<String, Integer>, Chunk> entry = this.storedChunks.entrySet().iterator().next();
            Chunk chunkToEliminate = entry.getValue();

            int dataSize = 0;
            try {
                dataSize = chunkToEliminate.getData().length;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (chunkToEliminate.deleteData()) {
                this.storedChunks.remove(entry.getKey(), entry.getValue());
                this.occupiedSpace -= dataSize;

                byte[] message = MessageFactory.createMessage(chunkToEliminate.version, "REMOVED", PeerClient.getId(), chunkToEliminate.fileId, chunkToEliminate.chunkNo);
                PeerClient.getMC().sendMessage(message);
            }

        }

        System.out.println("ESPAÇO OCUPADO DEPOIS DO RECLAIM: " + this.occupiedSpace);

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

    public void addChunkToStorage(Chunk chunk, byte[] data) {

        System.out.println("ESPAÇO OCUPADO ANTES BACKUP: " + this.occupiedSpace);
        if ((overallSpace != -1) && ((this.overallSpace - this.occupiedSpace) < data.length)) {
            System.out.println("Peer doesn't have space for chunk number " + chunk.chunkNo + " of " + chunk.fileId + " from " + chunk.senderId);
            return;
        }

        Pair<String, Integer> pair = new Pair<>(chunk.fileId, chunk.chunkNo);

        if (!storedChunks.containsKey(pair)) {
            this.storedChunks.put(pair, chunk);
            this.occupiedSpace += data.length;

            String filename = PeerClient.getId() + "/" + chunk.fileId + "_" + chunk.chunkNo;

            File file = new File(filename);
            try {

                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(data);

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
                Chunk chunkToEliminate = storedChunks.get(key);

                int dataSize = 0;

                try {
                    dataSize = chunkToEliminate.getData().length;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (chunkToEliminate.deleteData()) {
                    storedChunks.remove(key);
                    this.occupiedSpace = this.occupiedSpace - dataSize;
                }
            }
        }

        System.out.println("ESPAÇO OCUPADO DEPOIS: " + occupiedSpace);
    }
}
