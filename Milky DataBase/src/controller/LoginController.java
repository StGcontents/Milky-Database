package controller;

import model.Privilege;
import model.UserRepository;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import pattern.LogObserverAdapter;
import view.LoginView;

/**
 * Following class checks if parameters for login are correct. Related to LoginView
 * @author federico
 *
 */
public class LoginController extends ExceptionSubject {
	
	/*
	 * Singleton
	 */
	private static LoginController me;
	private LoginController() {
		repo = new UserRepository(DataSource.readOnly()); 
		new LogObserverAdapter(LoginView.instance()).setSubject(Privilege.instance());
		new ExceptionObserverAdapter(LoginView.instance()).setSubject(this);
	}
	public static synchronized LoginController instance() {
		if (me == null) me = new LoginController();
		return me;
	}
	
	private UserRepository repo;
	
	/*
	 * Runs the entire application.
	 */
	public static void main(String[] args) { instance().callView(); }
	
	public void callView() { LoginView.instance().generateView(); }
	
	/**
	 * Try logging into the application submitting an user ID and a password.
	 * @param userID String: user ID
	 * @param password String: user password
	 */
	public void log(String userID, String password) {
		final String param0 = userID;
		final String param1 = password;
		new Thread(new Runnable() { 
			@Override 
			public void run() { 
				try { repo.logUser(param0, param1); }
				catch (Exception e) { setState(e); }
			} 
		}).start();
	}
	
	public void onLoginExit(int privilegeLevel) {
		MainController.instance().callView();
	}
}