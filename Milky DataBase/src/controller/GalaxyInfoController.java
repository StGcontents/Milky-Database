package controller;

import model.FluxRepository;
import model.Galaxy;
import model.Privilege;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import pattern.FluxObserverAdapter;
import view.GalaxyInfoView;

/**
 * Controller class managing galaxies information retrieval.
 * @author stg
 *
 */
public class GalaxyInfoController extends ExceptionSubject {
	
	/*
	 * Singleton
	 */
	private static GalaxyInfoController me;
	private GalaxyInfoController() { 
		privilegeLevel = Privilege.instance().retrieveState();
		repo = new FluxRepository(DataSource.byPriviledge());
		view = GalaxyInfoView.instance();
		new FluxObserverAdapter(view).setSubject(repo.getFillerSubject());
		new ExceptionObserverAdapter(view).setSubject(this);
	}
	public static synchronized GalaxyInfoController instance() {
		if (me == null || me.privilegeLevel != Privilege.instance().retrieveState()) 
			me = new GalaxyInfoController();
		return me;
	}
	
	private int privilegeLevel;
	private GalaxyInfoView view;
	private FluxRepository repo;

	/**
	 * Updates a Galaxy objects with its Fluxes.
	 * @param galaxy Galaxy: a Galaxy pojo to be filled with Flux pojos.
	 */
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
	
	/**
	 * GUI update
	 * @param galaxy Galaxy: a Galaxy pojos whose state is to be shown.
	 */
	public void updateGalaxy(Galaxy galaxy) {
		view.setGalaxy(galaxy);
	}
}
