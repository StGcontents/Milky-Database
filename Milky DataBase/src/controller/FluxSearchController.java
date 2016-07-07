package controller;

import model.FluxRepository;
import model.Galaxy;
import model.Priviledge;
import view.GalaxyView;

public class FluxSearchController {
	
	private static FluxSearchController me;
	private FluxSearchController() { 
		priviledgeLevel = Priviledge.instance().retrieveState();
		repo = new FluxRepository(DataSource.byPriviledge());
		view = GalaxyView.instance();
		view.getFluxObserver().setSubject(repo.getFillerSubject());
	}
	public static synchronized FluxSearchController instance() {
		if (me == null || me.priviledgeLevel != Priviledge.instance().retrieveState()) 
			me = new FluxSearchController();
		return me;
	}
	
	private int priviledgeLevel;
	private GalaxyView view;
	private FluxRepository repo;

	public void retrieveFluxes(Galaxy galaxy) {
		final Galaxy param = galaxy;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyFluxes(param); }
				catch (Exception e) { e.printStackTrace(); }
			}
		}).start();
	}
}
