package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Serializzatore.SerializzatoreUtente;
import model.User;

public class LoginController
{

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