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
	
	public static DataSource readOnly() {
		return instance(DataSource.READONLY);
	}
	
	public Connection getConnection() throws Exception {
		try {
			synchronized (connection) {
				if (connection.isClosed()) {
					connection = new LazyConnection(DriverManager.getConnection(URI, getUser(), getPassword()));
				}
				else connection.keepAlive(); 
			}
		}
		catch (Exception e) {
			connection = new LazyConnection(DriverManager.getConnection(URI, getUser(), getPassword()));
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
