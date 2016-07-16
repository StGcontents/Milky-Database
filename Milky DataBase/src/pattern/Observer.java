package pattern;

/**
 * Abstract Observer. This pattern is used for View-Model direct communication or
 * Exception-driven GUI update.
 * @author stg
 *
 * @param <T>: Type of object managed by the Observer-Subject couple.
 */
public abstract class Observer<T> {
	protected Subject<T> subject;
	
	public Subject<T> getSubject() { return this.subject; }
	public void setSubject(Subject<T> subject) {
		this.subject = subject;
		this.subject.subscribe(this);
	}
	
	public abstract void stateChanged() ;
}
