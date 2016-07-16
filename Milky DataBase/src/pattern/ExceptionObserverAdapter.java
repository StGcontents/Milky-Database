package pattern;

import view.View;

/**
 * Observer implementation for Exception-driven GUI update.
 * It wraps up a View object in order to call its showError method.
 * Also kinda implements Adapter pattern.
 * @author stg
 *
 */
public class ExceptionObserverAdapter extends ViewObserverAdapter<Exception, View> {
	
	public ExceptionObserverAdapter(View adaptee) {
		setAdaptee(adaptee);
	}

	@Override
	public void doStuff() {
		adaptee.showError(subject.retrieveState());
	}
}
