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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import controller.GalaxySearchController;
import model.AdaptableValue;
import model.Galaxy;
import model.Galaxy.Coordinates;
import model.Galaxy.Luminosity;
import model.Priviledge;
import pattern.Observer;

@SuppressWarnings("rawtypes")
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
	private Checkbox nameBox, coordBox, redShiftBox, rsHigherBox, rsLowerBox, plusBox, minusBox;
	private TextField nameField;
	private DoubleTextField redshiftField, secondsField, arcsecField, rangeField;
	private IntTextField rsLimitField, dLimitField, hoursField, minField, degreesField, arcminField;
	private JList<AdaptableValue> results;
	
	private ListObserverAdapter listObserver;
	private GalaxyObserverAdapter galaxyObserver;
	
	protected GalaxyView() {
		listObserver = new ListObserverAdapter(this);
		galaxyObserver = new GalaxyObserverAdapter(this);
	}
	
	public Observer<List<AdaptableValue>> getListObserver() { return this.listObserver; }
	public Observer<Galaxy> getGalaxyObserver() { return this.galaxyObserver; }
	
	public Panel generateSearchPanel() {
		if (searchPanel == null) {
			
			SpringLayout layout = new SpringLayout();
			searchPanel = new Panel();
			searchPanel.setLayout(layout);
			
			CheckboxGroup group = new CheckboxGroup();
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
			nameField.setEnabled(false);
			
			redshiftField = new DoubleTextField();
			redshiftField.setEnabled(false);
			rsLimitField = new IntTextField();
			rsLimitField.setEnabled(false);
			CheckboxGroup highLowGroup = new CheckboxGroup();
			rsHigherBox = new Checkbox(">=", highLowGroup, true);
			rsLowerBox = new Checkbox("<=", highLowGroup, false);
			rsHigherBox.setEnabled(false);
			rsLowerBox.setEnabled(false);
			Label rsValueLabel = new Label("value: ");
			Label rsLimitLabel = new Label("limit: ");
			
			searchPanel.add(rsValueLabel);
			searchPanel.add(redshiftField);
			searchPanel.add(rsHigherBox);
			searchPanel.add(rsLowerBox);
			searchPanel.add(rsLimitLabel);
			searchPanel.add(rsLimitField);
			
			Label hLabel = new Label("h");
			hoursField = new IntTextField();
			hoursField.setEnabled(false);
			Label minLabel = new Label("m");
			minField = new IntTextField();
			minField.setEnabled(false);
			Label secLabel = new Label("s");
			secondsField = new DoubleTextField();
			secondsField.setEnabled(false);
			CheckboxGroup signGroup = new CheckboxGroup();
			plusBox = new Checkbox("+", signGroup, true);
			plusBox.setEnabled(false);
			minusBox = new Checkbox("-", signGroup, false);
			minusBox.setEnabled(false);
			degreesField = new IntTextField();
			degreesField.setEnabled(false);
			Label degLabel = new Label("°");
			arcminField = new IntTextField();
			arcminField.setEnabled(false);
			Label arcminLabel = new Label("'");
			arcsecField = new DoubleTextField();
			arcsecField.setEnabled(false);
			Label rangeLabel = new Label("range: ");
			rangeField = new DoubleTextField();
			rangeField.setEnabled(false);
			Label arcsecLabel = new Label("\"");
			Label dLimitLabel = new Label("limit: ");
			dLimitField = new IntTextField();
			dLimitField.setEnabled(false);
			
			searchPanel.add(hLabel);
			searchPanel.add(hoursField);
			searchPanel.add(minLabel);
			searchPanel.add(minField);
			searchPanel.add(secLabel);
			searchPanel.add(secondsField);
			searchPanel.add(plusBox);
			searchPanel.add(minusBox);
			searchPanel.add(degreesField);
			searchPanel.add(degLabel);
			searchPanel.add(arcminField);
			searchPanel.add(arcminLabel);
			searchPanel.add(arcsecField);
			searchPanel.add(arcsecLabel);
			searchPanel.add(rangeLabel);
			searchPanel.add(rangeField);
			searchPanel.add(dLimitLabel);
			searchPanel.add(dLimitField);
						
			nameBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(checked);
					
					redshiftField.setEnabled(!checked);
					rsLimitField.setEnabled(!checked);
					rsHigherBox.setEnabled(!checked);
					rsLowerBox.setEnabled(!checked);
					
					hoursField.setEnabled(!checked);
					minField.setEnabled(!checked);
					secondsField.setEnabled(!checked);
					plusBox.setEnabled(!checked);
					minusBox.setEnabled(!checked);
					degreesField.setEnabled(!checked);
					arcminField.setEnabled(!checked);
					arcsecField.setEnabled(!checked);
					rangeField.setEnabled(!checked);
					dLimitField.setEnabled(!checked);
				}
			});
			coordBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(!checked);
					
					redshiftField.setEnabled(!checked);
					rsLimitField.setEnabled(!checked);
					rsHigherBox.setEnabled(!checked);
					rsLowerBox.setEnabled(!checked);
					
					hoursField.setEnabled(checked);
					minField.setEnabled(checked);
					secondsField.setEnabled(checked);
					plusBox.setEnabled(checked);
					minusBox.setEnabled(checked);
					degreesField.setEnabled(checked);
					arcminField.setEnabled(checked);
					arcsecField.setEnabled(checked);
					rangeField.setEnabled(checked);
					dLimitField.setEnabled(checked);
				}
			});
			redShiftBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean checked = e.getStateChange() == ItemEvent.SELECTED;
					nameField.setEnabled(!checked);
					
					redshiftField.setEnabled(checked);
					rsLimitField.setEnabled(checked);
					rsHigherBox.setEnabled(checked);
					rsLowerBox.setEnabled(checked);
					
					hoursField.setEnabled(!checked);
					minField.setEnabled(!checked);
					secondsField.setEnabled(!checked);
					plusBox.setEnabled(!checked);
					minusBox.setEnabled(!checked);
					degreesField.setEnabled(!checked);
					arcminField.setEnabled(!checked);
					arcsecField.setEnabled(!checked);
					rangeField.setEnabled(!checked);
					dLimitField.setEnabled(!checked);
				}
			});
			
			group.setSelectedCheckbox(null);
			
			searchPanel.add(nameField);
			
			Button searchBtn = new Button("Search");
			searchBtn.addActionListener(new ActionListener() {		
				@Override
				public void actionPerformed(ActionEvent e) {
					showGalaxy(null);
					if (nameBox.getState()) {
						GalaxySearchController.instance().searchNames(nameField.getText());
					}
					else if (coordBox.getState()) {
						Integer h = hoursField.getValue(), m = minField.getValue(), 
								d = degreesField.getValue(), arcm = arcminField.getValue(),
								l = dLimitField.getValue();
						Double  s = secondsField.getValue(), arcs = arcsecField.getValue(),
								r = rangeField.getValue();
						if (h == null) hoursField.requestFocus();
						else if (m == null) minField.requestFocus(); 
						else if (s == null) secondsField.requestFocus();
						else if (d == null) degreesField.requestFocus();
						else if (arcm == null) arcminField.requestFocus();
						else if (arcs == null) arcsecField.requestFocus();
						else if (r == null) rangeField.requestFocus();
						else if (l == null) dLimitField.requestFocus();
						else {
							Coordinates center = new Coordinates(h, m, s, plusBox.getState(), d, arcm, arcs);
							GalaxySearchController.instance().searchInRange(center, r.doubleValue(), l.intValue());
						}
					}
					else if (redShiftBox.getState()) {
						Double redshift = redshiftField.getValue();
						if (redshift == null) {
							redshiftField.requestFocus();
							return;
						}
						Integer limit = rsLimitField.getValue();
						if (limit == null) {
							rsLimitField.requestFocus();
							return;
						}
						boolean higherThen = rsHigherBox.getState();
						GalaxySearchController.instance().searchByRedshiftValue(redshift.doubleValue(), higherThen, limit.intValue());
					}
				}
			});
			searchPanel.add(searchBtn);
			
			results = new JList<>();
			DefaultListModel<AdaptableValue> model = new DefaultListModel<>();
			results.setModel(model);
			results.setCellRenderer(new AkaRenderer());
			results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			results.setLayoutOrientation(JList.VERTICAL);
			results.addListSelectionListener(GalaxySearchController.instance());
			
			JScrollPane scrollPane = new JScrollPane(results);
			scrollPane.setPreferredSize(new Dimension(Panel.WIDTH, 100));
			searchPanel.add(scrollPane);
			
			//CHECKBOXES
			layout.putConstraint(SpringLayout.NORTH, nameBox, 25, SpringLayout.NORTH, searchPanel);
			layout.putConstraint(SpringLayout.WEST, nameBox, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, coordBox, 10, SpringLayout.SOUTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, coordBox, 0, SpringLayout.WEST, nameBox);
			
			layout.putConstraint(SpringLayout.NORTH, redShiftBox, 10, SpringLayout.SOUTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, redShiftBox, 0, SpringLayout.WEST, nameBox);
			
			//SEARCH BY NAME
			layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.NORTH, nameBox);
			layout.putConstraint(SpringLayout.WEST, nameField, 25, SpringLayout.EAST, redShiftBox);
			layout.putConstraint(SpringLayout.EAST, nameField, -25, SpringLayout.EAST, searchPanel);
			
			//SEARCH BY REDSHIFT VALUE
			layout.putConstraint(SpringLayout.NORTH, searchBtn, 20, SpringLayout.SOUTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, searchBtn, 25, SpringLayout.WEST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, scrollPane, 20, SpringLayout.SOUTH, searchBtn);
			layout.putConstraint(SpringLayout.WEST, scrollPane, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, scrollPane, -25, SpringLayout.EAST, searchPanel);
			
			layout.putConstraint(SpringLayout.NORTH, rsValueLabel, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, rsValueLabel, 25, SpringLayout.EAST, redShiftBox);
			
			layout.putConstraint(SpringLayout.NORTH, redshiftField, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, redshiftField, 10, SpringLayout.EAST, rsValueLabel);
			layout.putConstraint(SpringLayout.EAST, redshiftField, 60, SpringLayout.WEST, redshiftField);
			
			layout.putConstraint(SpringLayout.NORTH, rsHigherBox, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, rsHigherBox, 10, SpringLayout.EAST, redshiftField);
			layout.putConstraint(SpringLayout.NORTH, rsLowerBox, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, rsLowerBox, 10, SpringLayout.EAST, rsHigherBox);
			
			layout.putConstraint(SpringLayout.NORTH, rsLimitLabel, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, rsLimitLabel, 10, SpringLayout.EAST, rsLowerBox);
			
			layout.putConstraint(SpringLayout.NORTH, rsLimitField, 0, SpringLayout.NORTH, redShiftBox);
			layout.putConstraint(SpringLayout.WEST, rsLimitField, 10, SpringLayout.EAST, rsLimitLabel);
			layout.putConstraint(SpringLayout.EAST, rsLimitField, 60, SpringLayout.WEST, rsLimitField);
			
			//SEARCH BY RANGE
			layout.putConstraint(SpringLayout.NORTH, hLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, hLabel, 25, SpringLayout.EAST, redShiftBox);
			
			layout.putConstraint(SpringLayout.NORTH, hoursField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, hoursField, 5, SpringLayout.EAST, hLabel);
			layout.putConstraint(SpringLayout.EAST, hoursField, 30, SpringLayout.WEST, hoursField);
			
			layout.putConstraint(SpringLayout.NORTH, minLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, minLabel, 10, SpringLayout.EAST, hoursField);
			
			layout.putConstraint(SpringLayout.NORTH, minField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, minField, 5, SpringLayout.EAST, minLabel);
			layout.putConstraint(SpringLayout.EAST, minField, 30, SpringLayout.WEST, minField);
			
			layout.putConstraint(SpringLayout.NORTH, secLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, secLabel, 10, SpringLayout.EAST, minField);
			
			layout.putConstraint(SpringLayout.NORTH, secondsField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, secondsField, 5, SpringLayout.EAST, secLabel);
			layout.putConstraint(SpringLayout.EAST, secondsField, 60, SpringLayout.WEST, secondsField);
			
			layout.putConstraint(SpringLayout.NORTH, plusBox, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, plusBox, 10, SpringLayout.EAST, secondsField);
			
			layout.putConstraint(SpringLayout.NORTH, minusBox, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, minusBox, 5, SpringLayout.EAST, plusBox);
			
			layout.putConstraint(SpringLayout.NORTH, degreesField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, degreesField, 10, SpringLayout.EAST, minusBox);
			layout.putConstraint(SpringLayout.EAST, degreesField, 30, SpringLayout.WEST, degreesField);
			
			layout.putConstraint(SpringLayout.NORTH, degLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, degLabel, 5, SpringLayout.EAST, degreesField);
			
			layout.putConstraint(SpringLayout.NORTH, arcminField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, arcminField, 10, SpringLayout.EAST, degLabel);
			layout.putConstraint(SpringLayout.EAST, arcminField, 30, SpringLayout.WEST, arcminField);
			
			layout.putConstraint(SpringLayout.NORTH, arcminLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, arcminLabel, 5, SpringLayout.EAST, arcminField);
			
			layout.putConstraint(SpringLayout.NORTH, arcsecField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, arcsecField, 10, SpringLayout.EAST, arcminLabel);
			layout.putConstraint(SpringLayout.EAST, arcsecField, 60, SpringLayout.WEST, arcsecField);
			
			layout.putConstraint(SpringLayout.NORTH, arcsecLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, arcsecLabel, 5, SpringLayout.EAST, arcsecField);
			
			layout.putConstraint(SpringLayout.NORTH, rangeLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, rangeLabel, 15, SpringLayout.EAST, arcsecLabel);
			
			layout.putConstraint(SpringLayout.NORTH, rangeField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, rangeField, 5, SpringLayout.EAST, rangeLabel);
			layout.putConstraint(SpringLayout.EAST, rangeField, 60, SpringLayout.WEST, rangeField);
			
			layout.putConstraint(SpringLayout.NORTH, dLimitLabel, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, dLimitLabel, 10, SpringLayout.EAST, rangeField);
			
			layout.putConstraint(SpringLayout.NORTH, dLimitField, 0, SpringLayout.NORTH, coordBox);
			layout.putConstraint(SpringLayout.WEST, dLimitField, 5, SpringLayout.EAST, dLimitLabel);
			layout.putConstraint(SpringLayout.EAST, dLimitField, 60, SpringLayout.WEST, dLimitField);
			
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
		
		hoursField.setText(null);
		minField.setText(null);
		secondsField.setText(null);
		plusBox.setState(true);
		minusBox.setState(false);
		degreesField.setText(null);
		arcminField.setText(null);
		arcsecField.setText(null);
		rangeField.setText(null);
		dLimitField.setText(null);
		
		populate(null);
		showGalaxy(null);
	}
	
	private void populate(List<AdaptableValue> values) {
		DefaultListModel<AdaptableValue> model = (DefaultListModel<AdaptableValue>) results.getModel(); 
		model.clear();
		if (values != null) for (AdaptableValue value : values) model.addElement(value);
	}
	
	private void showGalaxy(Galaxy galaxy) { 
		if (resultPanel == null) {
			resultPanel = new GalaxyPanel(galaxy);
			searchPanel.add(resultPanel);
			SpringLayout layout = (SpringLayout) searchPanel.getLayout();
			layout.putConstraint(SpringLayout.NORTH, resultPanel, 150, SpringLayout.SOUTH, results.getParent());
			layout.putConstraint(SpringLayout.WEST, resultPanel, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, resultPanel, -25, SpringLayout.HORIZONTAL_CENTER, searchPanel);
			layout.putConstraint(SpringLayout.SOUTH, resultPanel, -25, SpringLayout.SOUTH, searchPanel);
		}
		else resultPanel.setGalaxy(galaxy);
	}
	
	@SuppressWarnings("serial")
	class GalaxyPanel extends Panel {
		
		private Galaxy galaxy;
		private JLabel nameLabel, rightAscLabel, declinationLabel, distanceLabel, 
					   redShiftLabel, spectreLabel, lumLabel, metallicityLabel;
		
		protected GalaxyPanel(Galaxy galaxy) {
			setup();
			setGalaxy(galaxy);
		}
		
		private void setup() {
			SpringLayout layout = new SpringLayout();
			setLayout(layout);
			
			nameLabel = new JLabel();
			nameLabel.setPreferredSize(new Dimension(200, 20));
			this.add(nameLabel);
			layout.putConstraint(SpringLayout.WEST, nameLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, nameLabel, 25, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, nameLabel, 0, SpringLayout.EAST, this);
			
			rightAscLabel = new JLabel();
			rightAscLabel.setPreferredSize(new Dimension(200, 20));
			this.add(rightAscLabel);
			layout.putConstraint(SpringLayout.WEST, rightAscLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, rightAscLabel, 10, SpringLayout.SOUTH, nameLabel);
			layout.putConstraint(SpringLayout.EAST, rightAscLabel, 0, SpringLayout.EAST, this);
			
			declinationLabel = new JLabel();
			declinationLabel.setPreferredSize(new Dimension(200, 20));
			this.add(declinationLabel);
			layout.putConstraint(SpringLayout.WEST, declinationLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, declinationLabel, 10, SpringLayout.SOUTH, rightAscLabel);
			layout.putConstraint(SpringLayout.EAST, declinationLabel, 0, SpringLayout.EAST, this);
			
			redShiftLabel = new JLabel();
			redShiftLabel.setPreferredSize(new Dimension(200, 20));
			this.add(redShiftLabel);
			layout.putConstraint(SpringLayout.WEST, redShiftLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, redShiftLabel, 10, SpringLayout.SOUTH, declinationLabel);
			layout.putConstraint(SpringLayout.EAST, redShiftLabel, 0, SpringLayout.EAST, this);
			
			distanceLabel = new JLabel();
			distanceLabel.setPreferredSize(new Dimension(200, 20));
			this.add(distanceLabel);
			layout.putConstraint(SpringLayout.WEST, distanceLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, distanceLabel, 10, SpringLayout.SOUTH, redShiftLabel);
			layout.putConstraint(SpringLayout.EAST, distanceLabel, 0, SpringLayout.EAST, this);
			
			spectreLabel = new JLabel();
			spectreLabel.setPreferredSize(new Dimension(200, 20));
			this.add(spectreLabel);
			layout.putConstraint(SpringLayout.WEST, spectreLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, spectreLabel, 10, SpringLayout.SOUTH, distanceLabel);
			layout.putConstraint(SpringLayout.EAST, spectreLabel, 0, SpringLayout.EAST, this);
			
			lumLabel = new JLabel();
			this.add(lumLabel);
			layout.putConstraint(SpringLayout.WEST, lumLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, lumLabel, 10, SpringLayout.SOUTH, spectreLabel);
			layout.putConstraint(SpringLayout.EAST, lumLabel, 0, SpringLayout.EAST, this);
			
			metallicityLabel = new JLabel();
			metallicityLabel.setPreferredSize(new Dimension(200, 20));
			this.add(metallicityLabel);
			layout.putConstraint(SpringLayout.WEST, metallicityLabel, 0, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, metallicityLabel, 10, SpringLayout.SOUTH, lumLabel);
			layout.putConstraint(SpringLayout.EAST, metallicityLabel, 0, SpringLayout.EAST, this);
		}
		
		protected void setGalaxy(Galaxy galaxy) {
			this.galaxy = galaxy;
			updateInformation();
		}
		
		private void updateInformation() {
			if (galaxy == null) setVisible(false);
			else {
				String name = "Name: " + galaxy.getName();
				int howMany;
				if ((howMany = galaxy.getAlternativeNames().length) > 0) {
					name += "; also known as: ";
					for (int i = 0; i < howMany; ++i) {
						if (i != 0) name += ", ";
						name += galaxy.getAlternativeNames()[i];
					}
				}
				nameLabel.setText(name);
				
				Galaxy.Coordinates coordinates = galaxy.getCoordinates();
				rightAscLabel.setText("Right ascension: " + coordinates.getRightAscensionHours() + "h "
									+ coordinates.getRightAscensionMinutes() + "m "
									+ coordinates.getRightAscensionSeconds() + "s");
				declinationLabel.setText("Declination: " + (coordinates.getSign() ? "+" : "-")
									   + coordinates.getDegrees() + "° "
									   + coordinates.getArcMinutes() + "' "
									   + coordinates.getArcSeconds() + "\"" );
				
				redShiftLabel.setText("Redshift: " + galaxy.getRedShift());
				distanceLabel.setText("Distance: " + 
					(galaxy.getDistance() == null ? "/" : galaxy.getDistance().intValue()));
				spectreLabel.setText("Spectral group: " + galaxy.getSpectre());
				
				String luminosity = "<html>Luminosity:";
				boolean valueExists = false;
				int i;
				Luminosity lums[] = galaxy.getLuminosities();
				String ions[] = {"(NeV 14.3)", "(NeV 24.3)", "(OIV 25.9)" }; 
				for (i = 0; i < 3; ++i) {
					if (lums[i] == null) continue;
					
					if (valueExists) luminosity += ",";
					else valueExists = true;
					luminosity += "<br>&#32;&#32;&#32;&#45;&#32;" + ions[i] + " " + lums[i].getValue() + (lums[i].isLimit() ? "[limit]" : "");
				}
				if (!valueExists) luminosity += "/";
				lumLabel.setText(luminosity + "</html>");
				
				String metallicity = "Metallicity: ";
				metallicity += galaxy.getMetallicity() == null ? "/" :
					galaxy.getMetallicity() + 
						(galaxy.getMetallicityError() == null ? "" : 
							" [error: " + galaxy.getMetallicityError().intValue() + "]");
				metallicityLabel.setText(metallicity);
				
				setVisible(true);
			}
		}
	}
	
	class AkaRenderer implements ListCellRenderer<AdaptableValue> {

		@Override
		public Component getListCellRendererComponent(JList<? extends AdaptableValue> list, AdaptableValue value, int index,
				boolean isSelected, boolean cellHasFocus) {
			
			JList.DropLocation location = list.getDropLocation();
			Color background;
	        Color foreground;
	         
			if (location != null && !location.isInsert() && location.getIndex() == index) {
	             background = Color.CYAN;
	             foreground = Color.BLACK;
			}
	        else if (isSelected) {
	        	background = Color.BLUE;
	            foreground = Color.WHITE;
			}
			else {
				background = Color.WHITE;
	            foreground = Color.BLACK;
			}	
			
			String string = value.getDescription();
			
			JLabel label = new JLabel(string);
			label.setSize(new Dimension(Label.WIDTH, 10));
			label.setBackground(background);
			label.setForeground(foreground);
			label.setOpaque(true);
			
			return label;
		}
		
	}
	
	class ListObserverAdapter extends Observer<List<AdaptableValue>> {
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
