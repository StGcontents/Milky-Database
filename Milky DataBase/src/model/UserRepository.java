package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import controller.DataSource;
import controller.UserFactory;
import exception.UserExistsException;
import pattern.ExceptionSubject;

public class UserRepository extends UniRepository<User> {
	
	private ExceptionSubject subject;
	public ExceptionSubject getExceptionSubject() { return this.subject; }
	
	public UserRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		subject = new ExceptionSubject();
	}	
	
	public User retrieveUserById(String userID) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String query = "SELECT * FROM user_admin WHERE id LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, userID);
		
		ResultSet set = statement.executeQuery();
		User user = UserFactory.instance().create(set).get(0);
		
		connection.commit();
		
		release(connection, statement, set);
		return user;
	}
	
	public void logUser(String userID, String password) throws Exception {
		Connection connection = dataSource.getConnection();
			
		int result;
		String query = "SELECT is_admin FROM user_admin WHERE id LIKE ? AND password LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, userID);
		statement.setString(2, password);
			
		ResultSet set = statement.executeQuery();
		if (set.next()) 
			result = set.getBoolean(1) ? DataSource.ADMIN : DataSource.COMMON;
		else result = DataSource.INVALID;
			
		release(connection, statement, set);
		
		Priviledge.instance().setPriviledge(result);
	}
	
	@Override
	public void persist(User user) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String check = "SELECT count(*) FROM user_admin WHERE id LIKE ?";
		
		PreparedStatement qStatement = connection.prepareStatement(check);
		qStatement.setString(1, user.getId());
		ResultSet set = qStatement.executeQuery();
		
		set.next();
		if (set.getInt(1) == 0) {
			String insert = "INSERT INTO user_admin(id , password , name , surname , mail,is_admin) "
					+ "values (?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(insert);
		
			statement.setString(1, user.getId());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getSurname());
			statement.setString(5, user.getMail());
			statement.setBoolean(6, user.isAdmin());
	
			statement.executeUpdate();
			
			release(connection, qStatement, set, statement);
		}
		else {
			release(connection, qStatement, set);
			throw new UserExistsException();
		}
		
		connection.commit();
		
		subject.setState(null);
	}

	@Override
	public void delete(User entity) throws Exception {
	}

	@Override
	public void update(User entity) throws Exception {		
	}

	@Override
	public List<User> read(PreparedStatement statement) throws Exception {
		return null;
	}
}
