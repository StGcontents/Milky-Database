package controller;

import java.awt.Panel;
import view.AddUserView;
import model.User;
import model.UserRepository;

public class AddUserController {

	private static AddUserController me;
	private UserRepository repo;
	private AddUserView view;
	
	private AddUserController() { 
		repo = new UserRepository(DataSource.byPriviledge());
		view = AddUserView.instance();
		view.getExceptionObserver().setSubject(repo.getExceptionSubject());
	}
	
	public static synchronized AddUserController instance() {
		if (me == null) 
			me = new AddUserController();
		return me;
	}
	
	public Panel callView() {
		view = AddUserView.instance();
		return view.generateView();
	}
	
	public void addUser(String userID, String password, String name, String surname, String mail) {
		
		final String param0 = userID, param1 = password, param2 = name, param3 = surname, param4 = mail;
		new Thread(new Runnable() {
			@Override
			public void run() {
				User user = UserFactory.instance().create(param0, param1, param2, param3, param4);
				try { repo.persistUser(user); }
				catch (Exception e) { repo.getExceptionSubject().setState(e); }
			}
		}).start();
	}
}
