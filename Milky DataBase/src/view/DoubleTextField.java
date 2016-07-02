package view;

@SuppressWarnings("serial")
public class DoubleTextField extends NumericTextField<Double> {
	 
	public DoubleTextField() {
		super();
		values = ".0123456789";
		addListener();
	}
	
	@Override
	public Double getValue() {
		String text = getText();
		if (text == null || "".equals(text)) return null;
		return Double.valueOf(text);
	}
}
