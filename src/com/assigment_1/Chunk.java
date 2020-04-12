package com.assigment_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Chunk {

    public double version;
    public String senderId;
    public String fileId;
    public int chunkNo;
    public int replicationDeg;

    public ArrayList<String> peersBackingUpChunk = new ArrayList<>();

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

    public int getNumStoredTimes() {

        return this.peersBackingUpChunk.size();
    }

    public String getId() {
        return this.fileId + "_" + this.chunkNo;
    }
}
