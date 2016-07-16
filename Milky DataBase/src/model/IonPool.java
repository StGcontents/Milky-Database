package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.DataSource;

/**
 * Ion pojos caching management class.
 * @author stg
 *
 */
@SuppressWarnings("serial")
public class IonPool extends HashMap<Integer, Ion> {
	
	private static IonPool me;
	private IonPool() { }
	private static synchronized void initialize() {
		if (me == null) me = new IonPool();
	}
	public static void insert(Ion ion) {
		initialize();
		if (!me.containsKey(ion.getId())) 
			me.put(ion.getId(), ion);
	}
	
	public static Ion checkById(int id) {
		initialize();
		return me.get(id);
	}
	
	public static Ion getByInfo(String name, int chargedState, double line) {
		initialize();
		List<Ion> ions = getIonList();
		for (Ion ion : ions) {
			if (ion.matches(name, chargedState, line)) 
				return ion;
		}
		
		return null;
	}
	
	public static List<Ion> getIonList() {
		initialize();
		if (me.isEmpty()) {
			try {
				IonRepository.instance(DataSource.byPriviledge()).retrieveIons();
			}
			catch (Exception e) { 
				e.printStackTrace(); 
				return new ArrayList<>();
			}
		}
		
		return new ArrayList<>(me.values());
	}
}
