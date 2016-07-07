package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import controller.DataSource;
import controller.UserFactory;

public class UserRepository extends Repository {
	
	public UserRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}	
	
	public User retrieveUserById(String userID) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		
		String query = "SELECT * FROM user_admin WHERE id LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query, ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
		statement.setString(1, userID);
		
		ResultSet set = statement.executeQuery();
		User user = UserFactory.instance().create(set).get(0);
		
		connection.commit();
		
		release(connection, statement, set);
		return user;
	}
	
	public void logUser(String userID, String password) {
		int result;
		try {
			Connection connection = dataSource.getConnection();
			
			String query = "SELECT is_admin FROM user_admin WHERE id LIKE ? AND password LIKE ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, userID);
			statement.setString(2, password);
			
			ResultSet set = statement.executeQuery();
			if (set.next()) 
				result = set.getBoolean(1) ? DataSource.ADMIN : DataSource.COMMON;
			else result = DataSource.INVALID;
			
			release(connection, statement, set);
		}
		catch (Exception e) {
			e.printStackTrace();
			result = DataSource.INVALID;
		}
		
		Priviledge.instance().setPriviledge(result);
	}
	
	private void persistUser(Connection connection, User utente) throws Exception {
		String insert = "INSERT INTO user_admin(id , password , name , surname , mail) "
				+ "values (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(insert);
		
		statement.setString(1, utente.getId());
		statement.setString(2, utente.getPassword());
		statement.setString(3, utente.getName());
		statement.setString(4, utente.getSurname());
		statement.setString(5, utente.getMail());
	
		
		statement.executeUpdate();
		release(statement);
	}
}
