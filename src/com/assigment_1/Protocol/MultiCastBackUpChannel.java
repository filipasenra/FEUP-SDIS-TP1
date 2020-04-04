package com.assigment_1.Protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class MultiCastBackUpChannel extends MultiCastChannel {

    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>

    public MultiCastBackUpChannel(String INETAddress, int port) {
        super(INETAddress, port);
    }

    public void backUpFile(double version, String senderId, String filepath, int replicationDeg) {

        File file = new File(filepath);

        try (
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis)
        ){
            int chunkNr = 0;
            int bytesAmount;
            byte[] buffer = new byte[com.assigment_1.Protocol.MultiCastChannel.sizeOfChunks];
            String fileID = this.generateId(file.getName(), file.lastModified(), file.getParent());

            while ((bytesAmount = bis.read(buffer)) > 0) {

                byte[] data = Arrays.copyOf(buffer, bytesAmount);
                byte[] message = MessageFactory.createMessage(version, "PUTCHUNK", senderId, fileID, chunkNr, replicationDeg, data);

                this.exec.execute(new Thread(() -> this.sendChunk(message)));

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

    private String sha256(String data){
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
