package view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import controller.GalaxySearchController;
import model.AdaptableValue;
import model.Galaxy;
import model.Galaxy.Coordinates;
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
	private JScrollPane scrollPane;
	private Button searchBtn;
	
	private ListObserverAdapter listObserver;
	private GalaxyObserverAdapter galaxyObserver;
	private FluxObserverAdapter fluxObserver;
	
	protected GalaxyView() {
		listObserver = new ListObserverAdapter(this);
		galaxyObserver = new GalaxyObserverAdapter(this);
		fluxObserver = new FluxObserverAdapter(this);
	}
	
	public Observer<List<AdaptableValue>> getListObserver() { return this.listObserver; }
	public Observer<Galaxy> getGalaxyObserver() { return this.galaxyObserver; }
	public Observer<Void> getFluxObserver() { return this.fluxObserver; }
	
	public Panel generateSearchPanel() {
		if (searchPanel == null) {
			SpringLayout layout = new SpringLayout();
			searchPanel = new Panel();
			searchPanel.setLayout(layout);
			
			initCheckboxes(layout);
			initButton(layout);
			initList(layout);
			initSearchByName(layout);
			initSearchByDistance(layout);
			initSearchByRedshift(layout);
				
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
		if (values != null) 
			for (AdaptableValue value : values) model.addElement(value);
		
		searchBtn.setEnabled(true);
	}
	
	private void showGalaxy(Galaxy galaxy) { 
		if (resultPanel == null) {
			resultPanel = new GalaxyPanel(galaxy);
			searchPanel.add(resultPanel);
			SpringLayout layout = (SpringLayout) searchPanel.getLayout();
			layout.putConstraint(SpringLayout.NORTH, resultPanel, 150, SpringLayout.SOUTH, results.getParent());
			layout.putConstraint(SpringLayout.WEST, resultPanel, 25, SpringLayout.WEST, searchPanel);
			layout.putConstraint(SpringLayout.EAST, resultPanel, -25, SpringLayout.EAST, searchPanel);
			layout.putConstraint(SpringLayout.SOUTH, resultPanel, -25, SpringLayout.SOUTH, searchPanel);
		}
		else resultPanel.setGalaxy(galaxy);
	}
	
	public boolean updateFluxes() { return resultPanel.updateFluxes(); }
	
	private void initCheckboxes(SpringLayout layout) {
		CheckboxGroup group = new CheckboxGroup();
		nameBox = new Checkbox("Search by name", group, false);
		nameBox.setName("name");
		coordBox = new Checkbox("Search in range", group, false);
		coordBox.setName("coord");
		redShiftBox = new Checkbox("Search by red shift value", group, false);
		redShiftBox.setName("redshift");
		
		nameBox.addItemListener(new OptionCheckListener(0));
		coordBox.addItemListener(new OptionCheckListener(1));
		redShiftBox.addItemListener(new OptionCheckListener(2));
		
		group.setSelectedCheckbox(null);
		
		searchPanel.add(nameBox);
		searchPanel.add(coordBox);
		searchPanel.add(redShiftBox);
		
		layout.putConstraint(SpringLayout.NORTH, nameBox, 25, SpringLayout.NORTH, searchPanel);
		layout.putConstraint(SpringLayout.WEST, nameBox, 25, SpringLayout.WEST, searchPanel);
		
		layout.putConstraint(SpringLayout.NORTH, coordBox, 10, SpringLayout.SOUTH, nameBox);
		layout.putConstraint(SpringLayout.WEST, coordBox, 0, SpringLayout.WEST, nameBox);
		
		layout.putConstraint(SpringLayout.NORTH, redShiftBox, 10, SpringLayout.SOUTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, redShiftBox, 0, SpringLayout.WEST, nameBox);
	}
	
	private void initSearchByName(SpringLayout layout) {
		nameField = new TextField();
		nameField.setEnabled(false);
		searchPanel.add(nameField);
		layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.NORTH, nameBox);
		layout.putConstraint(SpringLayout.WEST, nameField, 25, SpringLayout.EAST, redShiftBox);
		layout.putConstraint(SpringLayout.EAST, nameField, -25, SpringLayout.EAST, searchPanel);
	}
	
	private void initSearchByDistance(SpringLayout layout) {
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
	}
	
	private void initSearchByRedshift(SpringLayout layout) {
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
	}
	
	private void initButton(SpringLayout layout) {
		searchBtn = new Button("Search");
		searchBtn.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				showGalaxy(null);
				populate(null);
				searchBtn.setEnabled(false);
				
				if (nameBox.getState()) {
					String name = nameField.getText();
					if (name != null && !"".equals(name))
						GalaxySearchController.instance().searchNames(name);
					else searchBtn.setEnabled(true);		
				}
				
				else if (coordBox.getState()) {
					Integer h = hoursField.getValue(), m = minField.getValue(), 
							d = degreesField.getValue(), arcm = arcminField.getValue(),
							l = dLimitField.getValue();
					Double  s = secondsField.getValue(), arcs = arcsecField.getValue(),
							r = rangeField.getValue();
					searchBtn.setEnabled(true);
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
						searchBtn.setEnabled(false);
					}
				}
				
				else if (redShiftBox.getState()) {
					Double redshift = redshiftField.getValue();
					if (redshift == null) {
						redshiftField.requestFocus();
						searchBtn.setEnabled(true);
						return;
					}
					Integer limit = rsLimitField.getValue();
					if (limit == null) {
						rsLimitField.requestFocus();
						searchBtn.setEnabled(true);
						return;
					}
					boolean higherThen = rsHigherBox.getState();
					GalaxySearchController.instance().searchByRedshiftValue(redshift.doubleValue(), higherThen, limit.intValue());
				}
				
				else searchBtn.setEnabled(true);
			}
		});
		searchPanel.add(searchBtn);
	}
	
	private void initList(SpringLayout layout) {
		results = new JList<>();
		DefaultListModel<AdaptableValue> model = new DefaultListModel<>();
		results.setModel(model);
		results.setCellRenderer(new GalaxyListRenderer());
		results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		results.setLayoutOrientation(JList.VERTICAL);
		results.addListSelectionListener(GalaxySearchController.instance());
		
		scrollPane = new JScrollPane(results);
		scrollPane.setPreferredSize(new Dimension(Panel.WIDTH, 100));
		searchPanel.add(scrollPane);
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
	
	class FluxObserverAdapter extends Observer<Void> {
		private GalaxyView adaptee;
		protected FluxObserverAdapter(GalaxyView adaptee) { this.adaptee = adaptee; }
		@Override public void stateChanged() { adaptee.updateFluxes(); }
	}
	
	private class OptionCheckListener implements ItemListener {
		private int position;
		protected OptionCheckListener(int position) {
			this.position = position;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				enableNameSearch(position == 0);
				enableDistanceSearch(position == 1);
				enableRedshiftSearch(position == 2);
			}
		}
		
		private void enableNameSearch(boolean enable) {
			nameField.setEnabled(enable);
		}
		
		private void enableDistanceSearch(boolean enable) {
			hoursField.setEnabled(enable);
			minField.setEnabled(enable);
			secondsField.setEnabled(enable);
			plusBox.setEnabled(enable);
			minusBox.setEnabled(enable);
			degreesField.setEnabled(enable);
			arcminField.setEnabled(enable);
			arcsecField.setEnabled(enable);
			rangeField.setEnabled(enable);
			dLimitField.setEnabled(enable);
		}
		
		private void enableRedshiftSearch(boolean enable) {
			redshiftField.setEnabled(enable);
			rsLimitField.setEnabled(enable);
			rsHigherBox.setEnabled(enable);
			rsLowerBox.setEnabled(enable);
		}	
	}
}
