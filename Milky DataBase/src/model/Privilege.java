package model;

import pattern.Subject;
/**
 * Singleton class representing the privilege level granted to the logged user.
 * Used for DataSource initialization. It implements Subject behaviors. 
 * @author federico
 *
 */
public class Privilege extends Subject<Integer> {
	
	private static Privilege me;
	private Privilege() { }
	public static synchronized Privilege instance() {
		if (me == null) me = new Privilege();
		return me;
	}
	
	private int privilegeLevel;
	
	private int getPriviledge() { return privilegeLevel; }
	public void setPriviledge(int privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
		notifyObservers();
	}

	@Override public Integer retrieveState() { return getPriviledge(); }
}
