package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public abstract class NicerCellRenderer<E> implements ListCellRenderer<E> {

	@SuppressWarnings("serial")
	@Override
	public Component getListCellRendererComponent(JList<? extends E> arg0, E arg1, int arg2, boolean arg3,
			boolean arg4) {
		JLabel label = new JLabel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(new Color(20, 20, 20, 20));
				g.drawLine(5, getHeight(), getWidth() - 5, getHeight());
			}
		};
		
		label.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		label.setOpaque(true);
		
		JList.DropLocation location = arg0.getDropLocation();
		Color background;
        Color foreground;
         
		if (location != null && !location.isInsert() && location.getIndex() == arg2) {
             background = Color.CYAN;
             foreground = Color.BLACK;
		}
        else if (arg3) {
        	background = Color.BLUE;
            foreground = Color.WHITE;
		}
		else {
			background = Color.WHITE;
            foreground = Color.BLACK;
		}	
		label.setBackground(background);
		label.setForeground(foreground);
		
		return label;
	}
}
