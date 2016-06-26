package model;

public class Galaxy {
	
	private String name;
	private double redShift;
	private Coordinates coordinates;
	private double distance;
	private String[] alternativeNames;
	/*
	 * other parameters
	 */
	
	
	
	
	public class Coordinates {
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
		
		protected Coordinates(int hours, int minutes, double seconds, boolean sign, int degrees, int arcmin, double arcsec) {
			setRightAscensionHours(hours);
			setRightAscensionMinutes(minutes);
			setRightAscensionSeconds(seconds);
			setSign(sign);
			setDegrees(degrees);
			setArcMinutes(arcmin);
			setArcSeconds(arcsec);
		}
	}
}
