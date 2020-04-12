package com.assigment_1.Protocol;

import com.assigment_1.BackUpChunk;
import com.assigment_1.FileInfo;
import com.assigment_1.PeerClient;

import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

        PeerClient.getStorage().deleteFileFromBackUpChunks(fileID);

        byte[] message = MessageFactory.createMessage(version, "DELETE", senderId, fileID);

        PeerClient.getExec().execute(new Thread(() -> this.sendMessage(message)));
    }

    public void restoreFile(double version, String senderId, String filepath) {
        File file = new File(filepath);
        String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());
        ConcurrentHashMap<String, FileInfo> backedUpFiles = PeerClient.getStorage().getBackedUpFiles();

        //CHECK IF ALL THE CHUNKS OF THE FILE WERE PREVIOUSLY STORED DURING BACKUP
        //IF NOT IT'S IMPOSSIBLE TO RECOVER THE FILE AND THE RESTORE ENDS HERE

        if (!backedUpFiles.containsKey(fileID) || backedUpFiles.get(fileID).backedUpChunk.size() == 0) {
            System.out.println("Impossible to restore file because some or all chunks are not backed up!\n");
            return;
        }

        //IF ALL THE CHUNKS OF THE FILE WERE SAVED, PEER SENDS REQUESTS TO RECOVER THEM

        int numChunks = 0;
        for(Map.Entry<Integer, BackUpChunk> fileInfoEntry : backedUpFiles.get(fileID).backedUpChunk.entrySet() ) {

            numChunks++;
            byte[] message = MessageFactory.createMessage(version, "GETCHUNK", senderId, fileID, fileInfoEntry.getKey());
            PeerClient.getExec().execute(new Thread(() -> this.sendMessage(message)));

        }

        //RECOVERING THE ALL FILE AFTER REQUESTING THEIR CHUNKS
        PeerClient.getExec().schedule(new RestoreFileThread(fileID, filepath, numChunks), 10, TimeUnit.SECONDS);
    }
}
