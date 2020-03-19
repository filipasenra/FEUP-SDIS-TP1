package com.assigment_1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClientHandler {

    InterfacePeer peer;

    public TestClientHandler(String rmi_peer_ap) {

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry();
            this.peer = (InterfacePeer) reg.lookup(rmi_peer_ap);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public boolean doBackUp(String[] arguments){

        if(arguments.length != 2) {
            System.err.println("Wrong no. of arguments");
            System.err.println("Usage: <rmi_peer_ap> BACKUP <file_path> <replication_degree>");
            return false;
        }

        try {
            peer.backup(arguments[0], Integer.parseInt(arguments[1]));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return true;
    }
}
