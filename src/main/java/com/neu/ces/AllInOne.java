package com.neu.ces;

/**
 * Hello world!
 */
public class AllInOne {
    public static void main(String[] args) {
        FetchSQL.fetchSQL("a", "b", "c");
        FixSQL.fixSQL();
        ExecuteSQL.executeSQL();
    }
}
