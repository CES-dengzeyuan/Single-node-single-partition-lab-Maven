package com.neu.ces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

public class TPCCLoader {
    public static void tpccloader() throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        ExecuteSQL.initdb();
        String[] tpccList = {"Item", "Ware", "Stock", "Dist", "Cust", "Hist", "Open", "New", "Order"};
        Iterator<String> it = Arrays.stream(tpccList).iterator();
        while (it.hasNext()) {
            String loadfile = "tpcc" + it.next() + "Loader.sql";
            String lsql = FixSQL.fixLoadByName(loadfile);
//            System.out.println(lsql);
            ExecuteSQL.executeSQLTpcc(lsql);
//            System.out.println("TPCC loading finish!");
        }
    }
}
