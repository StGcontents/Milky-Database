package pattern;

/**
 * Subject implementation for Exception-driven GUI update.
 * All Controllers extend this class, them being the only ones
 * that manages Exceptions.
 * @author stg
 *
 */
public class ExceptionSubject extends ViewSubject<Exception> {
	
	private Exception state;
	
	public void setState(Exception e) {
		state = e;
		notifyObservers();
	}

	@Override
	public Exception retrieveState() {
		return state;
	}
}
