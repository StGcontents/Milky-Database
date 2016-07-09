package model;

@SuppressWarnings("rawtypes")
public abstract class AdaptableValue<T> {
	
	public static AdaptableValue getNameValue(String name, String alterName) { 
		return new NameValue(name, alterName);
	}
	
	public static AdaptableValue getDistanceValue(String name, double distance) {
		return new DistanceValue(name, distance);
	}
	
	public static AdaptableValue getRedshiftValue(String name, double redshift) {
		return new RedshiftValue(name, redshift);
	}
	
	protected String name; 

	protected T parameter;
	protected void setParameter(T parameter) { this.parameter = parameter; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getDescription() { return name + parseParameter(); }
	
	protected abstract String parseParameter();
	
	protected AdaptableValue(String name, T parameter) { 
		setName(name);
		setParameter(parameter);
	}
	
	private static class NameValue extends AdaptableValue<String> {
		protected NameValue(String name, String alterName) { super(name, alterName); }

		@Override protected String parseParameter() { 
			if (parameter != null) return " (aka " + parameter + ")";
			else return "";
		}
	}
	
	protected static abstract class DoubleValue extends AdaptableValue<Double> {
		protected DoubleValue(String name, double parameter) { super(name, parameter); }
		protected double precision;
		protected double truncatedValue() {
			double d = Math.floor(parameter); 
			return d + Math.floor((parameter - d) * precision) / precision;
		}
	}
	
	private static class DistanceValue extends DoubleValue {
		protected DistanceValue(String name, double distance) { 
			super(name, distance);
			precision = 10000.0;
		}
		@Override protected String parseParameter() { return " (distance from center: " + truncatedValue() + ")"; }
	}
	
	private static class RedshiftValue extends DoubleValue {
		protected RedshiftValue(String name, double redshift) { 
			super(name, redshift);
			precision = 1000000.0;
		}
		@Override protected String parseParameter() { return " (redshift value: " + truncatedValue() + ")"; }
	}
}
