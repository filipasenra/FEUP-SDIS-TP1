package com.assigment_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Storage implements Serializable {

    private ArrayList<Chunk> storedChunks = new ArrayList<>();

    private int overallSpace;
    private int occupiedSpace = 0;

    public Storage(int overallSpace) {
        this.overallSpace = overallSpace;
    }

    public void addChuckToStorage(Chunk chunk) {

        if ((this.overallSpace - this.occupiedSpace) < chunk.data.length) {
            System.out.println("Peer doesn't have space for chunk number " + chunk.chunkNo + " of " + chunk.fileId + " from " + chunk.senderId);
            return;
        }

        this.storedChunks.add(chunk);
        this.occupiedSpace += chunk.data.length;

        String filename = PeerClient.getId() + "/" + chunk.fileId + "_" + chunk.chunkNo;

        File file = new File(filename);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(chunk.data);

            //SEND CHUCK STORAGE CONFIRMATION MESSAGE
            PeerClient.getMC().confirmStore(chunk.version, PeerClient.getId(), chunk.fileId, chunk.chunkNo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
