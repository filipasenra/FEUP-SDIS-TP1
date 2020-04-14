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

    public String getRecoveredName() {
        String extension;
        String name;
        String recovered;

        int j = filename.lastIndexOf('.');

        if (j > 0) {
            extension = filename.substring(j + 1);
            name = filename.substring(0, j);
            recovered = name + "_recovered" + "." + extension;
        } else
            recovered = filename + "_";

        return recovered;
    }

    @Override
    public void run() {

        String recovered = getRecoveredName();

        File file = new File(recovered);

        if (file.exists())
            file.delete();

        for (int i = 0; i < numChunks; i++) {

            Pair<String, Integer> pair = new Pair<>(fileId, i);

            if (!PeerClient.getStorage().getRecoveredChunks().containsKey(pair)) {
                System.out.println("Impossible to restore file because some chunks are missing!: " + pair);
                return;
            } else {

                byte[] chunk = PeerClient.getStorage().getRecoveredChunks().get(pair);

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(recovered, true);
                    fos.write(chunk);

                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(" > File recovered successfully!");
    }
}
