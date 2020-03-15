package com.assigment_1;

import com.assigment_1.Protocol.BackUpProtocol;

public class Peer implements InterfacePeer {
    @Override
    public void backup(String file_path, int replication_degree) {
        System.out.println("> BACKUP SERVICE");
        System.out.println("  > File path: " + file_path);
        System.out.println("  > Replication Degree: " + replication_degree);

        //TODO: make this fields correct
        BackUpProtocol backUpProtocol = new BackUpProtocol(1.0, 1, 1, replication_degree, null);

    }
}
