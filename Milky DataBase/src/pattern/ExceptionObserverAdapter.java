package pattern;

import view.View;

public class ExceptionObserverAdapter extends ViewObserverAdapter<Exception, View> {
	
	public ExceptionObserverAdapter(View adaptee) {
		setAdaptee(adaptee);
	}

	@Override
	public void doStuff() {
		adaptee.showError(subject.retrieveState());
	}
}
