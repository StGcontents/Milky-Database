package controller;

import java.awt.Panel;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.GalaxyRepository;
import view.GalaxyPanel;

public class GalaxySearchController implements ListSelectionListener {
	
	private static GalaxySearchController me;
	private GalaxySearchController() { }
	public static synchronized GalaxySearchController instance() {
		if (me == null) me = new GalaxySearchController();
		return me;
	}
	
	private GalaxyPanel panel;
	private GalaxyRepository repo;
	
	public Panel callView() {
		panel = new GalaxyPanel();
		repo = new GalaxyRepository(DataSource.byPriviledge());
		panel.getListObserver().setSubject(repo.getNameSubject());
		panel.getGalaxyObserver().setSubject(repo.getGalaxySubject());
		return panel;
	}
	
	public void searchNames(String partial) {
		if (partial == null || "".equals(partial)) return;
		
		try { repo.retrieveGalaxyNames(partial); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		JList<String[]> list = (JList<String[]>) e.getSource();
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
			
			DefaultListModel<String[]> model = (DefaultListModel<String[]>) list.getModel();
			
			try { repo.retrieveGalaxyByName(model.get(i)[0], true); } 
			catch (Exception e1) { e1.printStackTrace(); }
		}
	}
}
