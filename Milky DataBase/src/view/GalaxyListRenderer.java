package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import model.AdaptableValue;

@SuppressWarnings("rawtypes")
public class GalaxyListRenderer implements ListCellRenderer<AdaptableValue> {

	@Override
	public Component getListCellRendererComponent(JList<? extends AdaptableValue> list, AdaptableValue value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		JList.DropLocation location = list.getDropLocation();
		Color background;
        Color foreground;
         
		if (location != null && !location.isInsert() && location.getIndex() == index) {
             background = Color.CYAN;
             foreground = Color.BLACK;
		}
        else if (isSelected) {
        	background = Color.BLUE;
            foreground = Color.WHITE;
		}
		else {
			background = Color.WHITE;
            foreground = Color.BLACK;
		}	
		
		String string = value.getDescription();
		
		JLabel label = new JLabel(string);
		label.setSize(new Dimension(Label.WIDTH, 10));
		label.setBackground(background);
		label.setForeground(foreground);
		label.setOpaque(true);
		
		return label;
	}
}
