package model;

public class Flux {
	
	private boolean isContinuous;
	private Ion ion;
	private double flux;
	private boolean upperLimit;
	private double error;
	private String aperture;
	public boolean isContinuous() { return isContinuous; }
	public void setContinuous(boolean isContinuous) { this.isContinuous = isContinuous; }
	
	public Ion getIon() { return ion; }
	public void setIon(Ion ion) { this.ion = ion; }
	
	public double getFlux() { return flux; }
	public void setFlux(double flux) { this.flux = flux; }
	
	public boolean isUpperLimit() { return upperLimit; }
	public void setUpperLimit(boolean upperLimit) { this.upperLimit = upperLimit; }
	
	public double getError() { return error; }
	public void setError(double error) { this.error = error; }
	
	public String getAperture() { return aperture; }
	public void setAperture(String aperture) { this.aperture = aperture; }
	
	
}
