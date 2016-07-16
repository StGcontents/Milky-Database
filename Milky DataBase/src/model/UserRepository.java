package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import controller.DataSource;
import exception.UserExistsException;
import pattern.ExceptionSubject;

/**
 * Repository implementation for User objects persisting, retrieval, update and deletion.
 * @author stg
 *
 */
public class UserRepository extends UniRepository<User> {
	
	private ExceptionSubject subject;
	public ExceptionSubject getExceptionSubject() { return this.subject; }
	
	public UserRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		subject = new ExceptionSubject();
	}	
	
	@Override
	public void persist(User user) throws Exception {
		Connection connection = null;
		PreparedStatement qStatement = null,
				statement = null;
		ResultSet set = null;
		boolean committed = false;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			String check = "SELECT count(*) FROM user_admin WHERE id LIKE ?";
			
			qStatement = connection.prepareStatement(check);
			qStatement.setString(1, user.getId());
			set = qStatement.executeQuery();
			
			set.next();
			if (set.getInt(1) == 0) {
				String insert = "INSERT INTO user_admin(id , password , name , surname , mail,is_admin) "
						+ "values (?, ?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(insert);
				
				statement.setString(1, user.getId());
				statement.setString(2, user.getPassword());
				statement.setString(3, user.getName());
				statement.setString(4, user.getSurname());
				statement.setString(5, user.getMail());
				statement.setBoolean(6, user.isAdmin());
				
				statement.executeUpdate();
			}
			else 
				throw new UserExistsException();
			
			connection.commit();
			committed = true;
			subject.setState(null);
		}
		finally {
			if (!committed)
				connection.rollback();
			release(statement, set, qStatement, connection);
		}
	}
	
	@Override
	public List<User> read(PreparedStatement statement) throws Exception {
		//TODO: pretty much useless, no functional requirement asks about
		//user information RETRIEVING
		return null;
	}
	
	public void logUser(String userID, String password) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet set = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(true);
			
			int result;
			String query = "SELECT is_admin FROM user_admin WHERE id LIKE ? AND password LIKE ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, userID);
			statement.setString(2, password);
			
			set = statement.executeQuery();
			if (set.next()) 
				result = set.getBoolean(1) ? DataSource.ADMIN : DataSource.COMMON;
			else result = DataSource.INVALID;
			
			Privilege.instance().setPriviledge(result);
		}
		finally {
			release(set, statement, connection);
		}
	}
	
	@Override
	public void delete(User entity) throws Exception {
		//TODO: not requested
	}

	@Override
	public void update(User entity) throws Exception {
		//TODO: not requested
	}
}
