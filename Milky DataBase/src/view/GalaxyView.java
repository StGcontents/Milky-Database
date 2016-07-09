package view;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import controller.GalaxySearchController;
import model.AdaptableValue;
import model.Galaxy;
import model.Galaxy.Coordinates;
import model.Priviledge;
import pattern.Observer;

@SuppressWarnings("rawtypes")
public class GalaxyView extends View {
	
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
	private JPanel searchPanel, distancePanel, redshiftPanel;
	private GalaxyPanel resultPanel;
	private JRadioButton nameBox, coordBox, redshiftBox, rsHigherBox, rsLowerBox, plusBox, minusBox;
	private JTextArea nameField;
	private DoubleTextField redshiftField, secondsField, arcsecField, rangeField;
	private IntTextField rsLimitField, dLimitField, hoursField, minField, degreesField, arcminField;
	private JList<AdaptableValue> results;
	private JScrollPane scrollPane;
	private JButton searchBtn;
	
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
	
	@Override
	public JPanel generateView() {
		if (searchPanel == null) {
			SpringLayout layout = new SpringLayout();
			searchPanel = new JPanel();
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
	
	@Override
	protected void reset() {
		nameBox.setSelected(false);
		coordBox.setSelected(false);
		redshiftBox.setSelected(false);
		
		nameField.setText(null);
		
		hoursField.setText(null);
		minField.setText(null);
		secondsField.setText(null);
		plusBox.setSelected(true);
		minusBox.setSelected(false);
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
		ButtonGroup group = new ButtonGroup();
		nameBox = new JRadioButton("Search by name", false);
		nameBox.setName("name");
		coordBox = new JRadioButton("Search in range", false);
		coordBox.setName("coord");
		redshiftBox = new JRadioButton("Search by red shift value", false);
		redshiftBox.setName("redshift");
		
		group.add(nameBox);
		group.add(coordBox);
		group.add(redshiftBox);
		
		nameBox.addItemListener(new OptionCheckListener(0));
		coordBox.addItemListener(new OptionCheckListener(1));
		redshiftBox.addItemListener(new OptionCheckListener(2));
		
		searchPanel.add(nameBox);
		searchPanel.add(coordBox);
		searchPanel.add(redshiftBox);
		
		layout.putConstraint(SpringLayout.NORTH, nameBox, 25, SpringLayout.NORTH, searchPanel);
		layout.putConstraint(SpringLayout.WEST, nameBox, 25, SpringLayout.WEST, searchPanel);
		
		layout.putConstraint(SpringLayout.NORTH, coordBox, 10, SpringLayout.SOUTH, nameBox);
		layout.putConstraint(SpringLayout.WEST, coordBox, 0, SpringLayout.WEST, nameBox);
		
		layout.putConstraint(SpringLayout.NORTH, redshiftBox, 10, SpringLayout.SOUTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, redshiftBox, 0, SpringLayout.WEST, nameBox);
	}
	
	private void initSearchByName(SpringLayout layout) {
		nameField = new JTextArea();
		nameField.setEnabled(false);
		searchPanel.add(nameField);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, nameField, 0, SpringLayout.VERTICAL_CENTER, nameBox);
		layout.putConstraint(SpringLayout.WEST, nameField, 25, SpringLayout.EAST, redshiftBox);
		layout.putConstraint(SpringLayout.EAST, nameField, -25, SpringLayout.EAST, searchPanel);
	}
	
	private void initSearchByDistance(SpringLayout extLayout) {
		
		distancePanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		
		JLabel hLabel = new JLabel("h");
		hoursField = new IntTextField();
		hoursField.setEnabled(false);
		hoursField.setPreferredSize(new Dimension(50, 15));
		JLabel minLabel = new JLabel("m");
		minField = new IntTextField();
		minField.setEnabled(false);
		minField.setPreferredSize(new Dimension(50, 15));
		JLabel secLabel = new JLabel("s");
		secondsField = new DoubleTextField();
		secondsField.setEnabled(false);
		secondsField.setPreferredSize(new Dimension(80, 15));
		
		ButtonGroup signGroup = new ButtonGroup();
		plusBox = new JRadioButton("+", true);
		plusBox.setEnabled(false);
		minusBox = new JRadioButton("-", false);
		minusBox.setEnabled(false);
		signGroup.add(plusBox);
		signGroup.add(minusBox);
		
		degreesField = new IntTextField();
		degreesField.setEnabled(false);
		degreesField.setPreferredSize(new Dimension(50, 15));
		JLabel degLabel = new JLabel("°");
		arcminField = new IntTextField();
		arcminField.setEnabled(false);
		arcminField.setPreferredSize(new Dimension(50, 15));
		JLabel arcminLabel = new JLabel("'");
		arcsecField = new DoubleTextField();
		arcsecField.setEnabled(false);
		arcsecField.setPreferredSize(new Dimension(80, 15));
		
		JLabel rangeLabel = new JLabel("range: ");
		rangeField = new DoubleTextField();
		rangeField.setEnabled(false);
		rangeField.setPreferredSize(new Dimension(80, 15));
		JLabel arcsecLabel = new JLabel("\"");
		JLabel dLimitLabel = new JLabel("limit: ");
		dLimitField = new IntTextField();
		dLimitField.setEnabled(false);
		dLimitField.setPreferredSize(new Dimension(80, 15));
		
		searchPanel.add(distancePanel);
		distancePanel.add(hLabel);
		distancePanel.add(hoursField);
		distancePanel.add(minLabel);
		distancePanel.add(minField);
		distancePanel.add(secLabel);
		distancePanel.add(secondsField);
		distancePanel.add(plusBox);
		distancePanel.add(minusBox);
		distancePanel.add(degreesField);
		distancePanel.add(degLabel);
		distancePanel.add(arcminField);
		distancePanel.add(arcminLabel);
		distancePanel.add(arcsecField);
		distancePanel.add(arcsecLabel);
		distancePanel.add(rangeLabel);
		distancePanel.add(rangeField);
		distancePanel.add(dLimitLabel);
		distancePanel.add(dLimitField);
		
		extLayout.putConstraint(SpringLayout.VERTICAL_CENTER, distancePanel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		extLayout.putConstraint(SpringLayout.WEST, distancePanel, 25, SpringLayout.EAST, redshiftBox);
		
		layout.putConstraint(SpringLayout.NORTH, hLabel, 0, SpringLayout.NORTH, distancePanel);
		layout.putConstraint(SpringLayout.WEST, hLabel, 0, SpringLayout.EAST, distancePanel);
		
		layout.putConstraint(SpringLayout.NORTH, hoursField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, hoursField, 5, SpringLayout.EAST, hLabel);
		
		layout.putConstraint(SpringLayout.NORTH, minLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, minLabel, 10, SpringLayout.EAST, hoursField);
		
		layout.putConstraint(SpringLayout.NORTH, minField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, minField, 5, SpringLayout.EAST, minLabel);
		
		layout.putConstraint(SpringLayout.NORTH, secLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, secLabel, 10, SpringLayout.EAST, minField);
		
		layout.putConstraint(SpringLayout.NORTH, secondsField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, secondsField, 5, SpringLayout.EAST, secLabel);
		
		layout.putConstraint(SpringLayout.NORTH, plusBox, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, plusBox, 10, SpringLayout.EAST, secondsField);
		
		layout.putConstraint(SpringLayout.NORTH, minusBox, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, minusBox, 5, SpringLayout.EAST, plusBox);
		
		layout.putConstraint(SpringLayout.NORTH, degreesField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, degreesField, 10, SpringLayout.EAST, minusBox);
		
		layout.putConstraint(SpringLayout.NORTH, degLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, degLabel, 5, SpringLayout.EAST, degreesField);
		
		layout.putConstraint(SpringLayout.NORTH, arcminField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcminField, 10, SpringLayout.EAST, degLabel);
		
		layout.putConstraint(SpringLayout.NORTH, arcminLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcminLabel, 5, SpringLayout.EAST, arcminField);
		
		layout.putConstraint(SpringLayout.NORTH, arcsecField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcsecField, 10, SpringLayout.EAST, arcminLabel);
		
		layout.putConstraint(SpringLayout.NORTH, arcsecLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcsecLabel, 5, SpringLayout.EAST, arcsecField);
		
		layout.putConstraint(SpringLayout.NORTH, rangeLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, rangeLabel, 15, SpringLayout.EAST, arcsecLabel);
		
		layout.putConstraint(SpringLayout.NORTH, rangeField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, rangeField, 5, SpringLayout.EAST, rangeLabel);
		
		layout.putConstraint(SpringLayout.NORTH, dLimitLabel, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, dLimitLabel, 10, SpringLayout.EAST, rangeField);
		
		layout.putConstraint(SpringLayout.NORTH, dLimitField, 0, SpringLayout.NORTH, coordBox);
		layout.putConstraint(SpringLayout.WEST, dLimitField, 5, SpringLayout.EAST, dLimitLabel);
	}
	
	private void initSearchByRedshift(SpringLayout extLayout) {
		
		redshiftPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		
		redshiftField = new DoubleTextField();
		redshiftField.setEnabled(false);
		redshiftField.setPreferredSize(new Dimension(100, 15));
		rsLimitField = new IntTextField();
		rsLimitField.setEnabled(false);
		rsLimitField.setPreferredSize(new Dimension(50, 15));
		
		ButtonGroup highLowGroup = new ButtonGroup();
		rsHigherBox = new JRadioButton(">=", true);
		rsLowerBox = new JRadioButton("<=", false);
		highLowGroup.add(rsHigherBox);
		highLowGroup.add(rsLowerBox);
		rsHigherBox.setEnabled(false);
		rsLowerBox.setEnabled(false);
		JLabel rsValueLabel = new JLabel("value: ");
		JLabel rsLimitLabel = new JLabel("limit: ");
		
		searchPanel.add(redshiftPanel);
		redshiftPanel.add(rsValueLabel);
		redshiftPanel.add(redshiftField);
		redshiftPanel.add(rsHigherBox);
		redshiftPanel.add(rsLowerBox);
		redshiftPanel.add(rsLimitLabel);
		redshiftPanel.add(rsLimitField);
		
		extLayout.putConstraint(SpringLayout.NORTH, searchBtn, 20, SpringLayout.SOUTH, redshiftPanel);
		extLayout.putConstraint(SpringLayout.WEST, searchBtn, 25, SpringLayout.WEST, searchPanel);
		
		extLayout.putConstraint(SpringLayout.NORTH, scrollPane, 20, SpringLayout.SOUTH, searchBtn);
		extLayout.putConstraint(SpringLayout.WEST, scrollPane, 25, SpringLayout.WEST, searchPanel);
		extLayout.putConstraint(SpringLayout.EAST, scrollPane, -25, SpringLayout.EAST, searchPanel);
		
		extLayout.putConstraint(SpringLayout.VERTICAL_CENTER, redshiftPanel, 0, SpringLayout.VERTICAL_CENTER, redshiftBox);
		extLayout.putConstraint(SpringLayout.WEST, redshiftPanel, 25, SpringLayout.EAST, redshiftBox);
		
		layout.putConstraint(SpringLayout.NORTH, rsValueLabel, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, rsValueLabel, 0, SpringLayout.EAST, redshiftPanel);
		
		layout.putConstraint(SpringLayout.NORTH, redshiftField, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, redshiftField, 10, SpringLayout.EAST, rsValueLabel);
		layout.putConstraint(SpringLayout.EAST, redshiftField, 60, SpringLayout.WEST, redshiftField);
		
		layout.putConstraint(SpringLayout.NORTH, rsHigherBox, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, rsHigherBox, 10, SpringLayout.EAST, redshiftField);
		
		layout.putConstraint(SpringLayout.NORTH, rsLowerBox, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, rsLowerBox, 10, SpringLayout.EAST, rsHigherBox);
		
		layout.putConstraint(SpringLayout.NORTH, rsLimitLabel, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, rsLimitLabel, 10, SpringLayout.EAST, rsLowerBox);
		
		layout.putConstraint(SpringLayout.NORTH, rsLimitField, 0, SpringLayout.NORTH, redshiftPanel);
		layout.putConstraint(SpringLayout.WEST, rsLimitField, 10, SpringLayout.EAST, rsLimitLabel);
		layout.putConstraint(SpringLayout.EAST, rsLimitField, 60, SpringLayout.WEST, rsLimitField);
	}
	
	private void initButton(SpringLayout layout) {
		searchBtn = new JButton("Search");
		searchBtn.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				showGalaxy(null);
				populate(null);
				searchBtn.setEnabled(false);
				
				if (nameBox.isSelected()) {
					String name = nameField.getText();
					if (name != null && !"".equals(name))
						GalaxySearchController.instance().searchNames(name);
					else searchBtn.setEnabled(true);		
				}
				
				else if (coordBox.isSelected()) {
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
						Coordinates center = new Coordinates(h, m, s, plusBox.isSelected(), d, arcm, arcs);
						GalaxySearchController.instance().searchInRange(center, r.doubleValue(), l.intValue());
						searchBtn.setEnabled(false);
					}
				}
				
				else if (redshiftBox.isSelected()) {
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
					boolean higherThen = rsHigherBox.isSelected();
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

	@Override
	public void showError(Exception e) {
		// TODO Auto-generated method stub
		
	}
}
