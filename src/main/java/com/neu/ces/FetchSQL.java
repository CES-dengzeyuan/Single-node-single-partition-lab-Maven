package com.neu.ces;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FetchSQL {
    public static void fetchSQL(String jarfile, String protocol, String confile) {
        try {
            String jarFile = "benchbase.jar";
            String Protocol = "tpcc";
            String conFile = "config/postgres/sample_" + Protocol + "_config.xml";
            String cmd = "java -jar " + jarFile + " -b " + Protocol + " -c " + conFile + " --create=true --load=true --execute=true";
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
