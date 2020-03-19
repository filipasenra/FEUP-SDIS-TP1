package com.assigment_1;

import com.assigment_1.Protocol.MultiCastBackUpChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Peer implements InterfacePeer {
    private String id;
    private Double version;

    public Peer(Double version, String id, String MCAddress, int MCPort, String MDBAddress, int MDBPort, String MDRAddress, int MDRPort) {

        this.version = version;
        this.id = id;
    }

    @Override
    public void backup(String file_path, int replication_degree) {
        System.out.println("> BACKUP SERVICE");
        System.out.println("  > File path: " + file_path);
        System.out.println("  > Replication Degree: " + replication_degree);

    }
}
