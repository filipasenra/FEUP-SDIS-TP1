package com.assigment_1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfacePeer extends Remote {

    void backup(String file_path, int replication_degree) throws RemoteException;

    void deletion(String file_path) throws RemoteException;

    public void restore(String file_path) throws RemoteException;
}
