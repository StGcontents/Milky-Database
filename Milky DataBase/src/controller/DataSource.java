package controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataSource {
	
	private static String URI = "jdbc:postgresql://localhost/galaxy",
						  user = "postgres", password = "postgres";
	
	public Connection getConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(URI, user, password);
	}

}
