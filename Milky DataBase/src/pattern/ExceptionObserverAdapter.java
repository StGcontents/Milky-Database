package pattern;

import view.View;

public class ExceptionObserverAdapter extends Observer<Exception> {
	
	private View adaptee;

	public View getAdaptee() { return adaptee; }
	public void setAdaptee(View adaptee) { this.adaptee = adaptee; }

	@Override
	public void stateChanged() {
		adaptee.showError(subject.retrieveState());
	}
}
