package com.neu.ces;

public class AllInOne {
    public static void main(String[] args) {
        FetchSQL.fetchSQL("a", "b");
        FixSQL.fixSQL();
        ExecuteSQL.executeSQL();
    }
}
