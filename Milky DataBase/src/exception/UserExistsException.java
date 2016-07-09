package exception;

@SuppressWarnings("serial")
public class UserExistsException extends Exception {
	@Override
	public String getMessage() {
		return "User ID already existed";
	}
}
