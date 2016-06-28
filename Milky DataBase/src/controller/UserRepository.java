package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.User;

public class UserRepository extends Repository {
	
	private static UserRepository me;
	private UserRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public static synchronized UserRepository instance(DataSource dataSource) {
		if (me == null) me = new UserRepository(dataSource);
		return me;
	}
	
	public User retrieveUserById(String userID) throws Exception {
		Connection connection = dataSource.getConnection();
		
		String query = "SELECT * FROM user_admin WHERE id LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, userID);
		
		ResultSet set = statement.executeQuery();
		User user = UserFactory.instance().create(set).get(0);
		
		release(connection, statement, set);
		return user;
	}
	
	public int logUser(String userID, String password) throws Exception {
		Connection connection = dataSource.getConnection();
		
		String query = "SELECT is_admin FROM user_admin WHERE id LIKE ? AND password LIKE ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, userID);
		statement.setString(2, password);
		
		ResultSet set = statement.executeQuery();
		int result;
		if (set.next()) 
			result = set.getBoolean(1) ? DataSource.ADMIN : DataSource.COMMON;
		else result = DataSource.INVALID;
		
		release(connection, statement, set);
		return result;
	}

}
