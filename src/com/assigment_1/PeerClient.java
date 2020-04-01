package com.assigment_1;

import com.assigment_1.PeerClient;
import com.assigment_1.Protocol.MultiCastBackUpChannel;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;


// make in root:
// javac -d out com/assigment_1/Protocol/*.java
// javac -d out com/assigment_1/*.java
// in out:
//rmiregistry &
//java com.assigment_1.PeerClient 2.0 1 Peer1 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

public class PeerClient {

    private static String id;
    private static MultiCastBackUpChannel MDB;
    private static Storage storage = new Storage(100000); ///TODO: change 100

    private static ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(250);

    public static void main(String[] args) {

        if(!parseArgs(args))
            System.exit(-1);

        executeMDB();
    }

    private static void executeMDB(){
        exec.execute(MDB);
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

        Peer obj = new Peer(version, id, MCAddress,MCPort, MDBAddress,MDBPort, MDRAddress, MDRPort);

        try {
            InterfacePeer peer = (InterfacePeer) UnicastRemoteObject.exportObject(obj, 0);
            Registry rmiReg  = LocateRegistry.getRegistry();
            rmiReg.rebind(remote_object_name, peer);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        MDB = new MultiCastBackUpChannel(MDBAddress, MDBPort);
        System.out.println("Peer ready");

        return true;
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
