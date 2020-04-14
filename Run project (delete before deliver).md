### Make

#### In folder src:
1. export PATH_TO_FX=<path_to_fx>/javafx-sdk-11.0.2/lib

2. javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out com/assigment_1/Protocol/*.java

3. javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out com/assigment_1/*.java


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
