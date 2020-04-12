package com.assigment_1.Protocol;

import com.assigment_1.BackUpChunk;
import com.assigment_1.PeerClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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

                PeerClient.getStorage().addChunkToBackUp(fileID, chunkNr, new BackUpChunk(version, senderId, fileID, chunkNr, replicationDeg, data));
                PeerClient.getExec().execute(new PutChunkThread(replicationDeg, message, fileID, chunkNr));

                chunkNr++;
            }


            //needs empty chunk
            if((file.length() % sizeOfChunks) == 0){
                System.out.println("SENDING: " + chunkNr);
                byte[] emptyData = {};
                byte[] message = MessageFactory.createMessage(version, "PUTCHUNK", senderId, fileID, chunkNr, replicationDeg, emptyData);

                PeerClient.getStorage().addChunkToBackUp(fileID, chunkNr, new BackUpChunk(version, senderId, fileID, chunkNr, replicationDeg, emptyData));
                PeerClient.getExec().execute(new PutChunkThread(replicationDeg, message, fileID, chunkNr));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}