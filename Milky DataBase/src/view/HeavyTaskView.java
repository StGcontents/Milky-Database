package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import controller.HeavyTaskController;
import model.Statistics;

public class HeavyTaskView extends View {
	
	private static HeavyTaskView me;
	private HeavyTaskView() { }
	public static synchronized HeavyTaskView instance() {
		if (me == null) 
			me = new HeavyTaskView();
		return me;
	}
	
	private JPanel taskPanel;
	private JRadioButton allBox, cBox, _3x3Box, _5x5Box, s1Box;
	private SpectreGroup spectralGroup;
	private JLabel avgLabel, stddevLabel, medLabel, madLabel;
	private JButton taskBtn;
	
	@Override
	public JPanel generateView() {
		if (taskPanel == null) {
			taskPanel = new JPanel();
			SpringLayout layout = new SpringLayout();
			taskPanel.setLayout(layout);
			
			JLabel apertureLabel = new JLabel("Choose aperture size:");
			ButtonGroup apertureGroup = new ButtonGroup();
			allBox = new JRadioButton("All", true);
			cBox = new JRadioButton("c", false);
			_3x3Box = new JRadioButton("3x3", false);
			_5x5Box = new JRadioButton("5x5", false);
			apertureGroup.add(allBox);
			apertureGroup.add(cBox);
			apertureGroup.add(_3x3Box);
			apertureGroup.add(_5x5Box);
			
			JLabel spectralLabel = new JLabel("Choose spectral group:"); 
			spectralGroup = new SpectreGroup();
			s1Box = new JRadioButton("S1", true);
			JRadioButton s1_5Box = new JRadioButton("S1.5", false);
			JRadioButton s1_8Box = new JRadioButton("S1.8", false);
			JRadioButton s1_9Box = new JRadioButton("S1.9", false);
			JRadioButton s1hBox = new JRadioButton("S1h", false);
			JRadioButton s2Box = new JRadioButton("S2", false);
			JRadioButton linBox = new JRadioButton("LIN", false);
			JRadioButton dwarfBox = new JRadioButton("DWARF", false);
			spectralGroup.add(s1Box);
			spectralGroup.add(s1_5Box);
			spectralGroup.add(s1_8Box);
			spectralGroup.add(s1_9Box);
			spectralGroup.add(s1hBox);
			spectralGroup.add(s2Box);
			spectralGroup.add(linBox);
			spectralGroup.add(dwarfBox);
			
			taskBtn = new JButton("Calculate");
			taskBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					avgLabel.setText(null);
					stddevLabel.setText(null);
					medLabel.setText(null);
					madLabel.setText(null);
					
					taskBtn.setEnabled(false);
					
					String apertureSize = null;
					if (cBox.isSelected()) apertureSize = "c";
					if (_3x3Box.isSelected()) apertureSize = "3x3";
					if (_5x5Box.isSelected()) apertureSize = "5x5";
					HeavyTaskController.instance().calculate(spectralGroup.getSelectedText(), apertureSize);
				}
			});
			
			avgLabel = new JLabel();
			avgLabel.setPreferredSize(new Dimension(300, 20));
			stddevLabel = new JLabel();
			stddevLabel.setPreferredSize(new Dimension(300, 20));
			medLabel = new JLabel();
			medLabel.setPreferredSize(new Dimension(300, 20));
			madLabel = new JLabel();
			madLabel.setPreferredSize(new Dimension(300, 20));
			
			taskPanel.add(spectralLabel);
			taskPanel.add(s1Box);
			taskPanel.add(s1_5Box);
			taskPanel.add(s1_8Box);
			taskPanel.add(s1_9Box);
			taskPanel.add(s1hBox);
			taskPanel.add(s2Box);
			taskPanel.add(linBox);
			taskPanel.add(dwarfBox);
			
			taskPanel.add(apertureLabel);
			taskPanel.add(allBox);
			taskPanel.add(cBox);
			taskPanel.add(_3x3Box);
			taskPanel.add(_5x5Box);
			
			taskPanel.add(taskBtn);
			
			taskPanel.add(avgLabel);
			taskPanel.add(stddevLabel);
			taskPanel.add(medLabel);
			taskPanel.add(madLabel);
			
			layout.putConstraint(SpringLayout.NORTH, spectralLabel, 25, SpringLayout.NORTH, taskPanel);
			layout.putConstraint(SpringLayout.WEST, spectralLabel, 25, SpringLayout.WEST, taskPanel);
			
			layout.putConstraint(SpringLayout.NORTH, s1Box, 2, SpringLayout.SOUTH, spectralLabel);
			layout.putConstraint(SpringLayout.WEST, s1Box, 0, SpringLayout.WEST, spectralLabel);
			
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
			
			layout.putConstraint(SpringLayout.NORTH, apertureLabel, 15, SpringLayout.SOUTH, s1Box);
			layout.putConstraint(SpringLayout.WEST, apertureLabel, 0, SpringLayout.WEST, s1Box);
			
			layout.putConstraint(SpringLayout.NORTH, allBox, 2, SpringLayout.SOUTH, apertureLabel);
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
			
			layout.putConstraint(SpringLayout.NORTH, madLabel, 10, SpringLayout.SOUTH, medLabel);
			layout.putConstraint(SpringLayout.WEST, madLabel, 0, SpringLayout.WEST, avgLabel);
		}
		else reset();
		
		return taskPanel;
	}
	
	@Override
	protected void reset() {
		s1Box.setSelected(true);
		allBox.setSelected(true);
		
		avgLabel.setText(null);
		avgLabel.setForeground(stddevLabel.getForeground());
		stddevLabel.setText(null);
		medLabel.setText(null);
		madLabel.setText(null);
	}
	
	public void update(Statistics stats) {
		if (stats == null) 
			avgLabel.setText("No values found");
		else {
			avgLabel.setText("Average: " + stats.getAvearge());
			stddevLabel.setText("Standard deviation: " + stats.getStandardDeviation());
			medLabel.setText("Median value: " + stats.getMedian());
			madLabel.setText("Median absolute deviation: " + stats.getMedianAbsoluteDev());
		}
		
		taskBtn.setEnabled(true);
	}
	
	@SuppressWarnings("serial")
	private class SpectreGroup extends ButtonGroup {
		private List<AbstractButton> buttons = new ArrayList<>();

		@Override
		public void add(AbstractButton b) {
			super.add(b);
			buttons.add(b);
		}
		
		public String getSelectedText() {
			for (AbstractButton b : buttons) 
				if (b.isSelected())
					return b.getText();
			return null;
		}
	}

	@Override
	public void showError(Exception e) {
		avgLabel.setForeground(Color.RED);
		if (e instanceof SQLException)
			avgLabel.setText("Cannot reach server.");
		else 
			avgLabel.setText("An error occurred.");
	}
	
	@Override
	public boolean isCurrentlyShown() {
		return taskPanel != null && taskPanel.isVisible();
	}
}
