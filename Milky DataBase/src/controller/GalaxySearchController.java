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
import model.Priviledge;
import view.GalaxyInfoView;
import view.GalaxyView;

public class GalaxySearchController extends ExceptionSubject implements ListSelectionListener {
	
	private static GalaxySearchController me;
	private GalaxySearchController() { 
		priviledgeLevel = Priviledge.instance().retrieveState();
		repo = new GalaxyRepository(DataSource.byPriviledge());
	}
	public static synchronized GalaxySearchController instance() {
		if (me == null || me.priviledgeLevel != Priviledge.instance().retrieveState()) 
			me = new GalaxySearchController();
		return me;
	}
	
	private int priviledgeLevel;
	private GalaxyView view;
	private GalaxyRepository repo;
	
	public JPanel callView() {
		view = GalaxyView.instance();
		new GalaxyListObserverAdapter(view).setSubject(repo.getNameSubject());
		new GalaxyObserverAdapter(GalaxyInfoView.instance()).setSubject(repo.getGalaxySubject());
		new ExceptionObserverAdapter(view).setSubject(this);
		return view.generateView();
	}
	
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
			
			try { repo.retrieveGalaxyByName(model.get(i).getName(), false); } 
			catch (Exception e1) { e1.printStackTrace(); }
		}
	}
}
