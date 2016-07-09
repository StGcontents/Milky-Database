package controller;

import model.FluxRepository;
import model.Galaxy;
import model.Priviledge;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import pattern.FluxObserverAdapter;
import view.GalaxyInfoView;

public class GalaxyInfoController extends ExceptionSubject {
	
	private static GalaxyInfoController me;
	private GalaxyInfoController() { 
		priviledgeLevel = Priviledge.instance().retrieveState();
		repo = new FluxRepository(DataSource.byPriviledge());
		view = GalaxyInfoView.instance();
		new FluxObserverAdapter(view).setSubject(repo.getFillerSubject());
		new ExceptionObserverAdapter(view).setSubject(this);
	}
	public static synchronized GalaxyInfoController instance() {
		if (me == null || me.priviledgeLevel != Priviledge.instance().retrieveState()) 
			me = new GalaxyInfoController();
		return me;
	}
	
	private int priviledgeLevel;
	private GalaxyInfoView view;
	private FluxRepository repo;

	public void retrieveFluxes(Galaxy galaxy) {
		final Galaxy param = galaxy;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyFluxes(param); }
				catch (Exception e) { setState(e); }
			}
		}).start();
	}
	
	public void updateGalaxy(Galaxy galaxy) {
		view.setGalaxy(galaxy);
	}
}
