package pattern;

public class ExceptionSubject extends Subject<Exception> {
	
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
