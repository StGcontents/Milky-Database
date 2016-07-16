package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;

/**
 * Concrete Factory for User pojos construction.
 * @author stg
 *
 */
public class UserFactory extends AbstractFactory<User> {

	private static UserFactory me;

	private UserFactory() {
	}

	public static synchronized UserFactory instance() {
		if (me == null)
			me = new UserFactory();
		return me;
	}

	@Override
	public List<User> create(ResultSet set) throws SQLException {
		List<User> users = new ArrayList<>();
		while (set.next()) {
			String id = set.getString(1), name = set.getString(2), surname = set.getString(3),
					password = set.getString(4), mail = set.getString(5);

			User user = create(id, password, name, surname, mail);
			users.add(user);
		}

		return users;
	}

	public User create(String id, String password, String name, String surname, String mail) {
		User user = new User();

		user.setId(id);
		user.setPassword(password);
		user.setName(name);
		user.setSurname(surname);
		user.setMail(mail);
		user.setAdmin(false);

		return user;
	}
}
