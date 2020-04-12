package com.assigment_1;

import java.io.IOException;
import java.util.ArrayList;

public class BackUpChunk extends Chunk {

    public byte[] data;
    public ArrayList<String> peersBackingUpChunk = new ArrayList<>();

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
    public byte[] getData() throws IOException {
        return this.data;
    }

    public int getNumStoredTimes() {

        return this.peersBackingUpChunk.size();
    }
}
