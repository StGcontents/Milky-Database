package pattern;

import model.Galaxy;
import view.GalaxyInfoView;

public class GalaxyObserverAdapter extends ViewObserverAdapter<Galaxy, GalaxyInfoView>{

	public GalaxyObserverAdapter(GalaxyInfoView adaptee) {
		setAdaptee(adaptee);
	}
	
	@Override
	public void doStuff() {
		adaptee.setGalaxy(subject.retrieveState());
	}
}
