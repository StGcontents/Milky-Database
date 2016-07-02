package view;

import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings("serial")
public abstract class NumericTextField<T extends Number> extends TextField {
	protected String values;
	public abstract T getValue() ;
	protected void addListener() {
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
				
				Character c = e.getKeyChar();
				
				if (values.contains(c.toString())) {
					if ('.' != c) {
						setText(getText() + c);
						setCaretPosition(getText().length());
					}
					else {
						if (getText() != null && !"".equals(getText()) && !getText().contains(".")) {
							setText(getText() + c);
							setCaretPosition(getText().length());
						}
					}
				}
			}
			
			@Override public void keyReleased(KeyEvent e) { }
			@Override public void keyPressed(KeyEvent e) { }
		});
	}
}
