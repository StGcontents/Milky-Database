package view;

@SuppressWarnings("serial")
public class IntTextField extends NumericTextField<Integer> {
	
	public IntTextField() {
		super();
		values = "0123456789";
		addListener();
	}

	@Override
	public Integer getValue() {
		String text = getText();
		if (text == null || "".equals(text)) return null;
		return Integer.valueOf(text);
	}
	
	
}
