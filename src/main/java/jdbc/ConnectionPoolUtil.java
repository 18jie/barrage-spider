package jdbc;

import java.sql.Connection;
import java.util.Vector;

public class ConnectionPoolUtil{
	private static String jdbcDriver = "com.mysql.jdbc.Driver"; // 数据库驱动
    private static String dbUrl = "jdbc:mysql://localhost:3306/book_reader?serverTimezone=Asia/Shanghai"; // 数据 URL
    private static String dbUsername = "root"; // 数据库用户名
    private static String dbPassword = "123456"; // 数据库用户密码
    
    private static ConnectionPool connPool = null;
    //简单是实现单例
    private static ConnectionPoolUtil instance = new ConnectionPoolUtil();
	private ConnectionPoolUtil(){};	
 	public static ConnectionPoolUtil getInstance(){
		return instance;
	}
 	
 	static{
		connPool = new ConnectionPool(jdbcDriver, dbUrl , dbUsername, dbPassword);
		connPool.createPool();
	}


	public void initPool() {
		connPool.createPool();
	}

	public Connection getConnection() {
		return connPool.getConnection();
	}

	public void returnConnection(Connection conn) {
		connPool.returnConnection(conn);
	}

	public void closeConnectionPool() {
		connPool.closeConnectionPool();
	}
	
	public Vector getConnectionVector() {
		return connPool.getV();
	}

	public void refreshConnections() {
		connPool.freshConnectionPool();
	}
}


