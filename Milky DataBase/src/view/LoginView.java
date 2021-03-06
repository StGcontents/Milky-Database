package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import controller.DataSource;
import controller.LoginController;

/**
 * View to log in the application. All rights reserved to Stefano Giancristofaro.
 * Each unauthorized use will be legally prosecuted. :D
 * @author federico
 *
 */
public class LoginView extends View {
	
	private static LoginView me;
	private LoginView() { }
	public static synchronized LoginView instance() {
		if (me == null) me = new LoginView();
		return me;
	}
	
	private Frame frame;
	private HintTextField userField, passField;
	private Button logBtn, exitBtn;
	private JLabel label;
	
	//wow such cool shapes!
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
	public Container generateView() {
		frame = new Frame() {

			@Override
			public void paint(Graphics g) {
				Random random = new Random(new Date().getTime());
				int flag = random.nextInt(8);
				try {
					String path = flag == 5 ? "./res/doge.jpg" : "./res/stars.jpg";
					BufferedImage im = ImageIO.read(new File(path));
					g.drawImage(im, 0, 0, null);
				}	
				catch (Exception e) {
					e.printStackTrace();
				}
			
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 52));
				g.setColor(Color.WHITE);
				g.drawString(flag == 5 ? "much login" : "WELCOME.", 325, 190);
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
		
		userField = new HintTextField("Insert user ID");
		passField = new HintTextField("Insert password");
		//userField.validate();
		//passField.validate();
	
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
		logBtn.requestFocusInWindow();
		userField.setText("Insert user ID");
		passField.setText("Insert password");
		
		return null;
	}
	
	private void purge() {
		frame = null;
		userField = null;
		passField = null;
		label = null;
		exitBtn = null;
		logBtn = null;
	}
	
	public void logIn(int priviledgeLevel) {
		if (priviledgeLevel > DataSource.INVALID) {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			purge();
			LoginController.instance().onLoginExit(priviledgeLevel);	
		}
		else {
			label.setText("Check credentials.");
			logBtn.setEnabled(true);
		}
	}
	
	class LogButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			logBtn.setEnabled(false);
			label.setText(null);
			LoginController.instance().log(userField.getText(), passField.getText());
		}
	}
	
	class HintTextField extends TextField implements FocusListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String hint;
		  private boolean showingHint;

		  public HintTextField(final String hint) {
		    super(hint);
		    this.hint = hint;
		    this.showingHint = true;
		    super.addFocusListener(this);
		    
		    
		  }

		  @Override
		  public void focusGained(FocusEvent e) {
		    if(this.getText().isEmpty()) {
		      super.setText("");
		      showingHint = false;
		    }
		  }
		  @Override
		  public void focusLost(FocusEvent e) {
		    if(this.getText().isEmpty()) {
		      super.setText(hint);
		      showingHint = true;
		    }
		  }

		  @Override
		  public String getText() {
		    return showingHint ? "" : super.getText();
		  }
		}

	@Override
	public void showError(Exception e) {
		if (e instanceof SQLException)
			label.setText("Cannot reach server.");
		else 
			label.setText("An error occurred.");
	}
	
	@Override
	protected void reset() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isCurrentlyShown() {
		return frame != null && frame.isVisible();
	}
}
