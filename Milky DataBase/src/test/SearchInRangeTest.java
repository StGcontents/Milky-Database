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
import model.Galaxy.Coordinates;
import pattern.Observer;

/**
 * Test case for galaxy search by range. It maps functional requisite #6.
 * Center coordinates, range and result limit are randomly generated and then
 * used to do a research; then, every galaxy name collected is used to retrieve
 * a Galaxy object from DB and it is proven that it meets the imposed prerequisites.
 * @author stg
 *
 */
@SuppressWarnings("rawtypes")
public class SearchInRangeTest {
	
	private GalaxyRepository repo;
	private Coordinates center;
	private double range;
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
					double d = testIsInRange(galaxy.getCoordinates());
					assertTrue(d < range);
					
					repo.notify();
				}
			}
		};
		observer1.setSubject(repo.getGalaxySubject());
	}

	private double testIsInRange(Coordinates coord) {
		double d = Math.abs(
				Math.acos(
						Math.sin(transformRA(coord)) *
						Math.sin(transformRA(center)) +
						Math.cos(transformRA(coord)) *
						Math.cos(transformRA(center)) *
						Math.cos(transformDec(coord) - transformDec(center))));
		return d;
	}
	
	private double transformRA(Coordinates coord) {
		return 15 * (coord.getRightAscensionHours() + coord.getRightAscensionMinutes() / 60.0 + coord.getRightAscensionSeconds() / 3600.0);
	}
	
	private double transformDec(Coordinates coord) {
		double d = coord.getDegrees() + coord.getArcMinutes() / 60.0 + coord.getArcSeconds() / 3600.0;
		return coord.getSign() ? d : d * (-1);
	}
	
	@Test
	public void testSearchInRange() {
		Random random = new Random(new Date().getTime());
		int h = random.nextInt(),
				min = random.nextInt(),
				deg = random.nextInt(),
				arcmin = random.nextInt(),
				limit = random.nextInt(99) + 1;
		double sec = random.nextDouble(),
				arcsec = random.nextDouble();
		range = random.nextDouble();
		boolean sign = random.nextBoolean();
		
		center = new Coordinates(h, min, sec, sign, deg, arcmin, arcsec);
		
		synchronized (repo) {
			GalaxySearchController.instance().searchInRange(center, range, limit);
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
