package pattern;

import view.View;

public abstract class ViewObserverAdapter<T, V extends View> extends Observer<T> {
	protected V adaptee;
	
	public V getAdaptee() { return adaptee; }
	public void setAdaptee(V adaptee) { this.adaptee = adaptee; }
}
