package pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Subject. Used in conjunction with coupled Observer for
 * View-Model direct communication or Exception-driven GUI update. 
 * @author stg
 *
 * @param <T>: Type of object stored in a Subject instance.
 */
@SuppressWarnings("rawtypes")
public abstract class Subject<T> {
	private List<Observer> observers = new ArrayList<>();
	
	public void subscribe(Observer observer) { observers.add(observer); }
	public void unsubscribe(Observer observer) { observers.remove(observer); }
	
	public void notifyObservers() {
		for (Observer observer : observers)
			observer.stateChanged();
	}
	
	public abstract T retrieveState() ;
}
