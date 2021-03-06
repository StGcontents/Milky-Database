package controller;

import java.sql.Connection;

/**
 * Runnable object whose purpose is to delay a Connection closure. It allows
 * for a Connection to be re-used multiple times before is actually closed. 
 * @author stg
 *
 */
class CountdownRunnable implements Runnable {

	private static final long timeout = 5000l;
	private Connection connection;
	private int stillUsed = 0;
	
	protected CountdownRunnable(Connection connection) {
		this.connection = connection;
	}
	
	public void nevermind() { 
		synchronized (connection) {
			stillUsed += 1; 
		}
	}
	
	public void okGo() { 
		synchronized (connection) {
			if (stillUsed > 0) 
				stillUsed -= 1;
		}
	}
	
	@Override
	public void run() {
		
		okGo();

		try { Thread.sleep(timeout); }
		catch (InterruptedException e) { e.printStackTrace(); }
		
		synchronized(connection) {
			if (stillUsed == 0) try { 
				connection.close(); 
				System.out.println("CLOSED");
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}