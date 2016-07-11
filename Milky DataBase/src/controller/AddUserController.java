package controller;

import java.awt.Panel;
import view.AddUserView;
import model.User;
import model.UserRepository;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;

/**
 * Following class allows to register the data of another user of the application. Receives
 * informations from the correspondent view (AddUserView)
 * @author federico
 *
 */
public class AddUserController extends ExceptionSubject {

	private static AddUserController me;
	private UserRepository repo;
	private AddUserView view;
	
	private AddUserController() { 
		repo = new UserRepository(DataSource.byPriviledge());
		view = AddUserView.instance();
		new ExceptionObserverAdapter(view).setSubject(this);
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
	/**
	 * Main method of the class. Receives as input the parameters of the user to be persisted.
	 * @param userID
	 * @param password
	 * @param name
	 * @param surname
	 * @param mail
	 */
	
	public void addUser(String userID, String password, String name, String surname, String mail) {
		
		final String param0 = userID, param1 = password, param2 = name, param3 = surname, param4 = mail;
		new Thread(new Runnable() {
			@Override
			public void run() {
				User user = UserFactory.instance().create(param0, param1, param2, param3, param4);
				try { 
					repo.persist(user);
					setState(null);
				}
				catch (Exception e) { setState(e); }
			}
		}).start();
	}
}
