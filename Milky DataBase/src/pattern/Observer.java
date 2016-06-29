package pattern;

public abstract class Observer<T> {
	protected Subject<T> subject;
	
	public Subject<T> getSubject() { return this.subject; }
	public void setSubject(Subject<T> subject) {
		this.subject = subject;
		this.subject.subscribe(this);
	}
	
	public abstract void stateChanged() ;
}
