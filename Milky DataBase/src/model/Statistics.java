package model;

public class Statistics {
	
	private double avg, stddev, med, mad;
	
	public double getAvearge() {
		double d = Math.floor(avg);
		d += Math.round((avg - d) * 10000) / 10000.0;
		return d; 
	}
	public void setAverage(double avg) { this.avg = avg; }

	public double getStandardDeviation() { 
		double d = Math.floor(stddev);
		d += Math.round((stddev - d) * 10000) / 10000.0;
		return d;	
	}
	public void setStandardDeviation(double stddev) { this.stddev = stddev; }

	public double getMedian() { 
		double d = Math.floor(med);
		d += Math.round((med - d) * 10000) / 10000.0;
		return d; 
	}
	public void setMedian(double med) { this.med = med; }
	
	public double getMedianAbsoluteDev() {
		double d = Math.floor(mad);
		d += Math.round((mad - d) * 10000) / 10000.0;
		return d;
	}
	public void setMedianAbsoluteDev(double mad) { this.mad = mad; }

	public Statistics(double avg, double stddev, double med, double mad) {
		setAverage(avg);
		setStandardDeviation(stddev);
		setMedian(med);
		setMedianAbsoluteDev(mad);
	}
}
