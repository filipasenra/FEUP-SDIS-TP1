package com.assigment_1;

public class BackUpChunk extends Chunk {

    public byte[] data;
    boolean active = false;

    public BackUpChunk(double version, String senderId, String fileId, int chunkNo, int replicationDeg, byte[] data) {
        super(version, senderId, fileId, chunkNo, replicationDeg);
        this.data = data;
    }

    @Override
    public boolean deleteData() {
        this.data = null;
        return true;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    public void makeInactive() {
        active = false;
    }

    public void makeActive() {
        active = true;
    }

    public boolean isInactive() {
        return !active;
    }
}
