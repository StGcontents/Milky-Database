package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Serializzatore.SerializzatoreUtente;
import model.User;
import view.LoginView;

public class LoginController {
	
	private static LoginController me;
	private LoginController() { }
	public static synchronized LoginController instance() {
		if (me == null) me = new LoginController();
		return me;
	}
	
	public static void main(String[] args) {
		new LoginView().generateView();
	}
	
	public boolean log(String userID, String password) {
		if (false) {
		UserRepository repo = UserRepository.instance(DataSource.instance(DataSource.READONLY));
		int priviledgeLevel;
		try { priviledgeLevel = repo.logUser(userID, password); }
		catch (Exception e) {
			e.printStackTrace();
			priviledgeLevel = DataSource.INVALID;
		}
		
		return priviledgeLevel != DataSource.INVALID;
		}
		return false;
	}

	public boolean login (String userName, String password)
	{
		HashMap<String, User> utenti = SerializzatoreUtente.getUtenti();
		
		List<User> utentiUserNameList = new ArrayList<User>();
		
		for (String id : utenti.keySet()) {
			utentiUserNameList.add(SerializzatoreUtente.getUtenteByUserName(userName));
		}
		
		for(User u : utentiUserNameList){
			if(u != null){
				if(u.getPassword().equals(password)){
					return true;
				}
			}
		}
		return false;
	}
	
	public User getUtente(String userName){
		return (SerializzatoreUtente.getUtenteByUserName(userName));
	}

}