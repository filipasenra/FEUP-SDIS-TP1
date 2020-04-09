package com.assigment_1.Protocol;

import javafx.util.Pair;
import com.assigment_1.PeerClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

        if (file.exists()) {
            file.delete();
        }

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
                String str = new String(chunk, "UTF-8");
                System.out.println(str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }




            try {
                if (!file.exists()) {
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
