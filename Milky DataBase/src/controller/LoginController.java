package controller;

import java.util.HashMap;

import model.User;
import modelBean.UserBean;

public class LoginController {
	
	public boolean login (UserBean u)
	{
		HashMap<String, User> utenti = SerializzatoreUtente.getUtenti();
		
		User utenteCercato = utenti.get(u.getUserId());
		if (utenteCercato == null)
			return false;

		if (utenteCercato.getPassword().equals(u.getPassword()))
			return true;
		
		return false;
	}
}
