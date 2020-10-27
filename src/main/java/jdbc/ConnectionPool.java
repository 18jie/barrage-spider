package jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

public class ConnectionPool {
    private int maxConnection = 11; //最大连接数
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver"; // 数据库驱动
    private String dbUrl = "jdbc:mysql://localhost:3306/book_reader"; // 数据 URL
    private String dbUsername = "root"; // 数据库用户名
    private String dbPassword = "123456"; // 数据库用户密码
    private int initialConnections = 10; // 连接池的初始大小
    private int incrementalConnections = 5; // 连接池自动增加的大小
    private Vector<ConnectionExample> v = null;//连接集合

    //为了测试遍历连接集合
    public Vector<ConnectionExample> getV() {
        return v;
    }

    public ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername, String dbPassword) {
        this.dbPassword = dbPassword;
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
    }

    /**
     * 第二步：创建连接池
     */
    public synchronized void createPool() {
        if (v != null) {
            return;
        }
        try {
            //实例化数据库驱动
            driverNewInstance(this.jdbcDriver);
            v = new Vector<>();
            //创建连接池的初始化连接,并存放在Vector向量中
            for (int x = 0; x < this.initialConnections; x++) {
                Connection conn = newConnection();
                ConnectionExample ce = new ConnectionExample(conn);
                ce.setExtra(false);
                v.addElement(ce);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第一步：创建连接
     *
     * @return
     * @throws SQLException
     */
    private Connection newConnection() throws SQLException {
        //创建连接
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        System.out.println("创建数据库连接成功");
        return conn;
    }

    /**
     * 获取连接
     */
    public synchronized Connection getConnection() {
        if (v == null) {
            System.out.println("连接池未创建,无法获取连接");
            return null;
        }
        Connection conn = getUseableConnection();
        if (conn == null) {
            /**
             * 无可用连接，
             * 方式一：连接数达到上线，等待别人释放连接后获取
             * 方式二：在连接数未达到上线的情况下，创建5个新的链接
             */
            if (v.size() >= this.maxConnection) {
                System.out.println("连接数已达最大，需要等待4000ms，再尝试获取连接");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("当前连接数不够用，且还未达到最大连接数，开始创建新的5个连接");
                try {
                    //自动增加5个连接
                    for (int x = 0; x < incrementalConnections; x++) {
                        if (v.size() < this.maxConnection) {
                            Connection connew = newConnection();
                            ConnectionExample ce = new ConnectionExample(connew);
                            ce.setExtra(true);
                            v.addElement(ce);
                        } else {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            conn = getUseableConnection();
        }
        return conn;

    }

    /**
     * 回收链接
     *
     * @param conn
     */
    public void returnConnection(Connection conn) {
        if (v == null) {
            System.out.println("连接池未创建,无法回收连接");
        }
        Iterator<ConnectionExample> enumerate = v.iterator();
        while (enumerate.hasNext()) {
            ConnectionExample ce = enumerate.next();
            if (conn == ce.getCon()) {
                ce.setBusy(true);
                break;
            }
        }
    }

    /**
     * 刷新连接池，处理掉额外创建的连接,回复初始连接数
     */
    public synchronized void freshConnectionPool() {
        if (v == null) {
            System.out.println("连接池未创建,无法刷新");
        }
        Iterator<ConnectionExample> enumerate = v.iterator();
        while (enumerate.hasNext()) {
            ConnectionExample ce = enumerate.next();
            if (!ce.isBusy()) { //当前连接在使用中
                try {
                    Thread.sleep(5000);//等待5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //是额外创建的连接,则销毁
            if (ce.isExtra()) {
                closeConnection(ce.getCon());
                enumerate.remove();
            } else {
                //是初始连接，将该线程设置为可用
                ce.setBusy(true);
            }
        }
    }

    /**
     * 关闭连接池
     */
    public synchronized void closeConnectionPool() {
        if (v == null) {
            System.out.println(" 连接池不存在，无法关闭 !");
            return;
        }
        Iterator<ConnectionExample> it = v.iterator();
        while (it.hasNext()) {
            ConnectionExample ce = it.next();
            if (!ce.isBusy()) { //连接为false不可用，正在使用
                try {
                    Thread.sleep(5000);//等待5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            closeConnection(ce.getCon());
            it.remove();//纯remove(),只会使Vector集合为[ ] ，仍需要将v引用到null上了，以同意判断null值的条件
        }
        v = null;
    }

    /**
     * 销毁连接
     *
     * @param conn
     */
    private void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取可用的连接
     *
     * @return
     */
    private Connection getUseableConnection() {
        ConnectionExample ce = v.stream().filter(one -> one.isBusy() == true).findFirst().orElse(null);
        if (ce == null) {
            System.out.println("无可用的连接");
            return null;
        } else {
            ce.setBusy(false);
            return ce.getCon();
        }
    }

    /**
     * 实例化驱动
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    private void driverNewInstance(String jdbcDriver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Driver driver = (Driver) Class.forName(this.jdbcDriver).newInstance();
        DriverManager.registerDriver(driver);
    }
}

