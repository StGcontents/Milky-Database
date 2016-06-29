package view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JList;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class GalaxyPanel extends Panel {
	
	private static Panel searchPanel;
	private Panel resultPanel;
	private CheckboxGroup group;
	private Checkbox nameBox, coordBox, redShiftBox;
	private Label label;
	private JList<String> results;
	
	public GalaxyPanel() {
		setLayout(new GridLayout(1, 1));
		setSize(400, 700);
		
		generateSearchPanel();
		add(searchPanel);
		
		setVisible(true);
	}
	
	private void generateSearchPanel() {
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
			
			final TextField nameField = new TextField();
			
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
					if (nameBox.getState()) {
						label.setText("Checkbox 1");
					}
					else if (coordBox.getState()) {
						label.setText("Checkbox 2");
					}
					else if (redShiftBox.getState()) {
						label.setText("Checkbox 3");
					}
				}
			});
			searchPanel.add(searchBtn);
			searchPanel.add(label);
			
			layout.putConstraint(SpringLayout.NORTH, nameBox, 25, SpringLayout.NORTH, searchPanel);
			layout.putConstraint(SpringLayout.WEST, nameBox, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, coordBox, 25, SpringLayout.NORTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, coordBox, 0, SpringLayout.WEST, nameBox);
			
			layout.putConstraint(SpringLayout.NORTH, redShiftBox, 25, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, redShiftBox, 0, SpringLayout.WEST, nameBox);
			
			layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.NORTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, nameField, 10, SpringLayout.EAST, redShiftBox);
			layout.putConstraint(SpringLayout.EAST, nameField, -25, SpringLayout.EAST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, searchBtn, 25, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, searchBtn, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, label, 25, SpringLayout.NORTH, searchBtn);
			layout.putConstraint(SpringLayout.WEST, label, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, label, -25, SpringLayout.HORIZONTAL_CENTER, searchPanel);
			
			searchPanel.setVisible(true);
		}
	}
}
