package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import controller.DataSource;
import controller.IonFactory;

public class IonRepository extends Repository {
	
	private static IonRepository me;
	protected IonRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public static synchronized IonRepository instance(DataSource dataSource) {
		if (me == null) me = new IonRepository(dataSource);
		return me;
	}
	
	public void persist(Ion ion) throws Exception {
		
	}
	
	public void retrieveIons() throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT * FROM ion";
		Statement statement = connection.createStatement();
		ResultSet set = statement.executeQuery(query);
		IonFactory.instance().create(set);
		release(connection, statement, set);
	}
}
