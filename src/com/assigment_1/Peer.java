package com.assigment_1;

import com.assigment_1.Protocol.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Peer implements InterfacePeer {
    private String id;
    private Double version;

    private MultiCastBackUpChannel MDB;
    private MulticastControlChannel MC;
    private ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(250);

    public Peer(Double version, String id, MulticastControlChannel MC, MultiCastBackUpChannel MDB, String MDRAddress, int MDRPort) {
        this.version = version;
        this.id = id;
        this.MDB = MDB;
        this.MC = MC;
    }

    @Override
    public void backup(String file_path, int replication_degree) {
        System.out.println("\nBACKUP SERVICE");
        System.out.println(" > File path: " + file_path);
        System.out.println(" > Replication Degree: " + replication_degree);

        exec.execute(new Thread(() -> MDB.backUpFile(this.version, this.id, file_path, replication_degree)));
    }
}
