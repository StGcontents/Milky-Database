package Serializzatore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import model.User;

public class SerializzatoreUtente {

	static final private String FILENAME = "/tmp/utenti.ser";

	private static Lock fileUtentiLock = new ReentrantLock();

	
	@SuppressWarnings("unchecked")
	public static HashMap<String, User> getUtenti() {
		fileUtentiLock.lock();

		File file = new File(FILENAME);
		ObjectInputStream is;

		try {
			is = new ObjectInputStream(new FileInputStream(file));
			Object obj = is.readObject();
			is.close();
			return (HashMap<String, User>) obj;
		} catch (FileNotFoundException e) {
			/* nessun utente ancora nel sistema */
			User u1 = new User("admin", "admin", "admin", "admin",
					"admin@email.com");
			User u2 = new User("admin", "admin", "admin", "admin",
					"admin@email.com");
			HashMap<String, User> utenti = new HashMap<String, User>();
			utenti.put(u1.getId(), u1);
			utenti.put(u2.getId(), u2);
			salvaUtenti(utenti);
			return utenti;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			fileUtentiLock.unlock();
		}

		return null; /* in caso di errori */
	}
	
	
	private static void salvaUtenti(HashMap<String, User> utenti) {
		fileUtentiLock.lock();

		File file = new File(FILENAME);
		try {
			ObjectOutputStream os = new ObjectOutputStream(
					new FileOutputStream(file));
			os.writeObject(utenti);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileUtentiLock.unlock();
	}
	
	public static User getUtenteByUserName(String userName) {
		return getUtenti().get(userName);
	}
	
}
