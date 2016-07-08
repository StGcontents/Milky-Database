package controller;

import java.sql.Connection;
import java.sql.DriverManager;

import model.Priviledge;

public abstract class DataSource {
	
	private static String URI = "jdbc:postgresql://localhost/galaxy";
	public static final int ADMIN = 0, COMMON = 1, READONLY = 2, INVALID = -1;
	
	public static synchronized DataSource instance(int priviledgeLevel) {
		try { loadDriver(); } 
		catch (Exception e) { 
			e.printStackTrace();
			return null;
		}
		
		switch (priviledgeLevel) {
		case 0: //Administrator
			return AdminDataSource.instance();
		case 1: //Common user
			return CommonDataSource.instance();
		case 2: default:
			return ReadOnlyDataSource.instance();
		}
	}
	
	public static synchronized DataSource byPriviledge() {
		int priviledgeLevel = Priviledge.instance().retrieveState();
		return instance(priviledgeLevel);
	}
	
	private static void loadDriver() throws Exception { Class.forName("org.postgresql.Driver"); }
	
	private LazyConnection connection;
	private Connection actualConnection;
	
	public static DataSource readOnly() {
		return instance(DataSource.READONLY);
	}
	
	/*************************************************************************************
	 * @return Connection: a Connection object representing a new DB session.			 *
	 * 																					 *
	 * Session establishing is managed by this method; if a Connection is requested for	 *
	 * the first time, it is simply created. If, on the other hand, a connection was	 *
	 * previously requested and established, this method checks if it is still up before *
	 * generating another one. This mechanism is also based on Connection implementation *
	 * LazyConnection; see class description for further information. 					 *
	 *************************************************************************************/
	public Connection getConnection() throws Exception {
		try {
			synchronized (actualConnection) {
				if (connection.isClosed()) {
					actualConnection = DriverManager.getConnection(URI, getUser(), getPassword());
					connection = new LazyConnection(actualConnection);
				}
				else connection.keepAlive(); 
			}
		}
		catch (Exception e) {
			actualConnection = DriverManager.getConnection(URI, getUser(), getPassword());
			connection = new LazyConnection(actualConnection);
		}
		
		return connection;
	}
	
	protected abstract String getUser() ;
	protected abstract String getPassword() ;
	
	private static class ReadOnlyDataSource extends DataSource {
		private static ReadOnlyDataSource me;
		private ReadOnlyDataSource() { }
		protected static DataSource instance() {
			if (ReadOnlyDataSource.me == null) ReadOnlyDataSource.me = new ReadOnlyDataSource();
			return ReadOnlyDataSource.me;
		}
		
		private static String user = "rd_only", password = "login";
		
		@Override protected String getUser() { return ReadOnlyDataSource.user; }
		@Override protected String getPassword() { return ReadOnlyDataSource.password; }
	}
	
	private static class CommonDataSource extends DataSource {
		private static CommonDataSource me;
		private CommonDataSource() { }
		protected static DataSource instance() {
			if (CommonDataSource.me == null) CommonDataSource.me = new CommonDataSource();
			return CommonDataSource.me;
		}
		
		private static String user = "common", password = "user";
		
		@Override protected String getUser() { return CommonDataSource.user; }
		@Override protected String getPassword() { return CommonDataSource.password; }
	}
	
	private static class AdminDataSource extends DataSource {
		private static AdminDataSource me;
		private AdminDataSource() { }
		protected static DataSource instance() {
			if (AdminDataSource.me == null) AdminDataSource.me = new AdminDataSource();
			return AdminDataSource.me;
		}
		
		private static String user = "admin", password = "password";
		
		@Override protected String getUser() { return AdminDataSource.user; }
		@Override protected String getPassword() { return AdminDataSource.password; }
	}
}
