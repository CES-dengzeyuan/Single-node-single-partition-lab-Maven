package com.neu.ces;

import java.io.IOException;
import java.sql.SQLException;

public class AllInOne {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {

//        String protocol = "ycsb";
//        FetchSQL.fetchSQL(protocol);
//        String Loader = FixSQL.fixLoadSQL(protocol);
//        String[] Worker = FixSQL.fixWorkSQL(protocol);
//        ExecuteSQL.executeSQL(protocol, Loader, Worker);

        String protocol = "tpcc";
        FetchSQL.fetchSQL(protocol);
        String[] Worker = FixSQL.fixWorkSQL(protocol);
//        System.out.println(Worker[0]);
        ExecuteSQL.executeSQL(protocol, Worker);
//        TPCCLoader.tpccloader();
    }
}
