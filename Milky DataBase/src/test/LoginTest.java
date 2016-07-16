package test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jeneratedata.text.RandomStringGenerator;

import controller.DataSource;
import model.Privilege;
import model.UserRepository;
import pattern.Observer;

/**
 * Test case for login routine. This test actually maps functional requisites from #1 to #3:
 *  - #1 is logging into the application: this test randomly chooses between 3 log options: 
 *  	log as administrator, log as common user, or generate random invalid data access; 
 *  - #2 is partially tested through the assert at the end of this test, that checks out
 *  	privilege level of the user according to the type of user randomly chosen;
 *   - #3 is tested via privilege level assert, because logging in as an administrator
 *   	returns to the application a different privilege level value, used for GUI 
 *   	construction and functionalities reachability (see DataSource class for more
 *   	information). 
 *   We chose to test UserRepository instead of LoginController because of Thread issues;
 *   it does not matter though, because the main log behavior is implemented through 
 *   Subject/Observer interaction between UserRepository and Privilege objects. 
 * @author stg
 *
 */
public class LoginTest {
	
	private final static String IDS[] = { "administrator", "common" };
	private final static String PASSWORDS[] = { "password", "common" };
	private final static int LEVELS[] = { DataSource.ADMIN, DataSource.COMMON, DataSource.INVALID };
	private int chosen;
	private String id, password;
	private Observer<Integer> observer;
	
	@Before
	public void initialize() {
		observer = new Observer<Integer>() {
			@Override
			public void stateChanged() {
				int priviledgeLevel = subject.retrieveState();
				assertEquals(LEVELS[chosen], priviledgeLevel);
			}
		};
		observer.setSubject(Privilege.instance());
		
		Random random = new Random(new Date().getTime());
		chosen = random.nextInt(3);
		if (chosen == 2) {
			int count = random.nextInt(20);
			RandomStringGenerator generator = new RandomStringGenerator(count, new char[] { 'a', 'b', 'c', 'd', 'e', 'f' });
			//Bound to be invalid
			id = generator.generate();
			count = random.nextInt(20);
			password = generator.generate();
		}
		else {
			id = IDS[chosen];
			password = PASSWORDS[chosen];
		}
	}
	
	@Test
	public void testLogin() {
		try { 
			new UserRepository(DataSource.testOnly()).logUser(id, password);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void dispose() {
		observer.getSubject().unsubscribe(observer);
	}
}
