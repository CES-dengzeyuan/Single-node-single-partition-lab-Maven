package com.neu.ces;

public class AllInOne {
    public static void main(String[] args) {
        FetchSQL.fetchSQL("tpcc");
        FixSQL.fixSQL();
        ExecuteSQL.executeSQL();
    }
}
