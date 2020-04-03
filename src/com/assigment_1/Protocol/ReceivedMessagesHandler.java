package com.assigment_1.Protocol;

import com.assigment_1.Chunk;
import com.assigment_1.PeerClient;

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
            System.err.println("NOT A VALID PROTOCOL");
            return;
        }

        //A peer must never store the chunks of its own files.
        if(PeerClient.getId().equals(messageFactory.senderId))
            return;

        switch (messageFactory.messageType) {
            case "PUTCHUNK":
                managePutChunk();
                break;
            case "STORED":
                //TODO
            case "GETCHUNK":
                //TODO
                break;
            case "CHUNK":
                //TODO
                break;
            case "DELETE":
                //TODO
                break;
            case "REMOVED":
                //TODO
                break;
            default:
                System.err.println("NOT A VALID PROTOCOL");
        }
    }

    private void managePutChunk() {

        System.out.println("RECEIVED " + this.messageFactory.messageType + ": " + this.messageFactory.version + " " + this.messageFactory.senderId + " " + this.messageFactory.fileId + " " + this.messageFactory.chunkNo + " " + this.messageFactory.replicationDeg);

        Chunk chunk = new Chunk(this.messageFactory.version, this.messageFactory.senderId, this.messageFactory.fileId, this.messageFactory.chunkNo, this.messageFactory.replicationDeg, this.messageFactory.data);
        PeerClient.getStorage().addChuckToStorage(chunk);

    }
}
