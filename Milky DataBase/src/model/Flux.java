package model;

public class Flux {
	
	private boolean isContinuous;
	private Ion ion;
	private double value;
	private boolean upperLimit;
	private Double error;
	private String aperture;
	public boolean isContinuous() { return isContinuous; }
	public void setContinuous(boolean isContinuous) { this.isContinuous = isContinuous; }
	
	public Ion getIon() { return ion; }
	public void setIon(Ion ion) { this.ion = ion; }
	
	public double getValue() { return value; }
	public void setValue(double value) { this.value = value; }
	
	public boolean isUpperLimit() { return upperLimit; }
	public void setUpperLimit(boolean upperLimit) { this.upperLimit = upperLimit; }
	
	public Double getError() { return error; }
	public void setError(Double error) { this.error = error; }
	
	public String getAperture() { return aperture; }
	public void setAperture(String aperture) { this.aperture = aperture; }
}
