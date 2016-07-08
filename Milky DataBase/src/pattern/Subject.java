package pattern;

import java.util.ArrayList;
import java.util.List;

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
