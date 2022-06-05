package com.neu.ces;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FixSQL {
    public static void fixSQL() {
        try {
            String loaderFile = "TPCCLoader.sql";
            String workerFile = "TPCCWorker.sql";
            File loadfile = new File(loaderFile);
            if (!loadfile.exists()) {
                System.out.print("Loader is not found!");
                return;
            }
            File workfile = new File(workerFile);
            if (!workfile.exists()) {
                System.out.print("Loader is not found!");
                return;
            }
            
            String lsql = Files.readString(Paths.get(loaderFile));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
