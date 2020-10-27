package jdbc;

import java.sql.Connection;

public class ConnectionExample {
	private boolean busy = true;//判断是否可用
	private boolean extra;//是否是额外连接
	private Connection con = null;
	
	public ConnectionExample(Connection conn) {
		this.con=conn;
	}

	public Connection getCon() {
		return con;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public boolean isExtra() {
		return extra;
	}

	public void setExtra(boolean extra) {
		this.extra = extra;
	}
	
}

