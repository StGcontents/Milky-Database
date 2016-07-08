package view;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import controller.AddUserController;
import model.Priviledge;

public class AddUserView {
	
	private static AddUserView me;
	public static synchronized AddUserView instance() {
		int currentPriviledgeLevel = Priviledge.instance().retrieveState();
		if (me == null || me.priviledgeLevel != currentPriviledgeLevel) {
			me = new AddUserView();
			me.priviledgeLevel = currentPriviledgeLevel;
		}
		return me;
	}
	private int priviledgeLevel;
	
	private Panel generalPanel;
	private JTextField id;
	private JTextField password;
	private JTextField name;
	private JTextField surname;
	private JTextField mail;
	private JButton AddUserButton;
	private JLabel jlId;
	private JLabel jlPassword;
	private JLabel jlName;
	private JLabel jlSurname;
	private JLabel jlMail;
	private JLabel error;
	

	public Panel generateView() {

		if (generalPanel == null) {

			SpringLayout layout = new SpringLayout();
			generalPanel = new Panel();
			generalPanel.setLayout(layout);

			id = new JTextField(20);
			password = new JTextField(20);
			name = new JTextField(20);
			surname = new JTextField(20);
			mail = new JTextField(20);
			
			AddUserButton = new JButton("Enter");
			jlId = new JLabel("Id: ");
			jlPassword = new JLabel("Password: ");
			jlName = new JLabel("Name: ");
			jlSurname = new JLabel("Surname ");
			jlMail = new JLabel("Mail ");
			error = new JLabel();

			generalPanel.add(id);
			generalPanel.add(password);
			generalPanel.add(name);
			generalPanel.add(surname);
			generalPanel.add(mail);
			generalPanel.add(AddUserButton);
			generalPanel.add(jlId);
			generalPanel.add(jlPassword);
			generalPanel.add(jlName);
			generalPanel.add(jlSurname);
			generalPanel.add(jlMail);
			generalPanel.add(error);

			initializelayoutConstraint(layout);

			AddUserButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (e.getActionCommand() == "Enter") {
						error.setText(null);
						if (id.getText().equals("")
								|| password.getText().equals("")
								|| name.getText().equals("")
								|| surname.getText().equals("")
								|| mail.getText().equals("")) {

							if (id.getText().equals("")) {
								error.setText("Error , Please insert a valid ID");
							}

							else if (password.getText().equals("")) {
								error.setText("Error , Please insert a valid Password");
							} else if (name.getText().equals("")) {
								error.setText("Error, Please insert a valid name");
							} else if (surname.getText().equals("")) {
								error.setText("Error, Please insert a valid surname");
							} else if (mail.getText().equals("")) {
								error.setText("Error, Please insert a valid mail address");
							}

						} else {

							try {
								AddUserController.instance().addUser(
										id.getText(), 
										password.getText(), 
										name.getText(), 
										surname.getText(), 
										mail.getText());
								
								error.setText("Operation successfuly accomplished");
								
								
							} catch (Exception e1) {
								error.setText("Operation failed");
								e1.printStackTrace();
							}	
						}
					} 
				} 
			}); 
			
		}
		return generalPanel;
	}
	
	private void initializelayoutConstraint(SpringLayout layout){
		
		
		layout.putConstraint(SpringLayout.NORTH, jlId, 25,
				SpringLayout.NORTH, generalPanel);
		layout.putConstraint(SpringLayout.WEST, jlId, 25,
				SpringLayout.WEST, generalPanel);
		layout.putConstraint(SpringLayout.NORTH, jlPassword, 10,
				SpringLayout.SOUTH, id);
		layout.putConstraint(SpringLayout.WEST, jlPassword, 25,
				SpringLayout.WEST, generalPanel);
		layout.putConstraint(SpringLayout.NORTH, jlName, 10,
				SpringLayout.SOUTH, password);
		layout.putConstraint(SpringLayout.WEST, jlName, 25,
				SpringLayout.WEST, generalPanel);
		layout.putConstraint(SpringLayout.NORTH, jlSurname, 10,
				SpringLayout.SOUTH, name);
		layout.putConstraint(SpringLayout.WEST, jlSurname, 25,
				SpringLayout.WEST, generalPanel);
		layout.putConstraint(SpringLayout.NORTH, jlMail, 10,
				SpringLayout.SOUTH, surname);
		layout.putConstraint(SpringLayout.WEST, jlMail, 25,
				SpringLayout.WEST, generalPanel);

		layout.putConstraint(SpringLayout.NORTH, id, 0, SpringLayout.SOUTH,
				jlId);
		layout.putConstraint(SpringLayout.WEST, id, 10, SpringLayout.WEST,
				jlId);
		layout.putConstraint(SpringLayout.NORTH, password, 0,
				SpringLayout.SOUTH, jlPassword);
		layout.putConstraint(SpringLayout.WEST, password, 10,
				SpringLayout.WEST, jlPassword);
		layout.putConstraint(SpringLayout.NORTH, name, 0,
				SpringLayout.SOUTH, jlName);
		layout.putConstraint(SpringLayout.WEST, name, 10,
				SpringLayout.WEST, jlName);
		layout.putConstraint(SpringLayout.NORTH, surname, 0,
				SpringLayout.SOUTH, jlSurname);
		layout.putConstraint(SpringLayout.WEST, surname, 10,
				SpringLayout.WEST, jlSurname);
		layout.putConstraint(SpringLayout.NORTH, mail, 0,
				SpringLayout.SOUTH, jlMail);
		layout.putConstraint(SpringLayout.WEST, mail, 10,
				SpringLayout.WEST, jlMail);
		layout.putConstraint(SpringLayout.NORTH, AddUserButton, 0,
				SpringLayout.SOUTH, mail);
		layout.putConstraint(SpringLayout.WEST, AddUserButton, 10,
				SpringLayout.WEST, generalPanel);
		layout.putConstraint(SpringLayout.NORTH, error, 0,
				SpringLayout.SOUTH, AddUserButton);
		layout.putConstraint(SpringLayout.WEST, error, 10,
				SpringLayout.WEST, generalPanel);
	}
	
}
