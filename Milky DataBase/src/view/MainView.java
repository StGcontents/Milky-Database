package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import controller.DataSource;
import controller.MainController;

public class MainView {
	
	public static final int LAST_COMMON_INDEX = 2;
	
	private Frame frame;
	private Label label;
	private JList<String> list;
	private int priviledgeLevel;
	private Container panel;
	
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
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setResizable(false);
		SpringLayout layout = new SpringLayout();
		frame.setLayout(layout);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				frame.dispose();
			}
		});
		list = new JList<>();
		list.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		DefaultListModel<String> model = new DefaultListModel<>();
		model.addElement("Find galaxy");
		model.addElement("Line flux calculations");
		list.setModel(model);
		list.addListSelectionListener(MainController.instance());
		list.setCellRenderer(new NicerCellRenderer<String>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends String> arg0, String arg1, int arg2,
					boolean arg3, boolean arg4) {
				// TODO Auto-generated method stub
				JLabel label = (JLabel) super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
				label.setText(arg1);
				return label;
			}
		});
		frame.add(list);
		
		panel = new Panel(new GridLayout(1, 1));
		label = new Label(priviledgeLevel == DataSource.ADMIN ? "WELCOME ADMIN" : "WELCOME STRANGER");
		panel.add(label);
		frame.add(panel);
		
		layout.putConstraint(SpringLayout.WEST, list, 0, SpringLayout.WEST, frame);
		layout.putConstraint(SpringLayout.EAST, list, 175, SpringLayout.WEST, frame);
		layout.putConstraint(SpringLayout.EAST, panel, 25, SpringLayout.EAST, frame);
		layout.putConstraint(SpringLayout.NORTH, panel, 25, SpringLayout.NORTH, frame);
		layout.putConstraint(SpringLayout.SOUTH, panel, -25, SpringLayout.SOUTH, frame);
		layout.putConstraint(SpringLayout.WEST, panel, 200, SpringLayout.WEST, frame);
	}
	
	private void decorateView() {
		((DefaultListModel<String>) list.getModel()).addElement("Import files");
		((DefaultListModel<String>) list.getModel()).addElement("Add user");
	}
	
	public void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	public void attachPanel(Container panel) {
		frame.remove(this.panel);
		this.panel = panel;
		frame.add(this.panel);
		
		SpringLayout layout = (SpringLayout) frame.getLayout();
		
		layout.putConstraint(SpringLayout.EAST, this.panel, 0, SpringLayout.EAST, frame);
		layout.putConstraint(SpringLayout.NORTH, this.panel, 0, SpringLayout.NORTH, frame);
		layout.putConstraint(SpringLayout.SOUTH, this.panel, 0, SpringLayout.SOUTH, frame);
		layout.putConstraint(SpringLayout.WEST, this.panel, 0, SpringLayout.EAST, list);
		
		frame.validate();
	}
}
