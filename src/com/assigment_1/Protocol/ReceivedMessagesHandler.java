package com.assigment_1.Protocol;

import com.assigment_1.BackUpChunk;
import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class ReceivedMessagesHandler implements Runnable {
    MessageFactory messageFactory;
    byte[] message;
    private Chunk chunk;

    public ReceivedMessagesHandler(byte[] message) {
        this.message = message;

    }

    @Override
    public void run() {

        this.messageFactory = new MessageFactory();

        if (!messageFactory.parseMessage(this.message)) {
            return;
        }

        //Ignore messages
        if (PeerClient.getId().equals(messageFactory.senderId)) {
            //System.out.println("Ignored Message");
            return;
        }

        switch (messageFactory.messageType) {
            case "PUTCHUNK":
                managePutChunk();
                break;
            case "STORED":
                manageStored();
                break;
            case "GETCHUNK":
                manageGetChunk();
                break;
            case "CHUNK":
                manageChunk();
                break;
            case "DELETE":
                manageDeletion();
                break;
            case "REMOVED":
                manageRemove();
                break;
            default:
                System.err.println("NOT A VALID PROTOCOL");
        }
    }

    private void managePutChunk() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo + " " + this.messageFactory.replicationDeg);

        Chunk chunk = new Chunk(this.messageFactory.version, this.messageFactory.senderId, this.messageFactory.fileId, this.messageFactory.chunkNo, this.messageFactory.replicationDeg);

        PeerClient.getStorage().addChunkToStorage(chunk, this.messageFactory.data);
    }

    private void manageStored() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo);

        PeerClient.getStorage().updateStoredChunksCounter(this.messageFactory.fileId, this.messageFactory.chunkNo, this.messageFactory.senderId);
    }

    private void manageDeletion() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId);

        PeerClient.getStorage().deleteFileFromStoredChunks(this.messageFactory.fileId);
    }


    private void manageRemove() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo);

        BackUpChunk chunk = PeerClient.getStorage().getBackUpChunk(this.messageFactory.fileId, this.messageFactory.chunkNo);
        //Checks if remove is from one of its files
        if (chunk != null && !chunk.isActive()) {

            PeerClient.getStorage().decrementCountOfChunk(this.messageFactory.fileId, this.messageFactory.chunkNo, this.messageFactory.senderId);
            byte[] message = MessageFactory.createMessage(chunk.version, "PUTCHUNK", chunk.senderId, chunk.fileId, chunk.replicationDeg, chunk.chunkNo, chunk.data);

            Random random = new Random();
            PeerClient.getExec().schedule(new PutChunkThread(chunk.replicationDeg, message, chunk.fileId, chunk.chunkNo), random.nextInt(401), TimeUnit.MILLISECONDS);
        }
    }

    private void manageGetChunk() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo);

        PeerClient.getMDR().sendChunk(this.messageFactory.version, this.messageFactory.fileId, this.messageFactory.chunkNo);
    }

    private void manageChunk() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo);
        PeerClient.getStorage().addRecoveredChunk(this.messageFactory.fileId, this.messageFactory.chunkNo, this.messageFactory.data);
    }
}
