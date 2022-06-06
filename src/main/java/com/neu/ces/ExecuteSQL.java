package com.neu.ces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteSQL {
    static String url = "jdbc:postgresql://localhost:5432/benchbase?sslmode=disable&ApplicationName=ycsb&reWriteBatchedInserts=true";
    static String user = "postgres";
    static String passwd = "Aa123456";
    static CountDownLatch countDownLatch;
    static List<Connection> connList = new ArrayList<>();
    static long sumTime, endTime, startTime;
    static int Bsize = 64;
    static int Rollbacknum = 0;
    static int Abortnum = 0;

    public static void createData(Statement stmt, Connection conn, String protocol) throws IOException, SQLException {
        String createName = protocol + "-ddl-postgres.sql";
        String csql = Files.readString(Paths.get(createName));
        stmt.execute(csql);
        conn.commit();
    }

    public static void loadData(Statement stmt, Connection conn, String Loader) throws IOException, SQLException {
        stmt.execute(Loader);
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
//                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    static class MyRunnabletpcc implements Runnable {
        private Connection conn = null;
        private String sql = null;

        public MyRunnabletpcc(Connection conn, String sql) {
            this.conn = conn;
            this.sql = sql;
        }

        @Override
        public void run() {
            try {
                String[] sqlList = this.sql.split("\n");
                Iterator<String> it = Arrays.stream(sqlList).iterator();
                while (it.hasNext()) {
                    String itt = it.next();
                    this.conn.createStatement().execute(itt);
                }
                this.conn.commit();
            } catch (SQLException e) {
                Abortnum += 1;
                try {
//                    Rollbacknum += 1;
                    this.conn.rollback();
                } catch (SQLException ex) {
//                    ex.printStackTrace();
                }
//                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    // 创建数据库会话池，默认开了64个，对应Bsize大小
    public static void connInit() throws SQLException {
        for (int i = 0; i < Bsize; i++) {
            Connection conn = DriverManager.getConnection(url, user, passwd);
            conn.setAutoCommit(false);
            connList.add(conn);
        }
    }

    // For TPC-C
    public static void executeSQL(String protocol, String[] Worker) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
        Connection conn = DriverManager.getConnection(url, user, passwd);
        conn.setAutoCommit(false);
        Class.forName("org.postgresql.Driver");
        connInit();

        for (int size = 2; size <= Bsize; size += 2) {
            Abortnum = 0;
            sumTime = 0;
            for (int i = 0; i < 1; i++) { //Loop
                TPCCLoader.tpccloader();

                // 创建线程池，线程数量对应Batch_size，并且会设置门闩阈值
                ExecutorService es = Executors.newScheduledThreadPool(size);

                startTime = System.currentTimeMillis();
                for (int j = 0; j < Worker.length / size; j++) {
                    countDownLatch = new CountDownLatch(size);
                    for (int k = 0; k < size; k++) {
                        es.submit(new MyRunnabletpcc(connList.get(k), Worker[k + j * size]));
                    }
                    countDownLatch.await();
                }

                countDownLatch = new CountDownLatch(Worker.length % size);
                for (int j = Worker.length % size - 1; j >= 0; j--) {
                    es.submit(new MyRunnabletpcc(connList.get(j), Worker[Worker.length - j - 1]));
                }
                countDownLatch.await();

                endTime = System.currentTimeMillis();
                sumTime += (endTime - startTime);

                for (int j = 0; j < size; j++) {
                    es.shutdown();
                }
            }
            System.out.println("Abort num is " + Abortnum);
            System.out.println("Bsize " + size + " used time is " + sumTime / 1);
        }

    }

    public static void executeSQLTpcc(String Loader) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
        Connection conn = DriverManager.getConnection(url, user, passwd);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        Class.forName("org.postgresql.Driver");
        loadData(stmt, conn, Loader);
        conn.close();
    }

    public static void initdb() throws SQLException, ClassNotFoundException, IOException {
        Connection conn = DriverManager.getConnection(url, user, passwd);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        Class.forName("org.postgresql.Driver");
        createData(stmt, conn, "tpcc");
        conn.close();
    }


    public static void executeSQL(String protocol, String Loader, String[] Worker) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
        Connection conn = DriverManager.getConnection(url, user, passwd);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        Class.forName("org.postgresql.Driver");
        connInit();

        for (int size = 10; size <= Bsize; size += 10) {
            sumTime = 0;
            for (int i = 0; i < 1; i++) { //Loop
                createData(stmt, conn, protocol);
                loadData(stmt, conn, Loader);

                // 创建线程池，线程数量对应Batch_size，并且会设置门闩阈值
                ExecutorService es = Executors.newScheduledThreadPool(size);

                startTime = System.currentTimeMillis();
                for (int j = 0; j < Worker.length / size; j++) {
                    countDownLatch = new CountDownLatch(size);
                    for (int k = 0; k < size; k++) {
                        es.submit(new MyRunnable(connList.get(k), Worker[k + j * size]));
                    }
                    countDownLatch.await();
                }

                countDownLatch = new CountDownLatch(Worker.length % size);
                for (int j = Worker.length % size - 1; j >= 0; j--) {
                    es.submit(new MyRunnable(connList.get(j), Worker[Worker.length - j - 1]));
                }
                countDownLatch.await();

                endTime = System.currentTimeMillis();
                sumTime += (endTime - startTime);

                for (int j = 0; j < size; j++) {
                    es.shutdown();
                }
            }
            System.out.println("Bsize " + size + " used time is " + sumTime / 1);
        }
    }
}
