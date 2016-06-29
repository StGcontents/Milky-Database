package controller;

public class GalaxySearchController {
	
	private static GalaxySearchController me;
	private GalaxySearchController() {
		
	}
	public static synchronized GalaxySearchController instance() {
		if (me == null) me = new GalaxySearchController();
		return me;
	}
}
