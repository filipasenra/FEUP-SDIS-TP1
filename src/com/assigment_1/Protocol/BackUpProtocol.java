package com.assigment_1.Protocol;

public class BackUpProtocol {
    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>

    double version;
    int senderId;
    int fileId;
    int replicationDeg;
    byte[][] split_file_data;

    public BackUpProtocol(double version, int senderId, int fileId, int replicationDeg, byte[][] split_file_data) {
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.replicationDeg = replicationDeg;
        this.split_file_data = split_file_data;
    }

    public void sending(){

        //TODO cycle initiating the putchunk communicating and receiving confirmation messages
    }
}
