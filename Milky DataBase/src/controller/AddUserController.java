package controller;

import java.awt.Panel;

import view.AddUserView;
import model.UserRepository;

public class AddUserController {

	private static AddUserController me;
	private AddUserController() { 
		repo = new UserRepository(DataSource.byPriviledge());
		view = new AddUserView();
	}
	public static synchronized AddUserController instance() {
		if (me == null) 
			me = new AddUserController();
		return me;
	}
	
	private UserRepository repo;
	private AddUserView view;
	
	public Panel generateView(){
		
		
		return view.generateView();
	}
}
