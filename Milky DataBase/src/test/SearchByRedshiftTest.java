package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.GalaxySearchController;
import model.AdaptableValue;
import model.Galaxy;
import model.GalaxyRepository;
import pattern.Observer;

/**
 * Test case for the redshift-based galaxy research. It maps functional requisite #7.
 * First of all, collects all names of galaxy allegedly meets the redshift restriction;
 * it then checks if the number of collected objects is less then the randomly generated limit; 
 * at last, it recovers from DB Galaxy objects in order to confront their redshift value with the one
 * randomly chosen, proving that that result is correct.
 * @author stg
 *
 */
@SuppressWarnings("rawtypes")
public class SearchByRedshiftTest {

	private GalaxyRepository repo;
	private double redshift;
	private boolean higherThan;
	private Observer<List<AdaptableValue>> observer0;
	private Observer<Galaxy> observer1;
	private List<AdaptableValue> results = new ArrayList<>();
	
	@Before
	public void initialize() {
		repo = GalaxySearchController.instance().getRepo();
		observer0 = new Observer<List<AdaptableValue>>() {
			@Override
			public void stateChanged() {
				synchronized (repo) {
					results = subject.retrieveState();
					repo.notify();
				}
			}
		};
		observer0.setSubject(repo.getNameSubject());
		
		observer1 = new Observer<Galaxy>() {
			@Override
			public void stateChanged() {
				synchronized(repo) {
					Galaxy galaxy = subject.retrieveState();
					if (higherThan)
						assertTrue(galaxy.getRedShift() > redshift);
					else
						assertTrue(galaxy.getRedShift() < redshift);
					
					repo.notify();
				}
			}
		};
		observer1.setSubject(repo.getGalaxySubject());
	}

	@Test
	public void testSearchByRedshift() {
		Random random = new Random(new Date().getTime());
		int limit = random.nextInt(99) + 1;
		redshift = Math.random();
		higherThan = random.nextBoolean();
		
		synchronized (repo) {
			GalaxySearchController.instance().searchByRedshiftValue(redshift, higherThan, limit);
			try {
				repo.wait();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
		
		assertTrue(results.size() <= limit);
		
		for (AdaptableValue value : results) {
			GalaxySearchController.instance().retrieveGalaxyByName(value.getName());
			synchronized(repo) {
				try {
					repo.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@After
	public void dispose() {
		observer0.getSubject().unsubscribe(observer0);
		observer1.getSubject().unsubscribe(observer1);
	}
}
