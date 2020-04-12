package com.assigment_1;

import com.assigment_1.Protocol.MessageFactory;
import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    private int overallSpace;
    private int occupiedSpace;

    private final ConcurrentHashMap<Pair<String, Integer>, Chunk> storedChunks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, FileInfo> backedUpFiles = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Pair<String, Integer>, byte[]> recoveredChunks = new ConcurrentHashMap<>();

    public Storage() {
        this.overallSpace = -1;
        this.occupiedSpace = 0;
    }

    public void setOverallSpace(int overallSpace) {
        this.overallSpace = overallSpace * 1000;

        System.out.println("ESPAÇO OCUPADO ANTES DO RECLAIM: " + this.occupiedSpace);

        while(this.overallSpace < occupiedSpace ) {

            if(this.storedChunks.size() == 0)
            {
                System.err.println("Eliminated all chunks and i'm still over capacity!\n");
            }

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

    public void decrementCountOfChunk(String fileId, int chunkNo, String senderId) {

        if(this.backedUpFiles.containsKey(fileId)){
            if(this.backedUpFiles.get(fileId).backedUpChunk.containsKey(chunkNo)){
                this.backedUpFiles.get(fileId).backedUpChunk.get(chunkNo).peersBackingUpChunk.remove(senderId);
            }
        }

    }

    public ConcurrentHashMap<Pair<String, Integer>, byte[]> getRecoveredChunks() {
        return recoveredChunks;
    }

    public ConcurrentHashMap<Pair<String, Integer>, Chunk> getStoredChunks(){
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

        if(this.backedUpFiles.containsKey(fileId)){
            if(this.backedUpFiles.get(fileId).backedUpChunk.containsKey(chunkNo)){
                if(!this.backedUpFiles.get(fileId).backedUpChunk.get(chunkNo).peersBackingUpChunk.contains(senderId))
                    this.backedUpFiles.get(fileId).backedUpChunk.get(chunkNo).peersBackingUpChunk.add(senderId);
            }
        }
    }

    public void deleteFileFromBackUpChunks(String fileId) {

        this.backedUpFiles.remove(fileId);
    }

    public void deleteFileFromStoredChunks(String fileId) {

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
                    this.occupiedSpace -= dataSize;
                }
            }
        }

        System.out.println("ESPAÇO OCUPADO DEPOIS: " + occupiedSpace);
    }

    public void addChunkToBackUp(String fileId, int chunkNo, BackUpChunk chunk) {

        if (!this.backedUpFiles.containsKey(fileId)) {
           return;
        }

        if (!this.backedUpFiles.get(fileId).backedUpChunk.containsKey(chunkNo)) {
            this.backedUpFiles.get(fileId).backedUpChunk.put(chunkNo, chunk);
        }

    }

    public void addBackedUpFiles(String fileId, FileInfo fileInfo){
        this.backedUpFiles.put(fileId, fileInfo);
    }

    public BackUpChunk getBackUpChunk(String fileId, int chunkNo) {

        if(!this.backedUpFiles.containsKey(fileId))
            return null;

        if(!this.backedUpFiles.get(fileId).backedUpChunk.containsKey(chunkNo))
            return null;

        return this.backedUpFiles.get(fileId).backedUpChunk.get(chunkNo);
    }

    public ConcurrentHashMap<String, FileInfo> getBackedUpFiles() {
        return backedUpFiles;
    }
}
