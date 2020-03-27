package com.assigment_1;

import java.util.Arrays;

//in out:
//java com.assigment_1.TestClient Peer1 BACKUP "/home/filipasenra/Desktop/Sem título 1.odt" 2

public class TestClient {

    public static void main(String[] args) {

        if(!parseArgs(args))
            System.exit(-1);

    }

    private static boolean parseArgs(String[] args) {

        if(args.length < 2)
        {
            System.err.println("usage: <rmi_peer_ap> <sub_protocol> <arguments_of_protocol>\n" +
                    "Protocols available: BACKUP, RESTORE, DELETE, RECLAIM, STATE");
            return false;
        }

        String rmi_peer_ap = args[0];
        String sub_protocol = args[1];
        String[] arguments = Arrays.copyOfRange(args, 2, args.length);

        TestClientHandler testClientHandler = new TestClientHandler(rmi_peer_ap);

        switch (sub_protocol){
            case "BACKUP":
                return testClientHandler.doBackUp(arguments);
            case "RESTORE":
                //TODO
            case "DELETE":
                //TODO
                break;
            case "RECLAIM":
                //TODO
                break;
            case "STATE":
                //TODO
                break;
            default:
                System.err.println("NOT A VALID PROTOCOL");
                return false;
        }

        return true;
    }

}