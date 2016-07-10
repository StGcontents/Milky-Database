package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import controller.DataSource;
import controller.IonFactory;

public class IonRepository extends UniRepository<Ion> {
	
	private static IonRepository me;
	protected IonRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public static synchronized IonRepository instance(DataSource dataSource) {
		if (me == null) me = new IonRepository(dataSource);
		return me;
	}
	
	@Override public void persist(Ion ion) throws Exception { }
	
	public void retrieveIons() throws Exception {
		Connection connection = dataSource.getConnection();
		String query = "SELECT * FROM ion";
		PreparedStatement statement = connection.prepareStatement(query);
		
		read(statement);
		
		release(connection);
	}
	@Override
	public List<Ion> read(PreparedStatement statement) throws Exception {
		ResultSet set = statement.executeQuery();
		List<Ion> list = IonFactory.instance().create(set);
		release(statement, set);
		return list;
	}
	
	@Override public void update(Ion entity) throws Exception { }
	
	@Override public void delete(Ion entity) throws Exception { }
}
