package controller;

public class DemoController {
	
	private static DemoController me;
	private DemoController() { }
	
	public synchronized DemoController getInstance() {
		if (me == null) me = new DemoController();
		return me;
	}
	
	public synchronized void doSomething() {
	}
}
