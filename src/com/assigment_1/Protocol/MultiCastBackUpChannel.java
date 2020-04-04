package com.assigment_1.Protocol;

import com.assigment_1.PeerClient;
import com.sun.javafx.image.BytePixelSetter;
import javafx.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MultiCastBackUpChannel extends MultiCastChannel {
    int time;
    int counter;

    public MultiCastBackUpChannel(String INETAddress, int port) {
        super(INETAddress, port);
        this.counter = 1;
        this.time = 1;
    }

    public void backUpFile(double version, String senderId, String filepath, int replicationDeg) {

        File file = new File(filepath);

        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            int chunkNr = 0;
            int bytesAmount;
            byte[] buffer = new byte[com.assigment_1.Protocol.MultiCastChannel.sizeOfChunks];
            String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());

            while ((bytesAmount = bis.read(buffer)) > 0) {

                byte[] data = Arrays.copyOf(buffer, bytesAmount);
                byte[] message = MessageFactory.createMessage(version, "PUTCHUNK", senderId, fileID, chunkNr, replicationDeg, data);

                if (!PeerClient.getStorage().getStoredChuncksCounter().containsKey(new Pair(fileID, chunkNr))) {
                    PeerClient.getStorage().getStoredChuncksCounter().put(new Pair(fileID, chunkNr), 0);
                }

                int numStoredTimes = PeerClient.getStorage().getStoredChuncksCounter().get(new Pair(fileID, chunkNr));

                System.out.println(numStoredTimes + " < " + counter);
                while (numStoredTimes < replicationDeg && counter <= 5) {

                    this.exec.schedule(new Thread(() -> this.sendChunk(message)), time, TimeUnit.SECONDS);
                    counter++;
                    time = 2 * time;
                }

                chunkNr++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateId(String filename, long lastModified, String owner) {

        String fileID = filename + '-' + lastModified + '-' + owner;

        return sha256(fileID);
    }

    private String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte singleByte : hash) {
                String hex = Integer.toHexString(0xff & singleByte);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
