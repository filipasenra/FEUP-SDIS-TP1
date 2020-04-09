package com.assigment_1.Protocol;

import javafx.util.Pair;
import com.assigment_1.PeerClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RestoreFileThread implements Runnable {
    String fileId;
    String filename;
    int numChunks;

    public RestoreFileThread(String fileId, String filename, int numChunks) {
        this.filename = filename;
        this.numChunks = numChunks;
        this.fileId = fileId;
    }

    @Override
    public void run() {
        for (int i=0; i<numChunks; i++) {
            System.out.println(i);
            Pair<String, Integer> pair = new Pair<>(fileId, i);
            byte[] chunk = PeerClient.getStorage().getRecoveredChunks().get(pair);

            if(pair == null) {
                System.out.println("Impossible to recover file because chunks are missing!");
                return;
            }

            String recovered = filename + "_";

            File file = new File(recovered);
            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    FileOutputStream fos = new FileOutputStream(recovered);
                    fos.write(chunk);

                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("File recovered successfuly!");
    }
}
