package exception;

/**
 * Exception subclass used for Exception driven GUI management.
 * Represents inability of parsing a .csv file with implemented Parsers. 
 * @author stg
 *
 */
@SuppressWarnings("serial")
public class ParserException extends Exception {

	@Override
	public String getMessage() {
		return "No Parser could parse file";
	}
}
