package com.assigment_1.Protocol;

import javafx.util.Pair;
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
        Pair<String, Integer> pair = new Pair <> (this.fileId, this.chunkNo);
        int numStoredTimes = PeerClient.getStorage().getStoredChunksCounter().get(pair).size();
        System.out.println(numStoredTimes + " < " + replicationDeg + " -> para o chunk " + chunkNo + " com delay " + this.delay + " na tentativa " + this.counter);

        if (numStoredTimes < replicationDeg) {
            PeerClient.getExec().execute(new Thread(() -> PeerClient.getMDB().sendMessage(message)));

           if (this.counter < 5) {
                PeerClient.getExec().schedule(this, this.delay, TimeUnit.SECONDS);
           }

            this.counter++;
            this.delay = 2 * this.delay;
        }
    }
}
