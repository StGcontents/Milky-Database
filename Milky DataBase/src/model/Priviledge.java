package model;

import pattern.Subject;
/**
 * class to give different kind of privileges to admin/user.
 * @author federico
 *
 */
public class Priviledge extends Subject<Integer> {
	
	private static Priviledge me;
	private Priviledge() { }
	public static synchronized Priviledge instance() {
		if (me == null) me = new Priviledge();
		return me;
	}
	
	private int priviledgeLevel;
	
	private int getPriviledge() { return priviledgeLevel; }
	public void setPriviledge(int priviledgeLevel) {
		this.priviledgeLevel = priviledgeLevel;
		notifyObservers();
	}

	@Override public Integer retrieveState() { return getPriviledge(); }
}
