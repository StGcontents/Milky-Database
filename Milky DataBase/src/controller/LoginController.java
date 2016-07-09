package controller;

import model.Priviledge;
import model.UserRepository;
import pattern.LogObserverAdapter;
import view.LoginView;

public class LoginController {
	
	private static LoginController me;
	private LoginController() { 
		repo = new UserRepository(DataSource.readOnly()); 
		new LogObserverAdapter(LoginView.instance()).setSubject(Priviledge.instance());
	}
	public static synchronized LoginController instance() {
		if (me == null) me = new LoginController();
		return me;
	}
	
	private UserRepository repo;
	
	public static void main(String[] args) { instance().callView(); }
	
	public void callView() { LoginView.instance().generateView(); }
	
	public void log(String userID, String password) {
		final String param0 = userID;
		final String param1 = password;
		new Thread(new Runnable() { 
			@Override 
			public void run() { 
				repo.logUser(param0, param1); 
			} 
		}).start();
	}
	
	public void onLoginExit(int priviledgeLevel) {
		MainController.instance().callView();
	}
}