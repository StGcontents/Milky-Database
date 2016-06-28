package controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.User;

public class UserFactory extends AbstractFactory<User> {
	
	private static UserFactory me;
	private UserFactory() { }
	public static synchronized UserFactory instance() {
		if (me == null) me = new UserFactory();
		return me;
	}

	@Override
	public List<User> create(ResultSet set) {
		List<User> users = new ArrayList<>();
		try {
			while (set.next()) {
				try {
					User user = new User();
					user.setId(set.getString(1));
					user.setName(set.getString(2));
					user.setSurname(set.getString(3));
					user.setPassword(set.getString(4));
					user.setMail(set.getString(5));
					user.setAdmin(set.getBoolean(6));
					
					users.add(user);
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	

}
