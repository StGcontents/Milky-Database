package controller;

import view.LoginView;

public class LoginController {
	
	private static LoginController me;
	private LoginController() { }
	public static synchronized LoginController instance() {
		if (me == null) me = new LoginController();
		return me;
	}
	
	public static void main(String[] args) {
		instance().callView();
	}
	
	public void callView() {
		new LoginView().generateView();
	}
	
	public int log(String userID, String password) {
		UserRepository repo = UserRepository.instance(DataSource.instance(DataSource.READONLY));
		int priviledgeLevel;
		try { priviledgeLevel = repo.logUser(userID, password); }
		catch (Exception e) {
			e.printStackTrace();
			priviledgeLevel = DataSource.INVALID;
		}
		
		return priviledgeLevel;
	}
	
	public void onLoginExit(int priviledgeLevel) {
		MainController.instance(priviledgeLevel).callView();
	}
}