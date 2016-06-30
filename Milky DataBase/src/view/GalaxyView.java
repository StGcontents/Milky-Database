package view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import controller.GalaxySearchController;
import model.Galaxy;
import model.Priviledge;
import pattern.Observer;

public class GalaxyView {
	
	private static GalaxyView me;
	public static synchronized GalaxyView instance() {
		int currentPriviledgeLevel = Priviledge.instance().retrieveState();
		if (me == null || me.lastPriviledgeLevel != currentPriviledgeLevel) {
			me = new GalaxyView();
			me.lastPriviledgeLevel = currentPriviledgeLevel;
		}
		return me;
	}
	private int lastPriviledgeLevel;
	private Panel searchPanel;
	private GalaxyPanel resultPanel;
	private CheckboxGroup group;
	private Checkbox nameBox, coordBox, redShiftBox;
	private TextField nameField;
	private Label label;
	private JList<String[]> results;
	
	private ListObserverAdapter listObserver;
	private GalaxyObserverAdapter galaxyObserver;
	
	protected GalaxyView() {
		listObserver = new ListObserverAdapter(this);
		galaxyObserver = new GalaxyObserverAdapter(this);
	}
	
	public Observer<List<String[]>> getListObserver() { return this.listObserver; }
	public Observer<Galaxy> getGalaxyObserver() { return this.galaxyObserver; }
	
	public Panel generateSearchPanel() {
		if (searchPanel == null) {
			
			SpringLayout layout = new SpringLayout();
			searchPanel = new Panel();
			searchPanel.setLayout(layout);
			
			group = new CheckboxGroup();
			nameBox = new Checkbox("Search by name", group, false);
			nameBox.setName("name");
			coordBox = new Checkbox("Search in range", group, false);
			coordBox.setName("coord");
			redShiftBox = new Checkbox("Search by red shift value", group, false);
			redShiftBox.setName("redshift");
			
			searchPanel.add(nameBox);
			searchPanel.add(coordBox);
			searchPanel.add(redShiftBox);
			
			nameField = new TextField();
			
			nameBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(checked);
				}
			});
			coordBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(!checked);
				}
			});
			redShiftBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(!checked);
				}
			});
			
			group.setSelectedCheckbox(null);
			
			searchPanel.add(nameField);
			
			label = new Label();
			label.setSize(100, 20);
			
			Button searchBtn = new Button("Search");
			searchBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showGalaxy(null);
					
					if (nameBox.getState()) {
						GalaxySearchController.instance().searchNames(nameField.getText());
					}
					else if (coordBox.getState()) {
						
					}
					else if (redShiftBox.getState()) {
						
					}
				}
			});
			searchPanel.add(searchBtn);
			searchPanel.add(label);
			
			results = new JList<>();
			DefaultListModel<String[]> model = new DefaultListModel<>();
			results.setModel(model);
			results.setCellRenderer(new AkaRenderer());
			results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			results.setLayoutOrientation(JList.VERTICAL);
			results.addListSelectionListener(GalaxySearchController.instance());
			
			JScrollPane scrollPane = new JScrollPane(results);
			scrollPane.setPreferredSize(new Dimension(Panel.WIDTH, 100));
			searchPanel.add(scrollPane);
			
			layout.putConstraint(SpringLayout.NORTH, nameBox, 25, SpringLayout.NORTH, searchPanel);
			layout.putConstraint(SpringLayout.WEST, nameBox, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, coordBox, 10, SpringLayout.SOUTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, coordBox, 0, SpringLayout.WEST, nameBox);
			
			layout.putConstraint(SpringLayout.NORTH, redShiftBox, 10, SpringLayout.SOUTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, redShiftBox, 0, SpringLayout.WEST, nameBox);
			
			layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.NORTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, nameField, 10, SpringLayout.EAST, redShiftBox);
			layout.putConstraint(SpringLayout.EAST, nameField, -25, SpringLayout.EAST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, searchBtn, 20, SpringLayout.SOUTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, searchBtn, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, scrollPane, 20, SpringLayout.SOUTH, searchBtn);
			layout.putConstraint(SpringLayout.WEST, scrollPane, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, scrollPane, -25, SpringLayout.EAST, searchPanel);
			
			searchPanel.setVisible(true);
		}
		
		else reset();
		
		return searchPanel;
	}
	
	private void reset() {
		nameBox.setState(false);
		coordBox.setState(false);
		redShiftBox.setState(false);
		
		nameField.setText(null);
		
		populate(null);
		showGalaxy(null);
	}
	
	private void populate(List<String[]> names) {
		DefaultListModel<String[]> model = (DefaultListModel<String[]>) results.getModel(); 
		model.clear();
		if (names != null) for (String[] couple : names) model.addElement(couple);
	}
	
	private void showGalaxy(Galaxy galaxy) { 
		if (resultPanel == null) {
			resultPanel = new GalaxyPanel(galaxy);
			searchPanel.add(resultPanel);
			SpringLayout layout = (SpringLayout) searchPanel.getLayout();
			layout.putConstraint(SpringLayout.NORTH, resultPanel, 20, SpringLayout.SOUTH, results.getParent());
			layout.putConstraint(SpringLayout.WEST, resultPanel, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, resultPanel, -25, SpringLayout.EAST, searchPanel);
			layout.putConstraint(SpringLayout.SOUTH, resultPanel, -25, SpringLayout.SOUTH, searchPanel);
		}
		
		else resultPanel.setGalaxy(galaxy);
	}
	
	class GalaxyPanel extends Panel {
		
		private Galaxy galaxy;
		private Label nameLabel/*, coordLabel*/;
		
		protected GalaxyPanel(Galaxy galaxy) {
			setPreferredSize(new Dimension(WIDTH, 300));
			nameLabel = new Label();
			//coordLabel = new Label();
			setGalaxy(galaxy);
		}
		
		protected void setGalaxy(Galaxy galaxy) {
			this.galaxy = galaxy;
			updateInformation();
		}
		
		private void updateInformation() {
			if (galaxy == null) setVisible(false);
			else {
				nameLabel.setText("Name: " + galaxy.getName());
				//coordLabel.setText("Coordinates: " + galaxy.getCoordinates().toString());
			}
		}
	}
	
	class AkaRenderer implements ListCellRenderer<String[]> {

		@Override
		public Component getListCellRendererComponent(JList<? extends String[]> list, String[] names, int index,
				boolean isSelected, boolean cellHasFocus) {
			String string = names[0];
			if (names[1] != null && !"".equals(names[1])) string += " (aka " + names[1] + ")";
			
			Label label = new Label(string);
			if (isSelected) {
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}
			else {
				label.setBackground(Color.WHITE);
				label.setForeground(Color.BLACK);
			}	
			return label;
		}	
	}
	
	class ListObserverAdapter extends Observer<List<String[]>> {
		private GalaxyView adaptee;
		protected ListObserverAdapter(GalaxyView adaptee) { this.adaptee = adaptee; } 
		@Override public void stateChanged() { adaptee.populate(getSubject().retrieveState()); }
	}
	
	class GalaxyObserverAdapter extends Observer<Galaxy> {
		private GalaxyView adaptee;
		protected GalaxyObserverAdapter(GalaxyView adaptee) { this.adaptee = adaptee; }
		@Override public void stateChanged() { adaptee.showGalaxy(getSubject().retrieveState()); }
	}
}
