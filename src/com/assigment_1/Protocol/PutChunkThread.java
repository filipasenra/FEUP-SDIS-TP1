package com.assigment_1.Protocol;

import com.assigment_1.PeerClient;

import java.util.concurrent.TimeUnit;

public class PutChunkThread implements Runnable {
    private int replicationDeg;
    byte[] message;
    int counter;
    int delay;
    String fileId;
    int chunkNo;

    public PutChunkThread(int replicationDeg, byte[] message, String fileId, int chunkNo) {

        this.replicationDeg = replicationDeg;
        this.message = message;
        this.counter = 1;
        this.delay = 1;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {

        int numStoredTimes = PeerClient.getStorage().getBackUpChunk(this.fileId, this.chunkNo).getNumStoredTimes();

        System.out.println(numStoredTimes + " < " + replicationDeg + " -> para o chunk " + chunkNo + " com delay " + this.delay + " na tentativa " + this.counter);

        if (numStoredTimes < replicationDeg) {
            PeerClient.getExec().execute(new Thread(() -> PeerClient.getMDB().sendMessage(message)));

           if (this.counter < 5) {
                PeerClient.getExec().schedule(this, this.delay, TimeUnit.SECONDS);
           } else {
               PeerClient.getStorage().getBackUpChunk(this.fileId, this.chunkNo).makeInactive();
           }

            this.counter++;
            this.delay = 2 * this.delay;

        } else {
            PeerClient.getStorage().getBackUpChunk(this.fileId, this.chunkNo).makeInactive();
        }
    }
}
