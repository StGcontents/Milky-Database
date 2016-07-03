package controller;

import java.awt.Panel;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Galaxy.Coordinates;
import model.AdaptableValue;
import model.GalaxyRepository;
import model.Priviledge;
import view.GalaxyView;

public class GalaxySearchController implements ListSelectionListener {
	
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
	
	public Panel callView() {
		view = GalaxyView.instance();
		view.getListObserver().setSubject(repo.getNameSubject());
		view.getGalaxyObserver().setSubject(repo.getGalaxySubject());
		return view.generateSearchPanel();
	}
	
	public void searchNames(String partial) {
		if (partial == null || "".equals(partial)) return;
		
		try { repo.retrieveGalaxyNames(partial); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void searchByRedshiftValue(double redshift, boolean higherThen, int limit) {
		try { repo.retrieveGalaxyByRedshiftValue(redshift, higherThen, limit); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void searchInRange(Coordinates center, double range, int limit) {
		try { repo.retrieveGalaxyInRange(center, range, limit); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		JList<AdaptableValue> list = (JList<AdaptableValue>) e.getSource();
		ListSelectionModel listModel = list.getSelectionModel();
		if (!listModel.isSelectionEmpty()) {
			int min = listModel.getMinSelectionIndex();
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
