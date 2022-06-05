package com.neu.ces;

import java.io.*;

public class FetchSQL {
    public static void fetchSQL(String protocol) throws IOException {
        String conFile = "config/postgres/sample_" + protocol + "_config.xml";
        String cmd = "java -jar benchbase.jar -b " + protocol + " -c " + conFile + " --create=true --load=true --execute=true";
        Process ps = Runtime.getRuntime().exec(cmd);
        System.out.println(cmd);
        FileWriter fw = new FileWriter(protocol + "_benchbase.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();
        bw.write(result);
        bw.flush();
        bw.close();
    }
}
