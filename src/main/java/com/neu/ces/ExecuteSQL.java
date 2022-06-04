package com.neu.ces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteSQL {
    static CountDownLatch countDownLatch;
    static List<Connection> connList = new ArrayList<>();
    static long sumTime, endTime, startTime;
    static int Bsize = 200;

    public static void createData(Statement stmt, Connection conn) throws IOException, SQLException {
        String createName = "ddl-postgres.sql";
        String csql = Files.readString(Paths.get(createName));
        stmt.execute(csql);
        conn.commit();
    }

    public static void loadData(Statement stmt, Connection conn) throws IOException, SQLException {
        String loadName = "YCSBloader.sql";
        String lsql = Files.readString(Paths.get(loadName));
        stmt.execute(lsql);
        conn.commit();
    }

    static class MyRunnable implements Runnable {
        private Connection conn = null;
        private String sql = null;

        public MyRunnable(Connection conn, String sql) {
            this.conn = conn;
            this.sql = sql;
        }

        @Override
        public void run() {
            try {
                this.conn.createStatement().execute(this.sql);
                this.conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }


    public static void connInit() throws SQLException {
        for (int i = 0; i < 200; i++) {
            String url = "jdbc:postgresql://localhost:5432/benchbase?sslmode=disable&ApplicationName=ycsb&reWriteBatchedInserts=true";
            String user = "postgres";
            String passwd = "Aa123456";
            Connection conn = DriverManager.getConnection(url, user, passwd);
            conn.setAutoCommit(false);
            connList.add(conn);
        }
    }

    public static void executeSQL() {
        try {
            String url = "jdbc:postgresql://localhost:5432/benchbase?sslmode=disable&ApplicationName=ycsb&reWriteBatchedInserts=true";
            String user = "postgres";
            String passwd = "Aa123456";
            Connection conn = DriverManager.getConnection(url, user, passwd);
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            Class.forName("org.postgresql.Driver");
            connInit();

            String fileName = "YCSBworker.sql";
            String s = Files.readString(Paths.get(fileName));
            String[] SqlList = s.split("Commit;");

            for (int size = 10; size <= Bsize; size += 10) {
                sumTime = 0;
                for (int i = 0; i < 5; i++) { //Loop
                    createData(stmt, conn);
                    loadData(stmt, conn);
                    stmt.execute("SELECT * FROM usertable;");
                    stmt.execute("SELECT * FROM usertable;");
                    stmt.execute("SELECT * FROM usertable;");

                    ExecutorService es = Executors.newScheduledThreadPool(size);
                    startTime = System.currentTimeMillis();
                    for (int j = 0; j < SqlList.length / size; j++) {
                        countDownLatch = new CountDownLatch(size);
                        for (int k = 0; k < size; k++) {
                            es.submit(new MyRunnable(connList.get(k), SqlList[k + j * size]));
                        }
                        countDownLatch.await();
                    }

                    countDownLatch = new CountDownLatch(SqlList.length % size);
                    for (int j = SqlList.length % size - 1; j >= 0; j--) {
                        es.submit(new MyRunnable(connList.get(j), SqlList[SqlList.length - j - 1]));
                    }
                    countDownLatch.await();

                    endTime = System.currentTimeMillis();
                    sumTime += (endTime - startTime);

                    for (int j = 0; j < size; j++) {
                        es.shutdown();
                    }
                }
                System.out.println("Bsize " + size + " used time is " + sumTime / 5);
            }

        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
