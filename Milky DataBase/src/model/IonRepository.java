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
	
	@Override 
	public void persist(Ion ion) throws Exception { 
		//TODO: since there's no way we can dynamically obtain ion information,
		//ion table is populated manually; therefore, this method implementation is
		//not required.
	}
	
	@Override
	public List<Ion> read(PreparedStatement statement) throws Exception {
		ResultSet set = statement.executeQuery();
		List<Ion> list = IonFactory.instance().create(set);
		release(statement, set);
		return list;
	}
	
	public void retrieveIons() throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "SELECT * FROM ion";
			statement = connection.prepareStatement(query, 
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY,
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
		
			read(statement);
		}
		finally {
			release(statement, connection);
		}
	}
	
	@Override 
	public void update(Ion entity) throws Exception { 
		//TODO: not implemented for the same reason read is not implemented
	}
	
	@Override 
	public void delete(Ion entity) throws Exception {
		//TODO: not implemented for the same reason read is not implemented
	}
}
