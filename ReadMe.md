# FEUP SDIS

Project for the Distributed Systems (SDIS) class of the Master in Informatics and Computer Engineering (MIEIC) at the Faculty of Engineering of the University of Porto (FEUP).

In this project we developed a distributed backup service for a local area network (LAN). The idea is to use the free disk space of the computers in a LAN for backing up files in other computers in the same LAN. The service is provided by servers in an environment that is assumed cooperative (rather than hostile). Nevertheless, each server retains control over its own disks and, if needed, may reclaim the space it made available for backing up other computers' files. 

Made by [Cláudia Inês Costa Martins](https://git.fe.up.pt/up201704136) and [Ana Filipa Campos Senra](https://git.fe.up.pt/up201704077).

## Compile

#### In folder src:
1. export PATH_TO_FX=<path_to_fx>/javafx-sdk-11.0.2/lib

2. javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out com/assigment_1/Protocol/*.java

3. javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out com/assigment_1/*.java

## Run

### PeerClient

#### In folder out:

1. rmiregistry &

2. export PATH_TO_FX=<path_to_fx>/javafx-sdk-11.0.2/lib

3. java --module-path $PATH_TO_FX --add-modules javafx.controls com.assigment_1.PeerClient <version> <server id> <access_point> <MC_IP_address> <MC_port> <MDB_IP_address> <MDB_port> <MDR_IP_address> <MDR_port>

   **Ex:** java --module-path $PATH_TO_FX --add-modules javafx.controls com.assigment_1.PeerClient 2.0 1 Peer1 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

### TestApp

#### In folder out:
1. export PATH_TO_FX=<path_to_fx>/javafx-sdk-11.0.2/lib

2. java --module-path $PATH_TO_FX --add-modules javafx.controls com.assigment_1.TestApp <rmi_peer_ap> <sub_protocol> <arguments_of_protocol>
   
   2.1. <rmi_peer_ap> BACKUP <file_path> <replication_degree>
   
   2.2. <rmi_peer_ap> DELETE <file_path>
   
   2.3. <rmi_peer_ap> RESTORE <file_path>
   
   2.4. <rmi_peer_ap> RECLAIM <disk_space>
   
   **Ex:** java --module-path $PATH_TO_FX --add-modules javafx.controls com.assigment_1.TestApp Peer1 DELETE "/home/filipasenra/Desktop/Sem título 1.odt"
