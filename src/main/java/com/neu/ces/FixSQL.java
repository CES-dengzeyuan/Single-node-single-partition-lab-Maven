package com.neu.ces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FixSQL {
    public static String fixLoadSQL(String protocol) throws IOException {
        String loaderFile = protocol + "Loader.sql";
        File loadfile = new File(loaderFile);
        if (!loadfile.exists()) {
            System.out.println("Loader is not found!");
            return null;
        }
        String lsql = Files.readString(Paths.get(loaderFile));
        return lsql.replace("EOL", ";\n");
    }

    public static String[] fixWorkSQL(String protocol) throws IOException {
        String workerFile = protocol + "Worker.sql";
        File workfile = new File(workerFile);
        if (!workfile.exists()) {
            System.out.println("Worker is not found!");
            return null;
        }
        String lsql = Files.readString(Paths.get(workerFile));
        String[] sql = lsql.replace("EOL", ";\n").split("EOF");
        System.out.println(Arrays.toString(sql));
        return sql;
    }
}
