package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
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
import javax.swing.SwingUtilities;

import controller.GalaxySearchController;
import model.AdaptableValue;
import model.Galaxy.Coordinates;
import model.Priviledge;

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
	private JPanel searchPanel, resultPanel;
	private JRadioButton nameBox, coordBox, redshiftBox, rsHigherBox, rsLowerBox, plusBox, minusBox;
	private JTextArea nameField;
	private DoubleTextField redshiftField, secondsField, arcsecField, rangeField;
	private IntTextField rsLimitField, dLimitField, hoursField, minField, degreesField, arcminField;
	private JList<AdaptableValue> results;
	private JScrollPane scrollPane;
	private JButton searchBtn;
	private JLabel searchFailedLabel;
	
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
			initResultPanel();
				
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
		resultPanel.setVisible(false);	
	}
	
	public void populate(List<AdaptableValue> values) {
		final List<AdaptableValue> arg0 = values; 
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	results.clearSelection();
		    	DefaultListModel<AdaptableValue> model = (DefaultListModel<AdaptableValue>) results.getModel();
		        model.clear();
		        if (arg0 != null) 
					for (AdaptableValue value : arg0) 
						model.addElement(value);
		        searchBtn.setEnabled(true);
		    }
		});
	}
	
	private void initResultPanel() { 
		resultPanel = (JPanel) GalaxyInfoView.instance().generateView();
		searchPanel.add(resultPanel);
		SpringLayout layout = (SpringLayout) searchPanel.getLayout();
		layout.putConstraint(SpringLayout.NORTH, resultPanel, 0, SpringLayout.SOUTH, scrollPane);
		layout.putConstraint(SpringLayout.WEST, resultPanel, 25, SpringLayout.WEST, searchPanel);
		layout.putConstraint(SpringLayout.EAST, resultPanel, -25, SpringLayout.EAST, searchPanel);
		layout.putConstraint(SpringLayout.SOUTH, resultPanel, -25, SpringLayout.SOUTH, searchPanel);
	}
	
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
	
	private void initSearchByDistance(SpringLayout layout) {
		
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
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, hLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, hLabel, 25, SpringLayout.EAST, redshiftBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, hoursField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, hoursField, 5, SpringLayout.EAST, hLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, minLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, minLabel, 10, SpringLayout.EAST, hoursField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, minField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, minField, 5, SpringLayout.EAST, minLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, secLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, secLabel, 10, SpringLayout.EAST, minField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, secondsField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, secondsField, 5, SpringLayout.EAST, secLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, plusBox, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, plusBox, 10, SpringLayout.EAST, secondsField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, minusBox, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, minusBox, 5, SpringLayout.EAST, plusBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, degreesField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, degreesField, 10, SpringLayout.EAST, minusBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, degLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, degLabel, 5, SpringLayout.EAST, degreesField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, arcminField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcminField, 10, SpringLayout.EAST, degLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, arcminLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcminLabel, 5, SpringLayout.EAST, arcminField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, arcsecField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcsecField, 10, SpringLayout.EAST, arcminLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, arcsecLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, arcsecLabel, 5, SpringLayout.EAST, arcsecField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rangeLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, rangeLabel, 15, SpringLayout.EAST, arcsecLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rangeField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, rangeField, 5, SpringLayout.EAST, rangeLabel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, dLimitLabel, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, dLimitLabel, 10, SpringLayout.EAST, rangeField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, dLimitField, 0, SpringLayout.VERTICAL_CENTER, coordBox);
		layout.putConstraint(SpringLayout.WEST, dLimitField, 5, SpringLayout.EAST, dLimitLabel);
	}
	
	private void initSearchByRedshift(SpringLayout layout) {
		
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
		
		searchPanel.add(rsValueLabel);
		searchPanel.add(redshiftField);
		searchPanel.add(rsHigherBox);
		searchPanel.add(rsLowerBox);
		searchPanel.add(rsLimitLabel);
		searchPanel.add(rsLimitField);
		
		layout.putConstraint(SpringLayout.NORTH, searchBtn, 20, SpringLayout.SOUTH, redshiftBox);
		layout.putConstraint(SpringLayout.WEST, searchBtn, 25, SpringLayout.WEST, searchPanel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, searchFailedLabel, 0, SpringLayout.VERTICAL_CENTER, searchBtn);
		layout.putConstraint(SpringLayout.WEST, searchFailedLabel, 15, SpringLayout.EAST, searchBtn);
		
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 20, SpringLayout.SOUTH, searchBtn);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 25, SpringLayout.WEST, searchPanel);
		layout.putConstraint(SpringLayout.EAST, scrollPane, -25, SpringLayout.EAST, searchPanel);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rsValueLabel, 0, SpringLayout.VERTICAL_CENTER, redshiftBox);
		layout.putConstraint(SpringLayout.WEST, rsValueLabel, 25, SpringLayout.EAST, redshiftBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, redshiftField, 0, SpringLayout.VERTICAL_CENTER, rsValueLabel);
		layout.putConstraint(SpringLayout.WEST, redshiftField, 10, SpringLayout.EAST, rsValueLabel);
		layout.putConstraint(SpringLayout.EAST, redshiftField, 60, SpringLayout.WEST, redshiftField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rsHigherBox, 0, SpringLayout.VERTICAL_CENTER, rsValueLabel);
		layout.putConstraint(SpringLayout.WEST, rsHigherBox, 10, SpringLayout.EAST, redshiftField);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rsLowerBox, 0, SpringLayout.VERTICAL_CENTER, rsValueLabel);
		layout.putConstraint(SpringLayout.WEST, rsLowerBox, 10, SpringLayout.EAST, rsHigherBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rsLimitLabel, 0, SpringLayout.VERTICAL_CENTER, rsValueLabel);
		layout.putConstraint(SpringLayout.WEST, rsLimitLabel, 10, SpringLayout.EAST, rsLowerBox);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, rsLimitField, 0, SpringLayout.VERTICAL_CENTER, rsValueLabel);
		layout.putConstraint(SpringLayout.WEST, rsLimitField, 10, SpringLayout.EAST, rsLimitLabel);
		layout.putConstraint(SpringLayout.EAST, rsLimitField, 60, SpringLayout.WEST, rsLimitField);
	}
	
	private void initButton(SpringLayout layout) {
		searchBtn = new JButton("Search");
		searchBtn.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				resultPanel.setVisible(false);
				populate(null);
				searchBtn.setEnabled(false);
				searchFailedLabel.setText(null);
				
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
		
		searchFailedLabel = new JLabel();
		searchFailedLabel.setForeground(Color.RED);
		searchPanel.add(searchFailedLabel);
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
		if (e instanceof SQLException)
			searchFailedLabel.setText("Something went wrong with your search.");
		else if (e != null) 
			searchFailedLabel.setText("An error occurred.");
		
		searchBtn.setEnabled(true);
	}
}
