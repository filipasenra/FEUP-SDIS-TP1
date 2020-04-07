package com.assigment_1.Protocol;

import com.assigment_1.PeerClient;
import javafx.util.Pair;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MulticastBackupChannel extends MulticastChannel {

    public MulticastBackupChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void backupFile(double version, String senderId, String filepath, int replicationDeg) {

        File file = new File(filepath);

        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            int chunkNr = 0;
            int bytesAmount;
            byte[] buffer = new byte[sizeOfChunks];
            String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());

            while ((bytesAmount = bis.read(buffer)) > 0) {

                byte[] data = Arrays.copyOf(buffer, bytesAmount);
                byte[] message = MessageFactory.createMessage(version, "PUTCHUNK", senderId, fileID, chunkNr, replicationDeg, data);

                Pair <String, Integer> pair = new Pair <> (fileID, chunkNr);

                if (!PeerClient.getStorage().getStoredChunksCounter().containsKey(pair)) {
                    ArrayList<String> aux = new ArrayList<>();
                    PeerClient.getStorage().getStoredChunksCounter().put(pair, aux);
                }

                PeerClient.getExec().execute(new PutChunkThread(replicationDeg, message, fileID, chunkNr));

                chunkNr++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
