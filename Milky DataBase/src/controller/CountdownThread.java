package controller;

import java.sql.Connection;

class CountdownThread extends Thread {

	private static final long timeout = 5000l;
	private Connection connection;
	private boolean stillUsed;
	
	protected CountdownThread(Connection connection) {
		this.connection = connection;
	}
	
	public void nevermind() { stillUsed = true; }
	
	public void okGo() {
		stillUsed = false;
		if (!this.isAlive()) this.start();
	}
	
	@Override
	public void run() {
		super.run();
		try { sleep(timeout); }
		catch (InterruptedException e) { e.printStackTrace(); }
		
		synchronized(connection) {
			if (!stillUsed) try { connection.close(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}