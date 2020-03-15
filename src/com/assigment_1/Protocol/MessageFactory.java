package com.assigment_1.Protocol;

final class MessageFactory {
    //<Version> <MessageType> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>
    static String CRLF = "\r\n";

    private MessageFactory(){}

    public static String createMessage(double version, String messageType, int senderId, int fileId, int chunkNo, int replicationDeg){

        return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDeg + CRLF + CRLF;
    }

    public static String createMessage(double version, String messageType, int senderId, int fileId, int chunkNo, int replicationDeg, byte[] body){

        return createMessage(version, messageType, senderId, fileId, chunkNo, replicationDeg) + body;
    }
}
