package com.neu.ces;

import java.io.IOException;

public class AllInOne {
    public static void main(String[] args) throws IOException {
        String protocol = "tpcc";
        FetchSQL.fetchSQL(protocol);
        FixSQL.fixLoadSQL(protocol);
        FixSQL.fixWorkSQL(protocol);
//        ExecuteSQL.executeSQL();
    }
}
