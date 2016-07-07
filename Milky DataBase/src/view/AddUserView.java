package view;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;



public class AddUserView {

	private Panel generale;
	private JTextField id;
	private JTextField password;
	private JTextField name;
	private JTextField surname;
	private JTextField mail;
	private JButton bottoneGenerale;
	private JLabel  jlId;
	private JLabel jlPassword;
	private JLabel jlName;
	private JLabel jlSurname;
	private JLabel jlMail;
	private JLabel errore;
	
	
	public Panel generateView() {
		
		if (generale == null) {
			
		SpringLayout layout = new SpringLayout();
		generale = new Panel();
		generale.setLayout(layout);
		
		id = new JTextField(20);
		password = new JTextField(20);
		name = new JTextField(20);
		surname = new JTextField(20);
		mail = new JTextField(20);
		bottoneGenerale=new JButton("Invio");
		jlId = new JLabel("Id: ");
		jlPassword = new JLabel("Password: ");
		jlName = new JLabel("Name: ");
		jlSurname = new JLabel("Surname ");
		jlMail = new JLabel("Mail ");
		errore = new JLabel();
	
		
				
		generale.add(id);
		generale.add(password);
		generale.add(name);
		generale.add(surname);
		generale.add(mail);
		generale.add(bottoneGenerale);
		generale.add(jlId);
		generale.add(jlPassword);
		generale.add(jlName);
		generale.add(jlSurname);
		generale.add(jlMail);
		generale.add(errore);
		
		
		
		layout.putConstraint(SpringLayout.NORTH, jlId, 25, SpringLayout.NORTH, generale);
		layout.putConstraint(SpringLayout.WEST, jlId, 25, SpringLayout.WEST, generale);
		layout.putConstraint(SpringLayout.NORTH, jlPassword, 10, SpringLayout.SOUTH, id);
		layout.putConstraint(SpringLayout.WEST, jlPassword, 25, SpringLayout.WEST, generale);
		layout.putConstraint(SpringLayout.NORTH,jlName , 10, SpringLayout.SOUTH, password);
		layout.putConstraint(SpringLayout.WEST, jlName , 25, SpringLayout.WEST, generale);
		layout.putConstraint(SpringLayout.NORTH, jlSurname, 10, SpringLayout.SOUTH, name);
		layout.putConstraint(SpringLayout.WEST, jlSurname, 25, SpringLayout.WEST, generale);
		layout.putConstraint(SpringLayout.NORTH,jlMail, 10, SpringLayout.SOUTH, surname);
		layout.putConstraint(SpringLayout.WEST,jlMail, 25, SpringLayout.WEST, generale);
	

		
		layout.putConstraint(SpringLayout.NORTH, id, 0, SpringLayout.SOUTH, jlId);
		layout.putConstraint(SpringLayout.WEST, id, 10, SpringLayout.WEST, jlId);
		layout.putConstraint(SpringLayout.NORTH, password, 0, SpringLayout.SOUTH,jlPassword);
		layout.putConstraint(SpringLayout.WEST, password, 10, SpringLayout.WEST, jlPassword);
		layout.putConstraint(SpringLayout.NORTH, name, 0, SpringLayout.SOUTH, jlName);
		layout.putConstraint(SpringLayout.WEST, name, 10, SpringLayout.WEST, jlName);
		layout.putConstraint(SpringLayout.NORTH, surname, 0, SpringLayout.SOUTH,jlSurname);
		layout.putConstraint(SpringLayout.WEST, surname, 10, SpringLayout.WEST, jlSurname);
		layout.putConstraint(SpringLayout.NORTH, mail, 0, SpringLayout.SOUTH, jlMail);
		layout.putConstraint(SpringLayout.WEST, mail, 10, SpringLayout.WEST, jlMail);
		layout.putConstraint(SpringLayout.NORTH, bottoneGenerale, 0, SpringLayout.SOUTH, mail);
		layout.putConstraint(SpringLayout.WEST,bottoneGenerale, 10, SpringLayout.WEST, generale);
		layout.putConstraint(SpringLayout.NORTH, errore, 0, SpringLayout.SOUTH, bottoneGenerale);
		layout.putConstraint(SpringLayout.WEST, errore, 10, SpringLayout.WEST, generale);
			
		
		bottoneGenerale.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if(e.getActionCommand() == "Invio"){
					errore.setText(null);
					if(id.getText().equals("") || password.getText().equals("") || name.getText().equals("") || surname.getText().equals("")|| mail.getText().equals("")){
						
						if(id.getText().equals("")){
							errore.setText("Errore , nessun inserimento dell'id");
						}
						
						else if(password.getText().equals("")){
							errore.setText("Errore , nessun insertimento della password");
							}
						else if(name.getText().equals("")){
							errore.setText("Errore, nessun inserimento del name");
							}
						else if(surname.getText().equals("")){
							errore.setText("Errore, nessun inserimento del surname");
							}
						else if(mail.getText().equals("")){
							errore.setText("Errore, nessun inserimento della mail");
							}
						
					}else{
							System.out.println(id.getText());
							System.out.println(password.getText());
							System.out.println(name.getText());
							System.out.println(surname.getText());
							System.out.println(mail.getText());
							
							errore.setText("Utente salvato nel sistema");
							}
						}	// Dell'invio
					}		// ActionPerformed
				});			// ActionListener
			}
		return generale;
	}		
}

