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
		view = new AddUserView();
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
	
	public void addUser(String userID, String password, String name, String surname, String mail) throws Exception {
		
		User user = new User(userID, password, name, surname, mail, false);
		
		repo.persistUser(user);

	}
}
