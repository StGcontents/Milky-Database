package pattern;

import java.util.List;

import model.AdaptableValue;
import view.GalaxyView;

/**
 * Observer implementation. Collects AdaptableValue objects for JList population.
 * @author stg
 *
 */
@SuppressWarnings("rawtypes")
public class GalaxyListObserverAdapter extends ViewObserverAdapter<List<AdaptableValue>, GalaxyView> {

	public GalaxyListObserverAdapter(GalaxyView adaptee) { 
		setAdaptee(adaptee); 
	} 
	
	@Override 
	public void doStuff() { 
		synchronized (adaptee) {
			adaptee.populate(getSubject().retrieveState());
		}
	}
}
