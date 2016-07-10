package exception;

@SuppressWarnings("serial")
public class ParserException extends Exception {

	@Override
	public String getMessage() {
		return "No Parser could parse file";
	}
}
