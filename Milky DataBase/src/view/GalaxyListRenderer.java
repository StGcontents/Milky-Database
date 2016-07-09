package view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import model.AdaptableValue;

@SuppressWarnings("rawtypes")
public class GalaxyListRenderer extends NicerCellRenderer<AdaptableValue> {

	@Override
	public Component getListCellRendererComponent(JList<? extends AdaptableValue> list, AdaptableValue value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		String string = value.getDescription();
		label.setText(string);
		
		return label;
	}
}
