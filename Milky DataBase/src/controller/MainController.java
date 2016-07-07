package controller;

import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Priviledge;
import view.MainView;

public class MainController implements ListSelectionListener {
	
	public static void main (String args []){
		
		instance().callView();
		
		
	}
	
	private static MainController me;
	private MainController(int priviledgeLevel) {
		this.priviledgeLevel = priviledgeLevel;
	}
	public static synchronized MainController instance() {
		int level = Priviledge.instance().retrieveState();
		if (me == null || me.priviledgeLevel != level) 
			me = new MainController(level);
		return me;
	}
	
	private int priviledgeLevel;
	private MainView view;
	
	public void callView() {
		view = new MainView(priviledgeLevel);
		view.generateView();
	}
	
	public void exitToLogin() {
		LoginController.instance().callView();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		ListSelectionModel model = ((JList<String>) e.getSource()).getSelectionModel();
		if (!model.isSelectionEmpty()) {
			int min = model.getMinSelectionIndex();
			int max = model.getMaxSelectionIndex();
			int i;
			for (i = min; i <= max; ++i) {
				if (model.isSelectedIndex(i)) break;
				else if (i == max) {
					i = -1;
					break;
				}
			}
			
			act(i);
		}
	}
	
	private void act(int index) {
		if (priviledgeLevel == DataSource.COMMON && index > MainView.LAST_COMMON_INDEX) return;
		
		switch(index) {
		case 0:
			view.attachPanel(GalaxySearchController.instance().callView());
			break;
		case 1:
			view.attachPanel(HeavyTaskController.instance().callView());
			break;
		case 2:
			if (priviledgeLevel == DataSource.COMMON) {
				view.close();
				exitToLogin();
			}
			else {
				Panel panel = new Panel(new FlowLayout());
				Label label = new Label("You selected row #" + (index + 1));
				panel.add(label);
				view.attachPanel(panel);
			}
			break;
		case 3:
			
			view.attachPanel(AddUserController.instance().generateView());
			
			
			break;
		case 4:
			view.close();
			exitToLogin();
			break;
		default:
			Panel panel = new Panel(new FlowLayout());
			Label label = new Label("You selected row #" + (index + 1));
			panel.add(label);
			view.attachPanel(panel);
		}
	}
}
