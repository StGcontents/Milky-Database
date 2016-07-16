package controller;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Galaxy.Coordinates;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import pattern.GalaxyListObserverAdapter;
import pattern.GalaxyObserverAdapter;
import model.AdaptableValue;
import model.GalaxyRepository;
import model.Privilege;
import view.GalaxyInfoView;
import view.GalaxyView;

/**
 * Controller class for Galaxy research and selection.
 * @author stg
 *
 */
public class GalaxySearchController extends ExceptionSubject implements ListSelectionListener {
	
	/*
	 * Singleton
	 */
	private static GalaxySearchController me;
	private GalaxySearchController() { 
		priviledgeLevel = Privilege.instance().retrieveState();
		repo = new GalaxyRepository(DataSource.byPriviledge());
	}
	public static synchronized GalaxySearchController instance() {
		if (me == null || me.priviledgeLevel != Privilege.instance().retrieveState()) 
			me = new GalaxySearchController();
		return me;
	}
	
	private int priviledgeLevel;
	private GalaxyView view;
	private GalaxyRepository repo;
	
	/*
	 * Test only
	 */
	public GalaxyRepository getRepo() { return repo; }
	
	public JPanel callView() {
		view = GalaxyView.instance();
		new GalaxyListObserverAdapter(view).setSubject(repo.getNameSubject());
		new GalaxyObserverAdapter(GalaxyInfoView.instance()).setSubject(repo.getGalaxySubject());
		new ExceptionObserverAdapter(view).setSubject(this);
		return view.generateView();
	}
	
	/**
	 * Implementation of research by input key.
	 * @param partial String: a key specified by the user for name based galaxy
	 *  search.
	 */
	public void searchNames(String partial) {		
		final String param0 = partial;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyNames(param0); }
				catch (Exception e) { setState(e); }
			}
		}).start();
	}
	
	/**
	 * Redshift value based galaxy research.
	 * @param redshift double: redshift value for confrontation.
	 * @param higherThen boolean: whether or not results have to be higher than or lower than
	 * 	the redshift value.
	 * @param limit int: numerical limit to fetch size.
	 */
	public void searchByRedshiftValue(double redshift, boolean higherThen, int limit) {
		final double param0 = redshift;
		final boolean param1 = higherThen;
		final int param2 = limit;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyByRedshiftValue(param0, param1, param2); }
				catch (Exception e) { setState(e); }
			}
		}).start();
	}
	
	/**
	 * Distance from a point based galaxy research.
	 * @param center Coordinates: coordinates specified by the user
	 * @param range double: distance range accepted
	 * @param limit int: numerical limit to fetch size.
	 */
	public void searchInRange(Coordinates center, double range, int limit) {
		final Coordinates param0 = center;
		final double param1 = range;
		final int param2 = limit;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyInRange(param0, param1, param2); }
				catch (Exception e) { setState(e); }
			}
		}).start();
	}
	
	/**
	 * Fetch a Galaxy object by name.
	 * @param name String: a Galaxy name.
	 */
	public void retrieveGalaxyByName(String name) {
		final String arg0 = name;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.retrieveGalaxyByName(arg0, false); } 
				catch (Exception e) { e.printStackTrace(); }
			}
		}).start();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void valueChanged(ListSelectionEvent e) {
	
		if (e.getValueIsAdjusting()) return;
		
		@SuppressWarnings("unchecked")	
		JList<AdaptableValue> list = (JList<AdaptableValue>) e.getSource();
		ListSelectionModel listModel = list.getSelectionModel();
		if (!listModel.isSelectionEmpty()) {
			int min = listModel.getMinSelectionIndex();
			if (min < 0) return;
			int max = listModel.getMaxSelectionIndex();
			int i;
			for (i = min; i <= max; ++i) {
				if (listModel.isSelectedIndex(i)) break;
				else if (i == max) {
					i = -1;
					break;
				}
			}
			
			if (i == -1) return;
			
			DefaultListModel<AdaptableValue> model = (DefaultListModel<AdaptableValue>) list.getModel();
			retrieveGalaxyByName(model.get(i).getName());
		}
	}
}
