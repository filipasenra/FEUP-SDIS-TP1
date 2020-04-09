package com.assigment_1;

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

    public byte[] getData() {
        return data;
    }
}
