package view;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import controller.AddUserController;
import exception.UserExistsException;
/**
 *This view allows the administrator to register another user of the system, sending data to its 
 *controller. 
 * @author federico
 *
 */
public class AddUserView extends View {

	private static AddUserView me;
	private AddUserView() { }
	public static synchronized AddUserView instance() {
		if (me == null) 
			me = new AddUserView();
		return me;
	}

	private Panel generalPanel;
	private JTextField idField, passwordField, nameField, surnameField, mailField;
	private JButton AddUserButton;
	private JLabel errorLabel;

	@Override
	public Panel generateView() {
		if (generalPanel == null) {

			SpringLayout layout = new SpringLayout();
			generalPanel = new Panel();
			generalPanel.setLayout(layout);

			idField = new JTextField(20);
			passwordField = new JTextField(20);
			nameField = new JTextField(20);
			surnameField = new JTextField(20);
			mailField = new JTextField(20);

			AddUserButton = new JButton("Enter");
			JLabel jlId = new JLabel("Id: ");
			JLabel jlPassword = new JLabel("Password: ");
			JLabel jlName = new JLabel("Name: ");
			JLabel jlSurname = new JLabel("Surname ");
			JLabel jlMail = new JLabel("Mail ");
			errorLabel = new JLabel();

			generalPanel.add(idField);
			generalPanel.add(passwordField);
			generalPanel.add(nameField);
			generalPanel.add(surnameField);
			generalPanel.add(mailField);
			generalPanel.add(AddUserButton);
			generalPanel.add(jlId);
			generalPanel.add(jlPassword);
			generalPanel.add(jlName);
			generalPanel.add(jlSurname);
			generalPanel.add(jlMail);
			generalPanel.add(errorLabel);

			AddUserButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (e.getActionCommand() == "Enter") {
						errorLabel.setText(null);

						if (idField.getText().equals(""))
							errorLabel.setText("Error , Please insert a valid ID");

						else if (passwordField.getText().equals(""))
							errorLabel.setText("Error , Please insert a valid Password");

						else if (nameField.getText().equals(""))
							errorLabel.setText("Error, Please insert a valid name");

						else if (surnameField.getText().equals(""))
							errorLabel.setText("Error, Please insert a valid surname");

						else
							AddUserController.instance().addUser(idField.getText(), passwordField.getText(),
									nameField.getText(), surnameField.getText(), mailField.getText());
					}
				}
			});
			
			layout.putConstraint(SpringLayout.NORTH, jlId, 25, SpringLayout.NORTH, generalPanel);
			layout.putConstraint(SpringLayout.WEST, jlId, 25, SpringLayout.WEST, generalPanel);
			layout.putConstraint(SpringLayout.NORTH, jlPassword, 10, SpringLayout.SOUTH, idField);
			layout.putConstraint(SpringLayout.WEST, jlPassword, 25, SpringLayout.WEST, generalPanel);
			layout.putConstraint(SpringLayout.NORTH, jlName, 10, SpringLayout.SOUTH, passwordField);
			layout.putConstraint(SpringLayout.WEST, jlName, 25, SpringLayout.WEST, generalPanel);
			layout.putConstraint(SpringLayout.NORTH, jlSurname, 10, SpringLayout.SOUTH, nameField);
			layout.putConstraint(SpringLayout.WEST, jlSurname, 25, SpringLayout.WEST, generalPanel);
			layout.putConstraint(SpringLayout.NORTH, jlMail, 10, SpringLayout.SOUTH, surnameField);
			layout.putConstraint(SpringLayout.WEST, jlMail, 25, SpringLayout.WEST, generalPanel);

			layout.putConstraint(SpringLayout.NORTH, idField, 0, SpringLayout.SOUTH, jlId);
			layout.putConstraint(SpringLayout.WEST, idField, 10, SpringLayout.WEST, jlId);
			layout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.SOUTH, jlPassword);
			layout.putConstraint(SpringLayout.WEST, passwordField, 10, SpringLayout.WEST, jlPassword);
			layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.SOUTH, jlName);
			layout.putConstraint(SpringLayout.WEST, nameField, 10, SpringLayout.WEST, jlName);
			layout.putConstraint(SpringLayout.NORTH, surnameField, 0, SpringLayout.SOUTH, jlSurname);
			layout.putConstraint(SpringLayout.WEST, surnameField, 10, SpringLayout.WEST, jlSurname);
			layout.putConstraint(SpringLayout.NORTH, mailField, 0, SpringLayout.SOUTH, jlMail);
			layout.putConstraint(SpringLayout.WEST, mailField, 10, SpringLayout.WEST, jlMail);
			layout.putConstraint(SpringLayout.NORTH, AddUserButton, 10, SpringLayout.SOUTH, mailField);
			layout.putConstraint(SpringLayout.WEST, AddUserButton, 10, SpringLayout.WEST, generalPanel);
			layout.putConstraint(SpringLayout.NORTH, errorLabel, 10, SpringLayout.SOUTH, AddUserButton);
			layout.putConstraint(SpringLayout.WEST, errorLabel, 10, SpringLayout.WEST, generalPanel);
		}
		else reset();
		return generalPanel;

	}

	@Override
	protected void reset() {
		errorLabel.setText(null);
		idField.setText(null);
		passwordField.setText(null);
		nameField.setText(null);
		surnameField.setText(null);
		mailField.setText(null);
	}

	@Override
	public void showError(Exception e) {
		if (e == null) {
			reset();
			errorLabel.setText("User registered");
		} 
		else if (e instanceof UserExistsException) {
			errorLabel.setText(e.getMessage());
			idField.requestFocus();
		} 
		else if (e instanceof SQLException)
			errorLabel.setText("An SQL error occurred");
		else
			errorLabel.setText("An error occurred");
	}
}
