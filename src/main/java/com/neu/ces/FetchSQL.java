package com.neu.ces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FetchSQL {
    public static void fetchSQL(String protocol) throws IOException {
        String conFile = "config/postgres/sample_" + protocol + "_config.xml";
        String cmd = "java -jar benchbase.jar -b " + protocol + " -c " + conFile + " --create=true --load=true --execute=true";
        Process ps = Runtime.getRuntime().exec(cmd);
        System.out.println(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();
        System.out.println(result);
    }
}
