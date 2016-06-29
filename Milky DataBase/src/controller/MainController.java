package controller;

import view.MainView;

public class MainController {
	
	private static MainController me;
	private MainController(int priviledgeLevel) {
		this.priviledgeLevel = priviledgeLevel;
	}
	public static synchronized MainController instance(int priviledgeLevel) {
		if (me == null || me.priviledgeLevel != priviledgeLevel) 
			me = new MainController(priviledgeLevel);
		return me;
	}
	
	private int priviledgeLevel;
	
	public void callView() {
		new MainView().generateView(priviledgeLevel);
	}
	
	public void exitToLogin() {
		LoginController.instance().callView();
	}

}
