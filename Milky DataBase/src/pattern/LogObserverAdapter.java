package pattern;

import view.LoginView;

public class LogObserverAdapter extends ViewObserverAdapter<Integer, LoginView> {
	
	public LogObserverAdapter(LoginView adaptee) {
		setAdaptee(adaptee);
	}

	@Override
	public void stateChanged() {
		adaptee.logIn(subject.retrieveState().intValue());
		
	}
	
	

}
