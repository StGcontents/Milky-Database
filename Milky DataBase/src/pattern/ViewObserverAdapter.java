package pattern;

import view.View;

/**
 * Particular instance of Observer object attached to a View.
 * This class is another step into the development of a pull model
 * MVC architecture.
 * @author stg
 *
 * @param <T>: Type of object managed by Observer pattern.
 * @param <V>: adapted View. 
 */
public abstract class ViewObserverAdapter<T, V extends View> extends Observer<T> {
	protected V adaptee;
	
	public V getAdaptee() { return adaptee; }
	public void setAdaptee(V adaptee) { this.adaptee = adaptee; }
	
	@Override
	public void stateChanged() {
		if (adaptee.isCurrentlyShown())
			doStuff();
	}
	
	protected abstract void doStuff() ;
}
