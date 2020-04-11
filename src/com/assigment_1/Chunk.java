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

    public Chunk(double version, String senderId, String fileId, int chunkNo, int replicationDeg) {
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
    }

    public boolean deleteData() {

        File file = new File(PeerClient.getId() + "/" + fileId + "_" + chunkNo);

        return file.delete();
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
