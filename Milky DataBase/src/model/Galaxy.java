package model;

import java.util.ArrayList;
import java.util.List;

public class Galaxy {
	
	private String name;
	private double redShift;
	private Coordinates coordinates;
	private Integer distance;
	private String spectre;
	private Luminosity[] luminosities = new Luminosity[3];
	private Integer metallicity, metallicityError;
	
	private String[] alternativeNames;
	private List<Flux> fluxes = new ArrayList<>();
	
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public double getRedShift() { return redShift; }
	public void setRedShift(double redShift) { this.redShift = redShift; }

	public Coordinates getCoordinates() { return coordinates; }
	public void setCoordinates(Coordinates coordinates) { 
		this.coordinates = coordinates;
	}

	public Integer getDistance() { return distance; }
	public void setDistance(Integer distance) { this.distance = distance; }
	
	public String getSpectre() { return spectre; }
	public void setSpectre(String spectre) { this.spectre = spectre; }
	
	public Luminosity[] getLuminosities() { return luminosities; }
	public void setLuminosity(Luminosity luminosity, int position) { luminosities[position] = luminosity; }
	
	public Integer getMetallicity() { return metallicity; }
	public void setMetallicity(Integer metallicity) { this.metallicity = metallicity; }
	
	public Integer getMetallicityError() { return metallicityError; }
	public void setMetallicityError(Integer metallicityError) { this.metallicityError = metallicityError; }

	public String[] getAlternativeNames() { return alternativeNames; }
	public void setAlternativeNames(String[] alternativeNames) { 
		this.alternativeNames = alternativeNames;
	}
	
	public List<Flux> getFluxes() { return this.fluxes; }
	public void addAll(List<Flux> fluxes) { this.fluxes.addAll(fluxes); }
	
	public static class Coordinates {
		
		private int rightAscensionHours;
		private int rightAscensionMinutes;
		private double rightAscensionSeconds;
		private boolean sign; //+ = true, - = false
		private int degrees;
		private int arcMinutes;
		private double arcSeconds;
		
		public int getRightAscensionHours() { return rightAscensionHours; }
		public void setRightAscensionHours(int rightAscensionHours) { 
			this.rightAscensionHours = rightAscensionHours; 		
		}

		public int getRightAscensionMinutes() { return rightAscensionMinutes; }
		public void setRightAscensionMinutes(int rightAscensionMinutes) {
			this.rightAscensionMinutes = rightAscensionMinutes;
		}

		public double getRightAscensionSeconds() { return rightAscensionSeconds; }
		public void setRightAscensionSeconds(double rightAscensionSeconds) {
			this.rightAscensionSeconds = rightAscensionSeconds;
		}

		public boolean getSign() { return sign; }
		public void setSign(boolean sign) { this.sign = sign; }
		
		public int getDegrees() { return degrees; }
		public void setDegrees(int degrees) { this.degrees = degrees; }

		public int getArcMinutes() { return arcMinutes; }
		public void setArcMinutes(int arcMinutes) { this.arcMinutes = arcMinutes; }

		public double getArcSeconds() { return arcSeconds; }
		public void setArcSeconds(double arcSeconds) { this.arcSeconds = arcSeconds; }
		
		public Coordinates(int hours, int minutes, double seconds, boolean sign, int degrees, int arcmin, double arcsec) {
			setRightAscensionHours(hours);
			setRightAscensionMinutes(minutes);
			setRightAscensionSeconds(seconds);
			setSign(sign);
			setDegrees(degrees);
			setArcMinutes(arcmin);
			setArcSeconds(arcsec);
		}
	}
	
	public static class Luminosity {
		private double value;
		private boolean limit;
		
		public boolean isLimit() { return limit; }
		public double getValue() { return value; }
		
		public Luminosity(double value, boolean limit) {
			this.value = value;
			this.limit = limit;
		}
	}
}
 