package pattern;

import view.GalaxyInfoView;

/**
 * Observer implementation waiting for Galaxy objects filling with Flux pojos.
 * It does not request any object, only acknowledges that a filling operation
 * was completed.
 * @author stg
 *
 */
public class FluxObserverAdapter extends ViewObserverAdapter<Void, GalaxyInfoView> {
	
	public FluxObserverAdapter(GalaxyInfoView adaptee) { 
		setAdaptee(adaptee); 
	}
	
	@Override 
	public void doStuff() { 
		adaptee.updateFluxes(); 
	}
}
