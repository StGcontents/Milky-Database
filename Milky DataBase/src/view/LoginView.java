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
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import controller.DataSource;
import controller.LoginController;
import pattern.Observer;

public class LoginView extends Observer<Integer> {
	
	private static LoginView me;
	private LoginView() { }
	public static synchronized LoginView instance() {
		if (me == null) me = new LoginView();
		return me;
	}
	
	private Frame frame;
	private TextField userField, passField;
	private Button logBtn, exitBtn;
	private JLabel label;
	
	private Shape coolshapes() {
		Path2D path = new GeneralPath();
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
	
	@SuppressWarnings("serial")
	public void generateView() {
		frame = new Frame() {

			@Override
			public void paint(Graphics g) {
				try {
					BufferedImage im = ImageIO.read(new File("./res/stars.jpg"));
					g.drawImage(im, 0, 0, null);
				}	
				catch (Exception e) {
					e.printStackTrace();
				}
			
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 52));
				g.setColor(Color.WHITE);
				g.drawString("WELCOME.", 325, 190);
			}			
		};
		
		SpringLayout layout = new SpringLayout(); 
		frame.setLayout(layout);
		frame.setSize(950, 500);
		frame.setLocation(208, 134);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("exiting");
				frame.dispose();
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
		
		userField = new TextField("Insert user ID");
		passField = new TextField("Insert password");
	
		logBtn = new Button("LOG IN");
		logBtn.setSize(50, 25);
		logBtn.setForeground(Color.WHITE);
		
		exitBtn = new Button("EXIT");
		exitBtn.setSize(50,  25);
		exitBtn.setForeground(Color.WHITE);
		
		logBtn.addActionListener(new LogButtonListener());
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		label = new JLabel();
		label.setSize(300, 20);
		label.setOpaque(true);
		label.setBackground(new Color(0, 0, 0, 0));
		label.setForeground(Color.RED);
		
		frame.add(userField);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, userField, 0, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, userField, -25, SpringLayout.VERTICAL_CENTER, frame);
		
		frame.add(passField);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, passField, 0, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, passField, 25, SpringLayout.VERTICAL_CENTER, frame);
		
		frame.add(logBtn);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, logBtn, -50, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, logBtn, 75, SpringLayout.VERTICAL_CENTER, frame);
		
		frame.add(exitBtn);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, exitBtn, 50, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, exitBtn, 75, SpringLayout.VERTICAL_CENTER, frame);
		
		frame.add(label);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label, 0, SpringLayout.HORIZONTAL_CENTER, frame);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, label, 100, SpringLayout.VERTICAL_CENTER, frame);
		
		frame.setUndecorated(true);
		frame.setShape(coolshapes());
		float[] hsb = new float[3];
		Color.RGBtoHSB(7, 16, 31, hsb);
		frame.setBackground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		
		frame.setVisible(true);
	}
	
	private void purge() {
		frame = null;
		userField = null;
		passField = null;
		label = null;
		exitBtn = null;
		logBtn = null;
	}
	
	@Override
	public void stateChanged() {
		int priviledgeLevel = getSubject().retrieveState();
		
		if (priviledgeLevel > DataSource.INVALID) {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			purge();
			LoginController.instance().onLoginExit(priviledgeLevel);	
		}
		else {
			label.setText("Check credentials.");
		}
	}
	
	class LogButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					LoginController.instance().log(userField.getText(), passField.getText());
				}
			}).start();
		}
	}
}
