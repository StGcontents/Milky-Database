package exception;

import java.sql.SQLException;

@SuppressWarnings("serial")
public class TolerableSQLException extends SQLException {
	private int counter;
	private String logPath;
	
	public TolerableSQLException() {
		counter = 1;
	}
	
	public TolerableSQLException(String logPath) {
		this();
		this.logPath = logPath;
	}
	
	public synchronized void increment() {
		++counter;
	}
	
	@Override
	public synchronized String getMessage() {
		return "<html>Parser encountered " + counter + " SQL errors while running;<br>check log file for more information (current path: " + logPath + ").</html>";
	}
}
