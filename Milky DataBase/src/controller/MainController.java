package controller;

import model.Priviledge;
import view.MainView;

public class MainController {
	
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

}
