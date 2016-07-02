package model;

public class Statistics {
	
	private double avg, stddev, med;
	
	public double getAvearge() { return avg; }
	public void setAverage(double avg) { this.avg = avg; }

	public double getStandardDeviation() { return stddev; }
	public void setStandardDeviation(double stddev) { this.stddev = stddev; }

	public double getMedian() { return med; }
	public void setMedian(double med) { this.med = med; }

	public Statistics(double avg, double stddev, double med) {
		setAverage(avg);
		setStandardDeviation(stddev);
		setMedian(med);
	}
}
