package com.assigment_1.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import com.assigment_1.PeerClient;
import javafx.util.Pair;
import java.util.Arrays;
import java.io.*;


public class MulticastBackupChannel extends MulticastChannel {
    int time;
    int counter;

    public MulticastBackupChannel(String INETAddress, int port) {
        super(INETAddress, port);
        this.counter = 1;
        this.time = 1;
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

                if (!PeerClient.getStorage().getStoredChunksCounter().containsKey(pair))
                    PeerClient.getStorage().getStoredChunksCounter().put(pair, 0);

                int numStoredTimes = PeerClient.getStorage().getStoredChunksCounter().get(pair);

                while (numStoredTimes < replicationDeg && counter <= 5) {
                    this.exec.schedule(new Thread(() -> this.sendChunk(message)), time, TimeUnit.SECONDS);
                    Thread.sleep(3000);

                    numStoredTimes = PeerClient.getStorage().getStoredChunksCounter().get(pair);
                    System.out.println(numStoredTimes + " < " + replicationDeg);

                    counter++;
                    time = 2 * time;
                }

                chunkNr++;
            }


        } catch (IOException |  InterruptedException e) {
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
