package model;

import java.util.HashMap;

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

}
