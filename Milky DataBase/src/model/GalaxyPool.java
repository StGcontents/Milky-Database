package model;

import java.util.HashMap;

@SuppressWarnings("serial")
public class GalaxyPool extends HashMap<String, Galaxy> {
	
	private static GalaxyPool me;
	private GalaxyPool() { }
	private static synchronized void initialize() {
		if (me == null) me = new GalaxyPool();
	}
	
	public static void insert(Galaxy galaxy) {
		initialize();
		me.put(galaxy.getName(), galaxy);
	}
	
	public static Galaxy getByName(String name) {
		initialize();
		return me.get(name);
	}
}
