package view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpringLayout;

import controller.HeavyTaskController;
import model.Priviledge;
import model.Statistics;
import pattern.Observer;

public class HeavyTaskView {
	
	private static HeavyTaskView me;
	public static synchronized HeavyTaskView instance() {
		int currentPriviledgeLevel = Priviledge.instance().retrieveState();
		if (me == null || me.lastPriviledgeLevel != currentPriviledgeLevel) {
			me = new HeavyTaskView();
			me.lastPriviledgeLevel = currentPriviledgeLevel;
		}
		return me;
	}
	
	private int lastPriviledgeLevel;
	private Panel taskPanel;
	private Checkbox allBox, cBox, _3x3Box, _5x5Box, s1Box;
	private CheckboxGroup spectralGroup;
	private Label avgLabel, stddevLabel, medLabel;
	private StatisticsObserver observer;
	
	public Observer<Statistics> getObserver() { return observer; }

	public HeavyTaskView() {
		observer = new StatisticsObserver(this);
	}
	
	public Panel generatePanel() {
		if (taskPanel == null) {
			taskPanel = new Panel();
			SpringLayout layout = new SpringLayout();
			taskPanel.setLayout(layout);
			
			CheckboxGroup apertureGroup = new CheckboxGroup();
			allBox = new Checkbox("All", apertureGroup, true);
			cBox = new Checkbox("c", apertureGroup, false);
			_3x3Box = new Checkbox("3x3", apertureGroup, false);
			_5x5Box = new Checkbox("5x5", apertureGroup, false);
			
			spectralGroup = new CheckboxGroup();
			s1Box = new Checkbox("S1", spectralGroup, true);
			Checkbox s1_5Box = new Checkbox("S1.5", spectralGroup, false);
			Checkbox s1_8Box = new Checkbox("S1.8", spectralGroup, false);
			Checkbox s1_9Box = new Checkbox("S1.9", spectralGroup, false);
			Checkbox s1hBox = new Checkbox("S1h", spectralGroup, false);
			Checkbox s2Box = new Checkbox("S2", spectralGroup, false);
			Checkbox linBox = new Checkbox("LIN", spectralGroup, false);
			Checkbox dwarfBox = new Checkbox("DWARF", spectralGroup, false);
			
			Button taskBtn = new Button("Calculate");
			taskBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					avgLabel.setText(null);
					stddevLabel.setText(null);
					medLabel.setText(null);
					
					String apertureSize = null;
					if (cBox.getState()) apertureSize = "c";
					if (_3x3Box.getState()) apertureSize = "3x3";
					if (_5x5Box.getState()) apertureSize = "5x5";
					HeavyTaskController.instance().calculate(spectralGroup.getSelectedCheckbox().getLabel(), apertureSize);
				}
			});
			
			avgLabel = new Label();
			avgLabel.setPreferredSize(new Dimension(200, 20));
			stddevLabel = new Label();
			stddevLabel.setPreferredSize(new Dimension(200, 20));
			medLabel = new Label();
			medLabel.setPreferredSize(new Dimension(200, 20));
			
			taskPanel.add(s1Box);
			taskPanel.add(s1_5Box);
			taskPanel.add(s1_8Box);
			taskPanel.add(s1_9Box);
			taskPanel.add(s1hBox);
			taskPanel.add(s2Box);
			taskPanel.add(linBox);
			taskPanel.add(dwarfBox);
			
			taskPanel.add(allBox);
			taskPanel.add(cBox);
			taskPanel.add(_3x3Box);
			taskPanel.add(_5x5Box);
			
			taskPanel.add(taskBtn);
			
			taskPanel.add(avgLabel);
			taskPanel.add(stddevLabel);
			taskPanel.add(medLabel);
			
			layout.putConstraint(SpringLayout.NORTH, s1Box, 25, SpringLayout.NORTH, taskPanel);
			layout.putConstraint(SpringLayout.WEST, s1Box, 25, SpringLayout.WEST, taskPanel);
			
			layout.putConstraint(SpringLayout.NORTH, s1_5Box, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, s1_5Box, 10, SpringLayout.EAST, s1Box);
			
			layout.putConstraint(SpringLayout.NORTH, s1_8Box, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, s1_8Box, 10, SpringLayout.EAST, s1_5Box);
			
			layout.putConstraint(SpringLayout.NORTH, s1_9Box, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, s1_9Box, 10, SpringLayout.EAST, s1_8Box);
			
			layout.putConstraint(SpringLayout.NORTH, s1hBox, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, s1hBox, 10, SpringLayout.EAST, s1_9Box);
			
			layout.putConstraint(SpringLayout.NORTH, s2Box, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, s2Box, 10, SpringLayout.EAST, s1hBox);
			
			layout.putConstraint(SpringLayout.NORTH, linBox, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, linBox, 10, SpringLayout.EAST, s2Box);
			
			layout.putConstraint(SpringLayout.NORTH, dwarfBox, 0, SpringLayout.NORTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, dwarfBox, 10, SpringLayout.EAST, linBox);
			
			layout.putConstraint(SpringLayout.NORTH, allBox, 10, SpringLayout.SOUTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, allBox, 0, SpringLayout.WEST, s1Box);
			
			layout.putConstraint(SpringLayout.NORTH, cBox, 0, SpringLayout.NORTH, allBox);
			layout.putConstraint(SpringLayout.WEST, cBox, 10, SpringLayout.EAST, allBox);
			
			layout.putConstraint(SpringLayout.NORTH, _3x3Box, 0, SpringLayout.NORTH, allBox);
			layout.putConstraint(SpringLayout.WEST, _3x3Box, 10, SpringLayout.EAST, cBox);
			
			layout.putConstraint(SpringLayout.NORTH, _5x5Box, 0, SpringLayout.NORTH, allBox);
			layout.putConstraint(SpringLayout.WEST, _5x5Box, 10, SpringLayout.EAST, _3x3Box);
			
			layout.putConstraint(SpringLayout.NORTH, taskBtn, 10, SpringLayout.SOUTH, allBox);
			layout.putConstraint(SpringLayout.WEST, taskBtn, 0, SpringLayout.WEST, allBox);
			
			layout.putConstraint(SpringLayout.NORTH, avgLabel, 25, SpringLayout.SOUTH, taskBtn);
			layout.putConstraint(SpringLayout.WEST, avgLabel, 0, SpringLayout.WEST, taskBtn);
			
			layout.putConstraint(SpringLayout.NORTH, stddevLabel, 10, SpringLayout.SOUTH, avgLabel);
			layout.putConstraint(SpringLayout.WEST, stddevLabel, 0, SpringLayout.WEST, avgLabel);
			
			layout.putConstraint(SpringLayout.NORTH, medLabel, 10, SpringLayout.SOUTH, stddevLabel);
			layout.putConstraint(SpringLayout.WEST, medLabel, 0, SpringLayout.WEST, avgLabel);
		}
		else reset();
		
		return taskPanel;
	}
	
	private void reset() {
		s1Box.setState(true);
		allBox.setState(true);
		
		avgLabel.setText(null);
		stddevLabel.setText(null);
		medLabel.setText(null);
	}
	
	private void update(Statistics stats) {
		avgLabel.setText("Average: " + stats.getAvearge());
		stddevLabel.setText("Standard deviation: " + stats.getStandardDeviation());
		medLabel.setText("Median value: " + stats.getMedian());
	}
	
	protected class StatisticsObserver extends Observer<Statistics> {
		private HeavyTaskView adaptee;
		protected StatisticsObserver(HeavyTaskView adaptee) {
			this.adaptee = adaptee;
		}
		
		@Override
		public void stateChanged() {
			adaptee.update(subject.retrieveState());
		}
	}
}
