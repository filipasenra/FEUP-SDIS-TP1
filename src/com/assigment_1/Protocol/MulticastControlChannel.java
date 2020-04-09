package com.assigment_1.Protocol;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import com.assigment_1.PeerClient;
import javafx.util.Pair;

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

        PeerClient.getStorage().deleteStoredChunksCounter(fileID);

        byte[] message = MessageFactory.createMessage(version, "DELETE", senderId, fileID);

        PeerClient.getExec().schedule(new Thread(() -> this.sendMessage(message)), 1, TimeUnit.SECONDS);
    }

    public void restoreFile(double version, String senderId, String filepath) {
        File file = new File(filepath);
        String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());
        ConcurrentHashMap<Pair<String, Integer>, ArrayList<String>> storedChunksCounter = PeerClient.getStorage().getStoredChunksCounter();

        //CHECK IF ALL THE CHUNKS OF THE FILE WERE PREVIOUSLY STORED DURING BACKUP
        //IF NOT IT'S IMPOSSIBLE TO RECOVER THE FILE AND THE RESTORE ENDS HERE
        ArrayList<ArrayList<String>> values = new ArrayList<>(storedChunksCounter.values());
        for (ArrayList<String> value : values) {
            if(value.size() == 0) {
                System.out.println("Impossible to restore file because some chunks missing!\n");
                return;
            }
        }


        ArrayList<Pair<String, Integer>> keys = new ArrayList<>(storedChunksCounter.keySet());
        for (Pair<String, Integer> pair : keys) {
            if (pair.getKey().equals(fileID)) {
                System.out.println(fileID + "_" + pair.getValue());
                byte[] message = MessageFactory.createMessage(version, "GETCHUNK", senderId, fileID, pair.getValue());
                PeerClient.getExec().execute(new Thread(() -> this.sendMessage(message)));
            }
        }
    }
}
