package test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.googlecode.jeneratedata.text.RandomStringGenerator;

import controller.DataSource;
import controller.UserFactory;
import exception.UserExistsException;
import model.User;
import model.UserRepository;

/**
 * Test case for the insertion of a new user profile for this platform. It (partially) maps
 * functional requisites #2 and #3, the first mentioning fields of interest and the latter 
 * mentioning this is an administration operation.
 * This test randomly generates user's credential and information, and performs two different
 * operations according to whether or not user ID and password fields violates user_admin
 * table constraints: if this is true, the operation is bound to fail, and this test expects
 * an SQLException to be thrown; otherwise, it inserts twice its generated values, the first time to
 * prove the user will be inserted into the DB and the second time to prove it will instead
 * throw an UserExistsException, as expected. At the end of the test, the inserted values are
 * deleted from DB.
 * Note that it has been chosen to test UserRepository only; that's for two main reasons:
 * 1) AddUserController basically manages thrown Exceptions to update GUI, doing no other
 * 	task;
 * 2) unlike other test examples, in which thread management was necessary to retrieve
 * 	actual results that are asynchronously returned by the application, this behavior is
 * 	immediately testable, and therefore we opted for a simple implementation.
 * @author stg
 *
 */
public class AddUserTest {
	
	private UserRepository repo;
	private User user;
	private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
	private RandomStringGenerator idGenerator, passwordGenerator, stringGenerator;
	private boolean bornToFail = false;
	
	@Rule
	public final ExpectedException expExc = ExpectedException.none();
	
	@Before
	public void initialize() {
		repo = new UserRepository(DataSource.testOnly());
		
		Random random = new Random(new Date().getTime());
		int count = random.nextInt(20);
		++count;
		bornToFail = count < 6;
		idGenerator = new RandomStringGenerator(count, alphabet);
				
		count = random.nextInt(20);
		++count;
		bornToFail = bornToFail || count < 6;
		passwordGenerator = new RandomStringGenerator(count, alphabet);
		
		count = random.nextInt(20);
		++count;
		stringGenerator = new RandomStringGenerator(count, alphabet);
	}
	
	@Test
	public void testPersist() throws Exception {
		String id = idGenerator.generate(), 
				password = passwordGenerator.generate(), 
				name = stringGenerator.generate(), 
				surname = stringGenerator.generate(), 
				mail = stringGenerator.generate();
		
		user = UserFactory.instance().create(id, password, name, surname, mail);
		
		if (bornToFail) 
			expExc.expect(SQLException.class);
		
		repo.persist(user);
		if (bornToFail) return;
		
		expExc.expect(UserExistsException.class);
		repo.persist(user);
	}
	
	@After
	public void dispose() {
		if (user != null) {
			try { repo.delete(user); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}
