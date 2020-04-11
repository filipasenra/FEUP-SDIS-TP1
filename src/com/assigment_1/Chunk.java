package com.assigment_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Chunk {

    double version;
    String senderId;
    String fileId;
    int chunkNo;
    int replicationDeg;
    byte[] data;

    public Chunk(double version, String senderId, String fileId, int chunkNo, int replicationDeg, byte[] data) {
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
        this.data = data;
    }

    public byte[] getData() throws IOException {
        File file = new File(PeerClient.getId() + "/" + fileId + "_" + chunkNo);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        return data;
    }
}
