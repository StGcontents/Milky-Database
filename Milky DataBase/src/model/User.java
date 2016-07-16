package model;

/**
 * User pojo.
 * @author stg
 *
 */
public class User {
	
	private String id, password, name, surname, mail;
	private boolean isAdmin;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getSurname() { return surname; }
	public void setSurname(String surname) { this.surname = surname; }
	
	public String getMail() { return mail; }
	public void setMail(String mail) { this.mail = mail; }
	
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	
	public User(String id, String password, String name, String surname, String mail, boolean isAdmin) {
		setId(id);
		setPassword(password);
		setName(name);
		setSurname(surname);
		setMail(mail);
		setAdmin(isAdmin);
	}
	
	public User() { }
}
