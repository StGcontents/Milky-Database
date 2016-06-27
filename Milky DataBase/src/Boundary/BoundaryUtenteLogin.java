package Boundary;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.LoginController;
import model.User;

public class BoundaryUtenteLogin extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton btHome;
	private JButton btLogin;
	private JLabel jlUserName, jlPassword;
	private JTextField tfUserName, tfPassword;
	private JPanel pannelloUserName, pannelloPassword, pannelloGenerale, pannelloLogin, pannelloBottoni;
	
	private LoginController controllore;
	//private BoundaryUtenteMenu bum;
	
	
	private CardLayout cardLayout;

	public BoundaryUtenteLogin(){
		
		this.setTitle("Basi e Galassie");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Toolkit mioToolkit = Toolkit.getDefaultToolkit();
		Dimension dimensioniSchermo = mioToolkit.getScreenSize();

		int larghezzaFrame, altezzaFrame;
		larghezzaFrame = (int) (dimensioniSchermo.getWidth()/1.5);
		altezzaFrame = (int) (dimensioniSchermo.getHeight()/1.5);

		this.setSize(larghezzaFrame, altezzaFrame);

		// NOTA IMPORTANTE: l'origine del sistema di riferimento dello schermo (punto (0,0)) è situata IN ALTO A SINISTRA; valori y positivi 'scendendo', x positivi proseguendo 'verso destra'.
		this.setLocation(((int)dimensioniSchermo.getWidth()/4), ((int)dimensioniSchermo.getHeight()/4));

		this.setLayout(new BorderLayout());
		
		btLogin = new JButton("Login");
		btHome = new JButton("Home"); // Lo usiamo per tornare indietro
	
		
		jlUserName = new JLabel("Username: ");
		jlPassword = new JLabel("Password: ");
		
		tfUserName = new JTextField(20);
		tfPassword = new JTextField(20);
		
		pannelloPassword = new JPanel();
		pannelloUserName = new JPanel();
		pannelloBottoni = new JPanel();
		pannelloLogin = new JPanel(new BorderLayout());
		pannelloGenerale = new JPanel(new CardLayout());
		
		pannelloBottoni.add(btHome);
		
		pannelloPassword.add(jlPassword);
		pannelloPassword.add(tfPassword);
		pannelloUserName.add(jlUserName);
		pannelloUserName.add(tfUserName);
		
		btLogin.addActionListener(this);
		
		btHome.addActionListener(this);
		
		btHome.setEnabled(false);
		
		
		pannelloUserName.setBackground(Color.white);
		pannelloPassword.setBackground(Color.white);
		
		pannelloLogin.add(pannelloUserName, BorderLayout.NORTH);
		pannelloLogin.add(pannelloPassword, BorderLayout.CENTER);
		pannelloLogin.add(btLogin, BorderLayout.SOUTH);
		
		pannelloLogin.setBackground(Color.white);
		
		pannelloGenerale.add(pannelloLogin, "Login");
		
		pannelloGenerale.setBackground(Color.white);
		
		this.add(pannelloLogin, BorderLayout.NORTH);
		this.getContentPane().add(pannelloBottoni, BorderLayout.NORTH);
		this.getContentPane().add(pannelloGenerale, BorderLayout.CENTER);
		
		}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		cardLayout = (CardLayout) pannelloGenerale.getLayout();
		
		if(e.getActionCommand()=="Login"){
			controllore = new LoginController();
			if(controllore.login(tfUserName.getText(), tfPassword.getText())){
				btHome.setEnabled(true);
				//bum = new BoundaryUtenteMenu();
				JOptionPane.showMessageDialog(this, "Login Effettuato! Benvenuto " + controllore.getUtente(tfUserName.getText()).getName());
				//cardLayout.show(pannelloGenerale, "Home");
			}else{
				JOptionPane.showMessageDialog(this,"Dati non validi!", "Attenzione!", JOptionPane.WARNING_MESSAGE);
			}
		}
		if(e.getActionCommand()=="Home"){
			
			cardLayout.show(pannelloGenerale, "Home");
			
	}
}
	
	public static void main(String[] args)
	{
		BoundaryUtenteLogin bul = new BoundaryUtenteLogin();
		bul.setVisible(true);
	}
	
}

