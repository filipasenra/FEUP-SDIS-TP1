package com.assigment_1.Protocol;

import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;
import javafx.util.Pair;


public class ReceivedMessagesHandler implements Runnable {
    MessageFactory messageFactory;
    byte[] message;

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

        Pair<String, Integer> pair = new Pair<>(this.messageFactory.fileId, this.messageFactory.chunkNo);
        System.out.println(PeerClient.getStorage().getStoredChunksCounter().get(pair));
    }

    private void manageDeletion() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId);

        PeerClient.getStorage().deleteFileChunks(this.messageFactory.fileId);
    }


    private void manageRemove() {
        System.out.println("RECEIVED: " + this.messageFactory.version + " " + this.messageFactory.messageType + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo);

        //TODO: rest of it
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
