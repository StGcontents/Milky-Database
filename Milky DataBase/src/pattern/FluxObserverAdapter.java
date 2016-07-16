package pattern;

import view.GalaxyInfoView;

public class FluxObserverAdapter extends ViewObserverAdapter<Void, GalaxyInfoView> {
	
	public FluxObserverAdapter(GalaxyInfoView adaptee) { 
		setAdaptee(adaptee); 
	}
	
	@Override 
	public void doStuff() { 
		adaptee.updateFluxes(); 
	}
}
