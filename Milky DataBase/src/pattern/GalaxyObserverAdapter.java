package pattern;

import model.Galaxy;
import view.GalaxyInfoView;

/**
 * Observer implementation, requesting a Galaxy object retrieved from DB.
 * @author stg
 *
 */
public class GalaxyObserverAdapter extends ViewObserverAdapter<Galaxy, GalaxyInfoView>{

	public GalaxyObserverAdapter(GalaxyInfoView adaptee) {
		setAdaptee(adaptee);
	}
	
	@Override
	public void doStuff() {
		adaptee.setGalaxy(subject.retrieveState());
	}
}
