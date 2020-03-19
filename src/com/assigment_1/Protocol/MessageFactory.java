package com.assigment_1.Protocol;

import java.util.Arrays;

final class MessageFactory {
    //<Version> <MessageType> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>
    static String CRLF = "\r\n";

    private MessageFactory(){}

    public static byte[] createMessage(double version, String messageType, String senderId, String fileId, int chunkNo, int replicationDeg){

        return (version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDeg + CRLF + CRLF).getBytes();
    }

    public static byte[] createMessage(double version, String messageType, String senderId, String fileId, int chunkNo, int replicationDeg, byte[] body){

        byte[] header = createMessage(version, messageType, senderId, fileId, chunkNo, replicationDeg);
        byte[] message = new byte[header.length + body.length];

        System.arraycopy(header,0,message,0, header.length);
        System.arraycopy(body,0,message,header.length,body.length);

        return message;
    }
}
