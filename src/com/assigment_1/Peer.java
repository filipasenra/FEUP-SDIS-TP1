package com.assigment_1;

import com.assigment_1.Protocol.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Peer implements InterfacePeer {
    private String id;
    private Double version;

    private MulticastBackupChannel MDB;
    private MulticastControlChannel MC;
    private MulticastDataRecoveryChannel MDR;
    private ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(250);

    public Peer(Double version, String id, MulticastControlChannel MC, MulticastBackupChannel MDB, MulticastDataRecoveryChannel MDR) {
        this.version = version;
        this.id = id;
        this.MDB = MDB;
        this.MC = MC;
        this.MDR = MDR;
    }

    @Override
    public void backup(String file_path, int replication_degree) {
        System.out.println("\nBACKUP SERVICE");
        System.out.println(" > File path: " + file_path);
        System.out.println(" > Replication Degree: " + replication_degree);

        exec.execute(new Thread(() -> MDB.backupFile(this.version, this.id, file_path, replication_degree)));
    }

    @Override
    public void deletion(String file_path) {
        System.out.println("\nDELETION SERVICE");
        System.out.println(" > File path: " + file_path);

        exec.execute(new Thread(() -> MC.deleteFile(this.version, this.id, file_path)));
    }

    @Override
    public void restore(String file_path) {
        System.out.println("\nRESTORE SERVICE");
        System.out.println(" > File path: " + file_path);

        exec.execute(new Thread(() -> MC.restoreFile(this.version, this.id, file_path)));
    }
}
