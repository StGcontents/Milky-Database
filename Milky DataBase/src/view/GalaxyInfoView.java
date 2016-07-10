package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.GalaxyInfoController;
import model.Flux;
import model.Galaxy;
import model.Ion;
import model.IonPool;
import model.Galaxy.Luminosity;

public class GalaxyInfoView extends View {

	private static GalaxyInfoView me;
	public static synchronized GalaxyInfoView instance() {
		if (me == null) me = new GalaxyInfoView();
		return me;
	}
	
	private Galaxy galaxy;
	private JPanel panel;
	private JLabel nameLabel, rightAscLabel, declinationLabel, distanceLabel, 
				   redShiftLabel, spectreLabel, lumLabel, metallicityLabel,
				   spinnerLabel, lineLabel, continuousLabel, conRatioLabel, lineRatioLabel,
				   errorLabel;
	private JButton fluxBtn;
	private JSpinner searchSpinner, ratioSpinner;
	private JRadioButton cBox, _3x3Box, _5x5Box, hrBox, lrBox;
	private JCheckBox conRatioBox, lineRatioBox, isConBox;
	private SpinnerListModel model, differentModel;
	
	protected GalaxyInfoView() {
		
	}
		
	@Override
	public Container generateView() {
		if (panel == null) {
			panel = new JPanel();
			SpringLayout layout = new SpringLayout();
			panel.setLayout(layout);
		
			initGalaxyResults(layout);		
			initBasicOptions(layout);
			initRatioOptions(layout);
			initButton(layout);
			initFluxResults(layout);
		}
		else setGalaxy(null);
		
		return panel;
	}
	
	public void setGalaxy(Galaxy galaxy) {
		this.galaxy = galaxy;
		updateInformation();
	}
	
	private void updateInformation() {
		if (galaxy == null) reset();
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
				(galaxy.getDistance() == null ? "/" : galaxy.getDistance().intValue() + " Mpc"));
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
				luminosity += "<br>&#32;&#32;&#32;&#45;&#32;" + ions[i] + " " + lums[i].getValue() + " keV " + (lums[i].isLimit() ? "[limit]" : "");
			}
			if (!valueExists) luminosity += " /";
			lumLabel.setText(luminosity + "</html>");
			
			String metallicity = "Metallicity: ";
			metallicity += galaxy.getMetallicity() == null ? "/" :
				galaxy.getMetallicity() + 
					(galaxy.getMetallicityError() == null ? "" : 
						" [error: " + galaxy.getMetallicityError().intValue() + "]");
			metallicityLabel.setText(metallicity);
			
			lineLabel.setText(null);
			continuousLabel.setText(null);
			conRatioLabel.setText(null);
			lineRatioLabel.setText(null);
			
			panel.setVisible(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public boolean updateFluxes() {
		if (!galaxy.isFilled()) return false;
		
		Ion selected = (Ion) searchSpinner.getValue(), 
			differentIon = (Ion) ratioSpinner.getValue();
		Flux fluxes[] = new Flux[2], ratioFlux = null;
		ListIterator iterator = galaxy.getFluxes().listIterator();
		
		boolean foundOne = false, 
				foundDifferent = false, 
				foundBoth = false,
				isRequested = lineRatioBox.isSelected(),
				isContinuous = isConBox.isSelected();
		
		String aperture;
		if (cBox.isSelected()) aperture = "c";
		else if (_3x3Box.isSelected()) aperture = "3x3";
		else if (_5x5Box.isSelected()) aperture = "5x5";
		else if (hrBox.isSelected()) aperture = "HR";
		else if (lrBox.isSelected()) aperture = "LR";
		else aperture = "HR+LR";
		
		while(iterator.hasNext()) {
			Flux flux = (Flux) iterator.next();
			if (flux.getIon().equals(selected) && aperture.equals(flux.getAperture())) {
				fluxes[flux.isContinuous() ? 1 : 0] = flux;
				if (foundOne && (foundDifferent || isRequested)) 
						break;
				else if (foundOne) foundBoth = true;
				else foundOne = true;
			}
			else if (isRequested
					&& flux.getIon().equals(differentIon) 
					&& aperture.equals(flux.getAperture())
					&& flux.isContinuous() == isContinuous) {
				ratioFlux = flux;
				foundDifferent = true;
				if (foundBoth) break;
			}
		}
		
		updateFluxLabel(lineLabel, fluxes[0], "Line flux: ");
		updateFluxLabel(continuousLabel, fluxes[1], "Continuous flux: ");
		
		if (conRatioBox.isSelected()) 
			updateContinuousRatioLabel(fluxes[0], fluxes[1]);
		else conRatioLabel.setText("Line flux/Continuous flux ratio: not requested");
		
		Ion ion = (Ion) searchSpinner.getValue(), 
				otherIon = (Ion) ratioSpinner.getValue(); 
		String ratioString = ion.toString() + "/" + otherIon.toString() + " flux ratio: ";
		lineRatioLabel.setText(ratioString);
		if (lineRatioBox.isSelected()) 
			updateLineRatioLabel(fluxes[0], ratioFlux);
		else lineRatioLabel.setText(lineRatioLabel.getText() + " not requested");
		
		fluxBtn.setEnabled(true);
		
		return true;
	}
	
	private void updateFluxLabel(JLabel label, Flux flux, String partial) {
		if (flux == null) partial += "/";
		else if (flux.isUpperLimit()) partial += flux.getValue() + " [Upper limit]";
		else partial += flux.getValue() + " [error: " + flux.getError()+"]";
		label.setText(partial);
	}
	
	private void updateContinuousRatioLabel(Flux lineFlux, Flux conFlux) {
		String ratioString = "Line flux/Continuous flux ratio: ";
		if (lineFlux != null && conFlux != null) 
			ratioString += (lineFlux.getValue() / conFlux.getValue());
		else ratioString += "/";
		conRatioLabel.setText(ratioString);
	}
	
	private void updateLineRatioLabel(Flux lineFlux, Flux otherFlux) {
		if (lineFlux != null && otherFlux != null) 
			lineRatioLabel.setText(lineRatioLabel.getText() + (lineFlux.getValue() / otherFlux.getValue()));
		else lineRatioLabel.setText(lineRatioLabel.getText() + "/");
	}
	
	private void initGalaxyResults(SpringLayout layout) {
		nameLabel = new JLabel();
		nameLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(nameLabel);
		layout.putConstraint(SpringLayout.WEST, nameLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, nameLabel, 25, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, nameLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		
		rightAscLabel = new JLabel();
		rightAscLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(rightAscLabel);
		layout.putConstraint(SpringLayout.WEST, rightAscLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, rightAscLabel, 10, SpringLayout.SOUTH, nameLabel);
		layout.putConstraint(SpringLayout.EAST, rightAscLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		
		declinationLabel = new JLabel();
		declinationLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(declinationLabel);
		layout.putConstraint(SpringLayout.WEST, declinationLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, declinationLabel, 10, SpringLayout.SOUTH, rightAscLabel);
		layout.putConstraint(SpringLayout.EAST, declinationLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		
		redShiftLabel = new JLabel();
		redShiftLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(redShiftLabel);
		layout.putConstraint(SpringLayout.WEST, redShiftLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, redShiftLabel, 10, SpringLayout.SOUTH, declinationLabel);
		layout.putConstraint(SpringLayout.EAST, redShiftLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);

		distanceLabel = new JLabel();
		distanceLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(distanceLabel);
		layout.putConstraint(SpringLayout.WEST, distanceLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, distanceLabel, 10, SpringLayout.SOUTH, redShiftLabel);
		layout.putConstraint(SpringLayout.EAST, distanceLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);

		spectreLabel = new JLabel();
		spectreLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(spectreLabel);
		layout.putConstraint(SpringLayout.WEST, spectreLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, spectreLabel, 10, SpringLayout.SOUTH, distanceLabel);
		layout.putConstraint(SpringLayout.EAST, spectreLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		

		lumLabel = new JLabel();
		panel.add(lumLabel);
		layout.putConstraint(SpringLayout.WEST, lumLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, lumLabel, 10, SpringLayout.SOUTH, spectreLabel);
		layout.putConstraint(SpringLayout.EAST, lumLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		

		metallicityLabel = new JLabel();
		metallicityLabel.setPreferredSize(new Dimension(200, 20));
		panel.add(metallicityLabel);
		layout.putConstraint(SpringLayout.WEST, metallicityLabel, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, metallicityLabel, 10, SpringLayout.SOUTH, lumLabel);
		layout.putConstraint(SpringLayout.EAST, metallicityLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
	}
	
	private void initBasicOptions(SpringLayout layout) {
		spinnerLabel = new JLabel("Select line and show fluxes:");
		panel.add(spinnerLabel);
		layout.putConstraint(SpringLayout.WEST, spinnerLabel, -25, SpringLayout.HORIZONTAL_CENTER, panel);
		layout.putConstraint(SpringLayout.NORTH, spinnerLabel, 25, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, spinnerLabel, -25, SpringLayout.EAST, panel);
		
		List<Ion> ions = IonPool.getIonList();
		model = new SpinnerListModel();
		if (!ions.isEmpty())
			model.setList(ions);
		searchSpinner = new JSpinner(model);
		searchSpinner.addChangeListener(new ExclusiveChangeListener((Ion) searchSpinner.getValue()));
		panel.add(searchSpinner);
		layout.putConstraint(SpringLayout.WEST, searchSpinner, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, searchSpinner, 5, SpringLayout.SOUTH, spinnerLabel);
		layout.putConstraint(SpringLayout.EAST, searchSpinner, -25, SpringLayout.EAST, panel);
		
		JLabel apertureLabel = new JLabel("Aperture size/resolution: ");
		ButtonGroup apertureGroup = new ButtonGroup();
		cBox = new JRadioButton("c", true);
		_3x3Box = new JRadioButton("3x3", false);
		_5x5Box = new JRadioButton("5x5", false);
		hrBox = new JRadioButton("HR", false);
		lrBox = new JRadioButton("LR", false);
		JRadioButton hrlrBox = new JRadioButton("HR+LR", false);
		apertureGroup.add(cBox);
		apertureGroup.add(_3x3Box);
		apertureGroup.add(_5x5Box);
		apertureGroup.add(hrBox);
		apertureGroup.add(lrBox);
		apertureGroup.add(hrlrBox);
		
		panel.add(apertureLabel);
		panel.add(cBox);
		panel.add(_3x3Box);
		panel.add(_5x5Box);
		panel.add(hrBox);
		panel.add(lrBox);
		panel.add(hrlrBox);
		
		layout.putConstraint(SpringLayout.WEST, apertureLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, apertureLabel, 10, SpringLayout.SOUTH, searchSpinner);
		layout.putConstraint(SpringLayout.WEST, cBox, 0, SpringLayout.WEST, apertureLabel);
		layout.putConstraint(SpringLayout.NORTH, cBox, 5, SpringLayout.SOUTH, apertureLabel);
		layout.putConstraint(SpringLayout.WEST, _3x3Box, 10, SpringLayout.EAST, cBox);
		layout.putConstraint(SpringLayout.NORTH, _3x3Box, 0, SpringLayout.NORTH, cBox);
		layout.putConstraint(SpringLayout.WEST, _5x5Box, 10, SpringLayout.EAST, _3x3Box);
		layout.putConstraint(SpringLayout.NORTH, _5x5Box, 0, SpringLayout.NORTH, cBox);
		layout.putConstraint(SpringLayout.WEST, hrBox, 10, SpringLayout.EAST, _5x5Box);
		layout.putConstraint(SpringLayout.NORTH, hrBox, 0, SpringLayout.NORTH, cBox);
		layout.putConstraint(SpringLayout.WEST, lrBox, 10, SpringLayout.EAST, hrBox);
		layout.putConstraint(SpringLayout.NORTH, lrBox, 0, SpringLayout.NORTH, cBox);
		layout.putConstraint(SpringLayout.WEST, hrlrBox, 10, SpringLayout.EAST, lrBox);
		layout.putConstraint(SpringLayout.NORTH, hrlrBox, 0, SpringLayout.NORTH, cBox);	
	}
	
	private void initRatioOptions(SpringLayout layout) {
		conRatioBox = new JCheckBox("Show line flux / continuous flux ratio");
		panel.add(conRatioBox);
		layout.putConstraint(SpringLayout.WEST, conRatioBox, 0, SpringLayout.WEST, cBox);
		layout.putConstraint(SpringLayout.NORTH, conRatioBox, 10, SpringLayout.SOUTH, cBox);
		
		lineRatioBox = new JCheckBox("Show line flux / line flux ratio (pick a different line)");
		lineRatioBox.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override
			public void mouseClicked(MouseEvent e) {
				ratioSpinner.setEnabled(lineRatioBox.isSelected());
				isConBox.setEnabled(lineRatioBox.isSelected());
			}
		});
		panel.add(lineRatioBox);
		layout.putConstraint(SpringLayout.WEST, lineRatioBox, 0, SpringLayout.WEST, conRatioBox);
		layout.putConstraint(SpringLayout.NORTH, lineRatioBox, 10, SpringLayout.SOUTH, conRatioBox);
		
		differentModel = new SpinnerListModel();
		List<Ion> ions = IonPool.getIonList();
		if (!ions.isEmpty()) {
			ions.remove(model.getValue());
			differentModel.setList(ions);
		}
		ratioSpinner = new JSpinner(differentModel);
		ratioSpinner.setEnabled(false);
		panel.add(ratioSpinner);
		layout.putConstraint(SpringLayout.WEST, ratioSpinner, 10, SpringLayout.EAST, lineRatioBox);
		layout.putConstraint(SpringLayout.NORTH, ratioSpinner, 0, SpringLayout.NORTH, lineRatioBox);
		layout.putConstraint(SpringLayout.EAST, ratioSpinner, -25, SpringLayout.EAST, panel);
		
		isConBox = new JCheckBox("Select for continuous flux ratio", false);
		isConBox.setEnabled(false);
		panel.add(isConBox);
		layout.putConstraint(SpringLayout.WEST, isConBox, 10, SpringLayout.WEST, lineRatioBox);
		layout.putConstraint(SpringLayout.NORTH, isConBox, 5, SpringLayout.SOUTH, lineRatioBox);
	}
	
	private void initButton(SpringLayout layout) {
		fluxBtn = new JButton("Search");
		fluxBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fluxBtn.setEnabled(false);
				errorLabel.setText(null);
				if (!updateFluxes()) {
					GalaxyInfoController.instance().retrieveFluxes(galaxy);
					updateFluxes();
				}
			}
		});
		panel.add(fluxBtn);
		layout.putConstraint(SpringLayout.WEST, fluxBtn, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, fluxBtn, 10, SpringLayout.SOUTH, isConBox);
		
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		panel.add(errorLabel);
		layout.putConstraint(SpringLayout.WEST, errorLabel, 15, SpringLayout.EAST, fluxBtn);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, errorLabel, 0, SpringLayout.VERTICAL_CENTER, fluxBtn);
	}
	
	private void initFluxResults(SpringLayout layout) {
		lineLabel = new JLabel();
		panel.add(lineLabel);
		layout.putConstraint(SpringLayout.WEST, lineLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, lineLabel, 10, SpringLayout.SOUTH, fluxBtn);
		layout.putConstraint(SpringLayout.EAST, lineLabel, -25, SpringLayout.EAST, panel);
		
		continuousLabel = new JLabel();
		panel.add(continuousLabel);
		layout.putConstraint(SpringLayout.WEST, continuousLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, continuousLabel, 5, SpringLayout.SOUTH, lineLabel);
		layout.putConstraint(SpringLayout.EAST, continuousLabel, -25, SpringLayout.EAST, panel);
		
		conRatioLabel = new JLabel();
		panel.add(conRatioLabel);
		layout.putConstraint(SpringLayout.WEST, conRatioLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, conRatioLabel, 5, SpringLayout.SOUTH, continuousLabel);
		layout.putConstraint(SpringLayout.EAST, conRatioLabel, -25, SpringLayout.EAST, panel);
		
		lineRatioLabel = new JLabel();
		panel.add(lineRatioLabel);
		layout.putConstraint(SpringLayout.WEST, lineRatioLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, lineRatioLabel, 5, SpringLayout.SOUTH, conRatioLabel);
		layout.putConstraint(SpringLayout.EAST, lineRatioLabel, -25, SpringLayout.EAST, panel);
	}
	
	
	private class ExclusiveChangeListener implements ChangeListener {
		
		private Ion prev;
		
		protected ExclusiveChangeListener(Ion startIon) {
			prev = startIon;
		}
			
		@SuppressWarnings("unchecked")
		@Override
		public void stateChanged(ChangeEvent e) {
			Ion current = (Ion) searchSpinner.getValue();
			if (prev !=null) 
				((List<Ion>) differentModel.getList()).add(prev);
			differentModel.getList().remove(current); 
			try { ratioSpinner.commitEdit(); } 
			catch (ParseException e1) { e1.printStackTrace(); }
			prev = current;
		}
	}

	@Override
	public void showError(Exception e) {
		if (e == null)
			errorLabel.setText(null);
		else if (e instanceof SQLException)
			errorLabel.setText("Could not retrieve values.");
		else 
			errorLabel.setText("An error occurred.");
		
		fluxBtn.setEnabled(true);
	}

	@Override
	protected void reset() {
		errorLabel.setText(null);
		panel.setVisible(false);
	}
}
