package view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import controller.DataSource;
import controller.MainController;

public class MainView {
	
	public static final int LAST_COMMON_INDEX = 2;
	
	private Frame frame;
	private Label label;
	private JList<String> list;
	private int priviledgeLevel;
	private Panel panel;
	
	public MainView(int priviledgeLevel) {
		this.priviledgeLevel = priviledgeLevel;
	}
	
	public void generateView() {		
		generateBasicView();
		
		if (priviledgeLevel == DataSource.ADMIN)
			decorateView();
		
		((DefaultListModel<String>) list.getModel()).addElement("Exit");
		
		frame.setVisible(true);
	}
	
	private void generateBasicView() {
		frame = new Frame("Logged in");
		frame.setSize(1000, 700);
		GridLayout layout = new GridLayout(1, 2);
		frame.setLayout(layout);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("exiting");
				frame.dispose();
			}
		});
		list = new JList<>();
		list.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		DefaultListModel<String> model = new DefaultListModel<>();
		model.addElement("Find galaxy");
		list.setModel(model);
		list.addListSelectionListener(MainController.instance());
		frame.add(list);
		
		panel = new Panel(new GridLayout(1, 1));
		label = new Label(priviledgeLevel == DataSource.ADMIN ? "WELCOME ADMIN" : "WELCOME STRANGER");
		panel.add(label);
		frame.add(panel);	
	}
	
	private void decorateView() {
		((DefaultListModel<String>) list.getModel()).addElement("Import files");
		((DefaultListModel<String>) list.getModel()).addElement("Add user");
	}
	
	public void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	public void attachPanel(Panel panel) {
		frame.remove(this.panel);
		frame.validate();
		this.panel = panel;
		frame.add(this.panel);
		frame.validate();
	}
}
