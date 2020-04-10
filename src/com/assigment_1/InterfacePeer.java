package com.assigment_1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfacePeer extends Remote {

    void backup(String file_path, int replication_degree) throws RemoteException;

    void deletion(String file_path) throws RemoteException;

    void restore(String file_path) throws RemoteException;

    void reclaim(String disk_space) throws RemoteException;
}
