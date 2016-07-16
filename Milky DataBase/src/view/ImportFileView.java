package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import controller.ImportFileController;
import exception.TolerableSQLException;

/**
 * View that allows to choose .csv file to import through a JFileChooser
 * A filter has been used to restraint user from importing different kinds of file.
 * After selection, calls the controller for further actions.
 * @author federico
 *
 */
public class ImportFileView extends View {

	private static ImportFileView me;

	private ImportFileView() {
	}

	public static synchronized ImportFileView instance() {
		if (me == null)
			me = new ImportFileView();
		return me;
	}

	private JButton importButton;
	private JLabel resultLabel;
	private JPanel generalPanel;

	public JPanel generateView() {
		if (generalPanel == null) {
			SpringLayout layout = new SpringLayout();
			generalPanel = new JPanel();
			generalPanel.setLayout(layout);
			
			importButton = new JButton("Import");

			importButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					reset();
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files (*csv)", "csv");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(generalPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						importButton.setEnabled(false);
						
						String path = chooser.getSelectedFile().getAbsolutePath();
						System.out.println("You chose to open this file: " + path);

						ImportFileController.instance().importCSV(path);
					} 
					else 
						System.out.println("window closed");
				}
			});

			generalPanel.add(importButton);
			layout.putConstraint(SpringLayout.NORTH, importButton, 25, SpringLayout.NORTH, generalPanel);
			layout.putConstraint(SpringLayout.WEST, importButton, 25, SpringLayout.WEST, generalPanel);
			
			resultLabel = new JLabel();
			generalPanel.add(resultLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, resultLabel, 0, SpringLayout.VERTICAL_CENTER, importButton);
			layout.putConstraint(SpringLayout.WEST, resultLabel, 15, SpringLayout.EAST, importButton);
		}
		return generalPanel;
	}

	@Override
	public void showError(Exception e) {
		if (e == null) 
			resultLabel.setText("File successfully parsed.");
		else if (e instanceof FileNotFoundException)
			resultLabel.setText("Selected file could not be found.");
		else if (e instanceof IOException)
			resultLabel.setText("Selected file could not be read.");
		else if (e instanceof TolerableSQLException) 
			resultLabel.setText(e.getMessage());
		else if (e instanceof SQLException)
			resultLabel.setText("Selected file could not be found.");
		else resultLabel.setText("An unexpected error occurred while parsing file.");
		
		importButton.setEnabled(true);
	}

	@Override
	protected void reset() {
		resultLabel.setText(null);
	}

	@Override
	public boolean isCurrentlyShown() {
		return generalPanel != null && generalPanel.isVisible();
	}
}
