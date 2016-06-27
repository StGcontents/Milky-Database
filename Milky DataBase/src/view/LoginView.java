package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;

import javax.swing.SpringLayout;

public class LoginView {

	public static void main(String[] args) {
		generateView().setVisible(true);
	}
	
	private static Shape coolshapes() {
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 200);
		path.curveTo(0, 200, 0, 0, 400, 0);
		path.curveTo(400, 0, 800, 0, 800, 200);
		path.curveTo(800, 200, 800, 400, 400, 400);
		path.curveTo(400, 400, 200, 400, 200, 300);
		path.curveTo(200, 300, 200, 400, 600, 400);
		path.curveTo(600, 400, 950, 400, 950, 300);
		path.curveTo(950, 300, 950, 500, 550, 500);
		path.curveTo(550, 500, 150, 500, 150, 300);
		path.curveTo(150, 300, 150, 100, 550, 100);
		path.curveTo(550, 100, 750, 100, 750, 200);
		path.curveTo(750, 200, 750, 100, 350, 100);
		path.curveTo(350, 100, 0, 100, 0, 200);
		
		return path;
	}
	
	static Point p;
	
	private static Frame generateView() {
		final Frame frame = new Frame("Demo");
		
		SpringLayout layout = new SpringLayout(); 
		frame.setLayout(layout);
		frame.setSize(950, 500);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("exiting");
				System.exit(0);
			}
		});
		
		frame.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) { p = null; }
			@Override public void mousePressed(MouseEvent e) { p = e.getPoint(); }
			@Override public void mouseClicked(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
		});
		frame.addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseMoved(MouseEvent e) { }
			@Override 
			public void mouseDragged(MouseEvent e) {
				Point point = e.getLocationOnScreen();
				try { frame.setLocation(point.x - p.x, point.y - p.y); }
				catch (NullPointerException ignore) { }
			}
		});
		
		final Label label = new Label("This is a label");
		label.setAlignment(Label.CENTER);
		label.setMinimumSize(new Dimension(500, 25));
	
		Button button = new Button("Log in"), exitBtn = new Button("Exit");
		button.setSize(50, 25);
		exitBtn.setSize(50,  25);
		button.addActionListener(new ActionListener() {
			boolean bool = true;
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bool) label.setText("PUSH THE BUTTON");
				else label.setText("MANGANESIO");
				bool = !bool;
			}
		});
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		frame.add(label);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, label, -50, SpringLayout.VERTICAL_CENTER, frame);
		frame.add(button);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, -50, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, button, 50, SpringLayout.VERTICAL_CENTER, frame);
		frame.add(exitBtn);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, exitBtn, 50, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, exitBtn, 50, SpringLayout.VERTICAL_CENTER, frame);
		frame.setUndecorated(true);
		frame.setShape(coolshapes());
		float[] hsb = new float[3];
		Color.RGBtoHSB(12, 71, 118, hsb);
		frame.setBackground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		
		return frame;
	}

}
