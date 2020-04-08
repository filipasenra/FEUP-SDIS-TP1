package com.assigment_1;

import java.rmi.RemoteException;
import com.assigment_1.Protocol.*;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ScheduledThreadPoolExecutor;

// make in src:
// export PATH_TO_FX=/home/filipasenra/openjfx-11.0.2_linux-x64_bin-sdk/javafx-sdk-11.0.2/lib
// javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out com/assigment_1/Protocol/*.java
// javac --module-path $PATH_TO_FX --add-modules javafx.controls  -d out com/assigment_1/*.java
// in out:
//rmiregistry &
//java --module-path $PATH_TO_FX --add-modules javafx.controls com.assigment_1.PeerClient 2.0 1 Peer1 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

//java com.assigment_1.PeerClient 2.0 1 Peer1 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003
//java com.assigment_1.PeerClient 2.0 2 Peer2 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003
//java com.assigment_1.PeerClient 2.0 3 Peer3 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003
//java com.assigment_1.PeerClient 2.0 4 Peer4 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003


public class PeerClient {

    private static String id;
    private static MulticastBackupChannel MDB;
    public static MulticastControlChannel MC;
    private static Storage storage = new Storage(150000); ///TODO: change 100

    private static ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(250);

    public static void main(String[] args) {
        if(!parseArgs(args))
            System.exit(-1);
    }

    private static boolean parseArgs(String[] args) {

        if(args.length != 9){
            System.err.println("usage: Peer <version> <server id> <access_point> <MC_IP_address> <MC_port> <MDB_IP_address> <MDB_port> <MDR_IP_address> <MDR_port>");
            return false;
        }

        Double version = Double.parseDouble(args[0]);
        id = args[1];
        String remote_object_name = args[2];
        String MCAddress = args[3];
        int MCPort = Integer.parseInt(args[4]);
        String MDBAddress = args[5];
        int MDBPort = Integer.parseInt(args[6]);
        String MDRAddress = args[7];
        int MDRPort = Integer.parseInt(args[8]);

        MDB = new MulticastBackupChannel(MDBAddress, MDBPort);
        MC = new MulticastControlChannel(MCAddress, MCPort);

        Peer obj = new Peer(version, id, MC, MDB, MDRAddress, MDRPort);

        try {
            InterfacePeer peer = (InterfacePeer) UnicastRemoteObject.exportObject(obj, 0);
            Registry rmiReg  = LocateRegistry.getRegistry();
            rmiReg.rebind(remote_object_name, peer);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("Peer ready");

        exec.execute(MDB);
        exec.execute(MC);

        return true;
    }

    public static MulticastControlChannel getMC() {
        return MC;
    }

    public static MulticastBackupChannel getMDB() {
        return MDB;
    }

    public static String getId() {
        return id;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static ScheduledThreadPoolExecutor getExec() {
        return exec;
    }
}
