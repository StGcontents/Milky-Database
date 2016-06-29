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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.DataSource;
import controller.MainController;
import model.Priviledge;

public class MainView {
	
	protected static final int LAST_COMMON_INDEX = 2;
	
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
		list.addListSelectionListener(new BasicSelectionListener());
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
	
	private class BasicSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel model = ((JList) e.getSource()).getSelectionModel();
			if (!model.isSelectionEmpty()) {
				int min = model.getMinSelectionIndex();
				int max = model.getMaxSelectionIndex();
				int i;
				for (i = min; i <= max; ++i) {
					if (model.isSelectedIndex(i)) break;
					else if (i == max) {
						i = -1;
						break;
					}
				}
				
				act(i);
			}
		}
		
		private void act(int index) {
			if (priviledgeLevel == DataSource.COMMON && index > LAST_COMMON_INDEX) {
				//TODO something, maybe nothing
				return;
			}
			
			switch(index) {
			case 0:
				frame.remove(panel);
				panel = new GalaxyPanel();
				frame.add(panel);
				frame.validate();
				panel.validate();
				break;
			case 1:
				if (priviledgeLevel == DataSource.COMMON) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					MainController.instance().exitToLogin();
				}
				else {
					label.setText("You selected row #" + (index + 1));
				}
				break;
			case 3:
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				MainController.instance().exitToLogin();
				break;
			default: label.setText("You selected row #" + (index + 1));
			}
		}
	}	
}
