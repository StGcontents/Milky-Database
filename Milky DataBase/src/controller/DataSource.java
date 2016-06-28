package controller;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class DataSource {
	
	private static String URI = "jdbc:postgresql://localhost/galaxy";
	public static final int ADMIN = 0, COMMON = 1, READONLY = 2, INVALID = -1;
	
	public static DataSource instance(int priviledgeLevel) {
		switch (priviledgeLevel) {
		case 0: //Administrator
			return AdminDataSource.instance();
		case 1: //Common user
			return CommonDataSource.instance();
		case 2: default:
			return ReadOnlyDataSource.instance();
		}
	}
	
	public Connection getConnection() throws Exception {
		loadDriver();
		return DriverManager.getConnection(URI, getUser(), getPassword());
	}
	
	private void loadDriver() throws Exception { Class.forName("org.postgresql.Driver"); }
	
	protected abstract String getUser() ;
	protected abstract String getPassword() ;
	
	private static class ReadOnlyDataSource extends DataSource {
		private static ReadOnlyDataSource me;
		private ReadOnlyDataSource() { }
		protected static DataSource instance() {
			if (ReadOnlyDataSource.me == null) ReadOnlyDataSource.me = new ReadOnlyDataSource();
			return ReadOnlyDataSource.me;
		}
		
		private static String user = "postgres", password = "postgres";
		
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
		
		private static String user = "user", password = "password";
		
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
		
		private static String user = "admin", password = "postgres";
		
		@Override protected String getUser() { return AdminDataSource.user; }
		@Override protected String getPassword() { return AdminDataSource.password; }
	}
}
