package view;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.DataSource;
import controller.MainController;

public class MainView {
	
	protected int LAST_COMMON_INDEX = 2;
	
	private Frame frame;
	private Label label;
	private JList<String> list;
	private int priviledgeLevel;
	
	public void generateView(int level) {
		priviledgeLevel = level;
		
		generateBasicView();
		if (priviledgeLevel == DataSource.ADMIN)
			decorateView();
		
		((DefaultListModel<String>) list.getModel()).addElement("Exit");
		
		frame.setVisible(true);
	}
	
	private void generateBasicView() {
		frame = new Frame("Logged in");
		frame.setSize(400, 400);
		frame.setLayout(new GridLayout(1, 1));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("exiting");
				frame.dispose();
			}
		});
		list = new JList<>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		DefaultListModel<String> model = new DefaultListModel<>();
		model.addElement("Find galaxy");
		list.setModel(model);
		list.addListSelectionListener(new BasicSelectionListener());
		frame.add(list);
		
		label = new Label(priviledgeLevel == DataSource.ADMIN ? "WELCOME ADMIN" : "WELCOME STRANGER");
		frame.add(label);		
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
			case 1:
				if (priviledgeLevel == DataSource.COMMON) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					MainController.instance(priviledgeLevel).exitToLogin();
				}
				else label.setText("You selected row #" + (index + 1));
				break;
			case 3:
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				MainController.instance(priviledgeLevel).exitToLogin();
				break;
			default: label.setText("You selected row #" + (index + 1));
			}
		}
	}	
}
