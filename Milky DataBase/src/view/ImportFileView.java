package view;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import controller.ImportFileController;

public class ImportFileView extends View {

	private static ImportFileView me;
	private ImportFileView() { }
	public static synchronized ImportFileView instance() {
		if (me == null) 
			me = new ImportFileView();
		return me;
	}
	
	private JButton ImportButton;
	private JFileChooser chooser;
	private Panel generalPanel;
	private String paths[] = new String[10];
	
	public Panel generateView() {
		if (generalPanel == null) {
			SpringLayout layout = new SpringLayout();
			generalPanel = new Panel();
			generalPanel.setLayout(layout);
			ImportButton = new JButton("Import");
			//generalPanel.add(parent);
			//generalPanel.add(chooser);
			

			ImportButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
	                            "CSV files (*csv)", "csv");
						chooser.setFileFilter(filter);
						int returnVal = chooser.showOpenDialog(generalPanel);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							String path = chooser.getSelectedFile().getAbsolutePath();
						   System.out.println("You chose to open this file: " +
						        path);

						   paths[0]= path;
						   try {
							ImportFileController.importCSV(paths);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							System.out.println("something went wrong");
						}
						}
						else
						{System.out.println("window closed");}
				
			}});

			generalPanel.add(ImportButton);
		}
		return generalPanel;
}
	@Override
	public void showError(Exception e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		
	}
	}
