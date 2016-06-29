package controller;

import model.Priviledge;
import model.UserRepository;
import view.LoginView;

public class LoginController {
	
	private static LoginController me;
	private LoginController() { 
		repo = new UserRepository(DataSource.instance(DataSource.READONLY)); 
		LoginView.instance().setSubject(Priviledge.instance());
	}
	public static synchronized LoginController instance() {
		if (me == null) me = new LoginController();
		return me;
	}
	
	private UserRepository repo;
	
	public static void main(String[] args) { instance().callView(); }
	
	public void callView() { LoginView.instance().generateView(); }
	
	public void log(String userID, String password) {
		repo.logUser(userID, password);
	}
	
	public void onLoginExit(int priviledgeLevel) {
		MainController.instance().callView();
	}
}