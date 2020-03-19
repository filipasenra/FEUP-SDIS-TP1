package com.assigment_1;


import com.assigment_1.Protocol.MultiCastBackUpChannel;
import com.assigment_1.Protocol.MultiCastChannel;

public class Peer implements InterfacePeer {
    MultiCastBackUpChannel MDB;

    public Peer(String MCAddress, int MCPort, String MDBAddress, int MDBPort, String MDRAddress, int MDRPort) {

        MDB = new MultiCastBackUpChannel(MDBAddress, MDBPort);
    }

    @Override
    public void backup(String file_path, int replication_degree) {
        System.out.println("> BACKUP SERVICE");
        System.out.println("  > File path: " + file_path);
        System.out.println("  > Replication Degree: " + replication_degree);

    }
}
