package com.assigment_1.Protocol;

import com.assigment_1.PeerClient;
import javafx.util.Pair;

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
        String recovered = filename + "_";
        File file = new File(recovered);

        for (int i = 0; i < numChunks; i++) {
            System.out.println(i);
            Pair<String, Integer> pair = new Pair<>(fileId, i);

            System.out.println(pair);
            if (pair == null) {
                System.out.println("Impossible to recover file because chunks are missing!");
                return;
            }

            byte[] chunk = PeerClient.getStorage().getRecoveredChunks().get(pair);

            try {
                if (file.exists()) {
                    file.delete();

                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(recovered, true);
                fos.write(chunk);

                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("File recovered successfuly!");
    }
}
