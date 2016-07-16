package exception;

/**
 * Exception subclass used for Exception-driven GUI management.
 * Thrown when an administrator tries to persist twice the same user ID.
 * @author stg
 *
 */
@SuppressWarnings("serial")
public class UserExistsException extends Exception {
	@Override
	public String getMessage() {
		return "User ID already existed";
	}
}
