package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jeneratedata.text.RandomStringGenerator;

import controller.GalaxySearchController;
import model.AdaptableValue;
import pattern.Observer;
import model.GalaxyRepository;

/**
 * Test case for galaxy research by (partial) name routine. It maps functional requisite
 * #5; note that it actually states "result will show galaxy information", but that's not
 * what is being tested here. As a matter of fact, the implemented galaxy search by name
 * only shows to the user a list of galaxy names; on item selection, it will be shown to the user
 * the actual informations about the galaxy. Truth is, this behavior is not so interesting,
 * nor possible, to actually test, because involves GUI operations; so, we decided to instead 
 * prove that all the names listed after searching by partial key actually contains the inserted 
 * search key.
 * @author stg
 *
 */
@SuppressWarnings("rawtypes")
public class SearchByNameTest {
	
	private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
	private Observer<List<AdaptableValue>> observer;
	private List<AdaptableValue> results = new ArrayList<>();
	private GalaxyRepository repo;	
	
	@Before
	public void initialize() {
		repo = GalaxySearchController.instance().getRepo();
		observer = new Observer<List<AdaptableValue>>() {
			@Override
			public void stateChanged() {
				synchronized (repo) {
					results = subject.retrieveState();
					repo.notify();
				}
			}
		};
		observer.setSubject(repo.getNameSubject());
	}
	
	@Test
	public void testSearchByName() {
		Random random = new Random(new Date().getTime());
		int count =  random.nextInt(3);
		++count;
		RandomStringGenerator generator = new RandomStringGenerator(count, alphabet.toCharArray());
		String partial = generator.generate();
		synchronized (repo) {
			GalaxySearchController.instance().searchNames(partial);
			try {
				repo.wait();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
		
		for (AdaptableValue value : results)
			assertTrue(value.isNameLike(partial));
	}

	@After
	public void dispose() {
		observer.getSubject().unsubscribe(observer);
	}
}
