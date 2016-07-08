package view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.FluxSearchController;
import model.Flux;
import model.Galaxy;
import model.Ion;
import model.IonPool;
import model.Galaxy.Luminosity;

@SuppressWarnings("serial")
public class GalaxyPanel extends Panel {

	private Galaxy galaxy;
	private JLabel nameLabel, rightAscLabel, declinationLabel, distanceLabel, 
				   redShiftLabel, spectreLabel, lumLabel, metallicityLabel,
				   spinnerLabel, lineLabel, continuousLabel, conRatioLabel, lineRatioLabel;
	private Button fluxBtn;
	private JSpinner searchSpinner, ratioSpinner;
	private Checkbox cBox, _3x3Box, _5x5Box, hrBox, lrBox, conRatioBox, lineRatioBox, isConBox;
	private SpinnerListModel model, differentModel;
	
	protected GalaxyPanel(Galaxy galaxy) {
		setup();
		setGalaxy(galaxy);
	}
		
	private void setup() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		initGalaxyResults(layout);		
		initBasicOptions(layout);
		initRatioOptions(layout);
		initButton(layout);
		initFluxResults(layout);		
	}
	
	public void setGalaxy(Galaxy galaxy) {
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
			
			lineLabel.setText(null);
			continuousLabel.setText(null);
			conRatioLabel.setText(null);
			lineRatioLabel.setText(null);
			
			setVisible(true);
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
				isRequested = lineRatioBox.getState(),
				isContinuous = isConBox.getState();
		
		String aperture;
		if (cBox.getState()) aperture = "c";
		else if (_3x3Box.getState()) aperture = "3x3";
		else if (_5x5Box.getState()) aperture = "5x5";
		else if (hrBox.getState()) aperture = "HR";
		else if (lrBox.getState()) aperture = "LR";
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
		
		if (conRatioBox.getState()) 
			updateContinuousRatioLabel(fluxes[0], fluxes[1]);
		else conRatioLabel.setText("Line flux/Continuous flux ratio: not requested");
		
		Ion ion = (Ion) searchSpinner.getValue(), 
				otherIon = (Ion) ratioSpinner.getValue(); 
		String ratioString = ion.toString() + "/" + otherIon.toString() + " flux ratio: ";
		lineRatioLabel.setText(ratioString);
		if (lineRatioBox.getState()) 
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
		this.add(nameLabel);
		layout.putConstraint(SpringLayout.WEST, nameLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, nameLabel, 25, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, nameLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		
		rightAscLabel = new JLabel();
		rightAscLabel.setPreferredSize(new Dimension(200, 20));
		this.add(rightAscLabel);
		layout.putConstraint(SpringLayout.WEST, rightAscLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, rightAscLabel, 10, SpringLayout.SOUTH, nameLabel);
		layout.putConstraint(SpringLayout.EAST, rightAscLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		
		declinationLabel = new JLabel();
		declinationLabel.setPreferredSize(new Dimension(200, 20));
		this.add(declinationLabel);
		layout.putConstraint(SpringLayout.WEST, declinationLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, declinationLabel, 10, SpringLayout.SOUTH, rightAscLabel);
		layout.putConstraint(SpringLayout.EAST, declinationLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		
		redShiftLabel = new JLabel();
		redShiftLabel.setPreferredSize(new Dimension(200, 20));
		this.add(redShiftLabel);
		layout.putConstraint(SpringLayout.WEST, redShiftLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, redShiftLabel, 10, SpringLayout.SOUTH, declinationLabel);
		layout.putConstraint(SpringLayout.EAST, redShiftLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);

		distanceLabel = new JLabel();
		distanceLabel.setPreferredSize(new Dimension(200, 20));
		this.add(distanceLabel);
		layout.putConstraint(SpringLayout.WEST, distanceLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, distanceLabel, 10, SpringLayout.SOUTH, redShiftLabel);
		layout.putConstraint(SpringLayout.EAST, distanceLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);

		spectreLabel = new JLabel();
		spectreLabel.setPreferredSize(new Dimension(200, 20));
		this.add(spectreLabel);
		layout.putConstraint(SpringLayout.WEST, spectreLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, spectreLabel, 10, SpringLayout.SOUTH, distanceLabel);
		layout.putConstraint(SpringLayout.EAST, spectreLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		

		lumLabel = new JLabel();
		this.add(lumLabel);
		layout.putConstraint(SpringLayout.WEST, lumLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, lumLabel, 10, SpringLayout.SOUTH, spectreLabel);
		layout.putConstraint(SpringLayout.EAST, lumLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		

		metallicityLabel = new JLabel();
		metallicityLabel.setPreferredSize(new Dimension(200, 20));
		this.add(metallicityLabel);
		layout.putConstraint(SpringLayout.WEST, metallicityLabel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, metallicityLabel, 10, SpringLayout.SOUTH, lumLabel);
		layout.putConstraint(SpringLayout.EAST, metallicityLabel, 25, SpringLayout.HORIZONTAL_CENTER, this);
	}
	
	private void initBasicOptions(SpringLayout layout) {
		spinnerLabel = new JLabel("Select line and show fluxes:");
		this.add(spinnerLabel);
		layout.putConstraint(SpringLayout.WEST, spinnerLabel, -25, SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, spinnerLabel, 25, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, spinnerLabel, -25, SpringLayout.EAST, this);
		
		model = new SpinnerListModel(IonPool.getIonList());
		searchSpinner = new JSpinner(model);
		searchSpinner.addChangeListener(new ExclusiveChangeListener((Ion) searchSpinner.getValue()));
		this.add(searchSpinner);
		layout.putConstraint(SpringLayout.WEST, searchSpinner, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, searchSpinner, 5, SpringLayout.SOUTH, spinnerLabel);
		layout.putConstraint(SpringLayout.EAST, searchSpinner, -25, SpringLayout.EAST, this);
		
		JLabel apertureLabel = new JLabel("Aperture size/resolution: ");
		CheckboxGroup apertureGroup = new CheckboxGroup();
		cBox = new Checkbox("c", apertureGroup, true);
		_3x3Box = new Checkbox("3x3", apertureGroup, false);
		_5x5Box = new Checkbox("5x5", apertureGroup, false);
		hrBox = new Checkbox("HR", apertureGroup, false);
		lrBox = new Checkbox("LR", apertureGroup, false);
		Checkbox hrlrBox = new Checkbox("HR+LR", apertureGroup, false);
		this.add(apertureLabel);
		this.add(cBox);
		this.add(_3x3Box);
		this.add(_5x5Box);
		this.add(hrBox);
		this.add(lrBox);
		this.add(hrlrBox);
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
		conRatioBox = new Checkbox("Show line flux / continuous flux ratio");
		this.add(conRatioBox);
		layout.putConstraint(SpringLayout.WEST, conRatioBox, 0, SpringLayout.WEST, cBox);
		layout.putConstraint(SpringLayout.NORTH, conRatioBox, 10, SpringLayout.SOUTH, cBox);
		
		lineRatioBox = new Checkbox("Show line flux / line flux ratio (pick a different line)");
		lineRatioBox.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override
			public void mouseClicked(MouseEvent e) {
				ratioSpinner.setEnabled(lineRatioBox.getState());
				isConBox.setEnabled(lineRatioBox.getState());
			}
		});
		this.add(lineRatioBox);
		layout.putConstraint(SpringLayout.WEST, lineRatioBox, 0, SpringLayout.WEST, conRatioBox);
		layout.putConstraint(SpringLayout.NORTH, lineRatioBox, 10, SpringLayout.SOUTH, conRatioBox);
		
		differentModel = new SpinnerListModel(IonPool.getIonList());
		List<Ion> ions = IonPool.getIonList();
		ions.remove(model.getValue());
		differentModel = new SpinnerListModel(ions);
		ratioSpinner = new JSpinner(differentModel);
		ratioSpinner.setEnabled(false);
		this.add(ratioSpinner);
		layout.putConstraint(SpringLayout.WEST, ratioSpinner, 10, SpringLayout.EAST, lineRatioBox);
		layout.putConstraint(SpringLayout.NORTH, ratioSpinner, 0, SpringLayout.NORTH, lineRatioBox);
		layout.putConstraint(SpringLayout.EAST, ratioSpinner, -25, SpringLayout.EAST, this);
		
		isConBox = new Checkbox("Select for continuous flux ratio", false);
		isConBox.setEnabled(false);
		this.add(isConBox);
		layout.putConstraint(SpringLayout.WEST, isConBox, 10, SpringLayout.WEST, lineRatioBox);
		layout.putConstraint(SpringLayout.NORTH, isConBox, 5, SpringLayout.SOUTH, lineRatioBox);
	}
	
	private void initButton(SpringLayout layout) {
		fluxBtn = new Button("Search");
		fluxBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fluxBtn.setEnabled(false);
				if (!updateFluxes()) {
					FluxSearchController.instance().retrieveFluxes(galaxy);
					updateFluxes();
				}
			}
		});
		this.add(fluxBtn);
		layout.putConstraint(SpringLayout.WEST, fluxBtn, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, fluxBtn, 10, SpringLayout.SOUTH, isConBox);
	}
	
	private void initFluxResults(SpringLayout layout) {
		lineLabel = new JLabel();
		this.add(lineLabel);
		layout.putConstraint(SpringLayout.WEST, lineLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, lineLabel, 10, SpringLayout.SOUTH, fluxBtn);
		layout.putConstraint(SpringLayout.EAST, lineLabel, -25, SpringLayout.EAST, this);
		
		continuousLabel = new JLabel();
		this.add(continuousLabel);
		layout.putConstraint(SpringLayout.WEST, continuousLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, continuousLabel, 5, SpringLayout.SOUTH, lineLabel);
		layout.putConstraint(SpringLayout.EAST, continuousLabel, -25, SpringLayout.EAST, this);
		
		conRatioLabel = new JLabel();
		this.add(conRatioLabel);
		layout.putConstraint(SpringLayout.WEST, conRatioLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, conRatioLabel, 5, SpringLayout.SOUTH, continuousLabel);
		layout.putConstraint(SpringLayout.EAST, conRatioLabel, -25, SpringLayout.EAST, this);
		
		lineRatioLabel = new JLabel();
		this.add(lineRatioLabel);
		layout.putConstraint(SpringLayout.WEST, lineRatioLabel, 0, SpringLayout.WEST, spinnerLabel);
		layout.putConstraint(SpringLayout.NORTH, lineRatioLabel, 5, SpringLayout.SOUTH, conRatioLabel);
		layout.putConstraint(SpringLayout.EAST, lineRatioLabel, -25, SpringLayout.EAST, this);
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
}
