package controller;

import java.awt.Container;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.ParserException;
import exception.TolerableSQLException;
import model.FluxRepository;
import model.Galaxy;
import model.GalaxyRepository;
import model.Repository;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import view.ImportFileView;
import view.View;
import parser.AbstractCSVParser;

/**
 * The ImportFileController class allows to import a .csv file by receiving a path from the
 * correspondent view (ImportFileView) and calling the parsers in the parser package in accordance with
 * MVC pattern
 * @author federico
 *
 */


public class ImportFileController extends ExceptionSubject {
	private static final int PARSERS[] = {  AbstractCSVParser.IRS, AbstractCSVParser.GLXY,
											AbstractCSVParser.PACS_LINE, AbstractCSVParser.PACS_CON };
	
	private static ImportFileController me;
	private ImportFileController() { 
		view = ImportFileView.instance();
		new ExceptionObserverAdapter(view).setSubject(this);
	}
	
	public static synchronized ImportFileController instance() {
		if (me == null) 
			me = new ImportFileController();
		return me;
	}
	
	private View view;
	private FileWriter writer;
	
	public Container callView() {
		view = ImportFileView.instance();
		return view.generateView();
	}
	/**
	 * Main method of the class. Tries the parsers onto the passed csv and persists the Data.
	 * @param filePath
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void importCSV(String filePath) {
		final String arg0 = filePath;
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(arg0);
				List<Galaxy> results = null;
				int chosen = -1;
				for (int flag : PARSERS) {
					try {
						System.out.println("PARSER " + flag);
						results = AbstractCSVParser.instance(flag).parseFile(file);
						chosen = flag;
						break;
					}
					catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
						continue;
					}
					catch (Exception e) {
						setState(e);
						return;
					}
				}
				
				if (results == null) {
					System.out.println("NO PARSER");
					setState(new ParserException());
				}
				
				Repository repo;
				switch (chosen) {
					case AbstractCSVParser.GLXY:
						repo = new GalaxyRepository(DataSource.byPriviledge());
						break;
					default:
						repo = new FluxRepository(DataSource.byPriviledge());
				}
				
				resetLog();
				openLog();
				TolerableSQLException exception = null;
				
				for (Galaxy galaxy : results) {
					try { repo.persist(galaxy); } 
					catch (SQLException e) {
						report(e);
						if (exception == null)
							exception = new TolerableSQLException(new File("./res/log.txt").getAbsolutePath());
						else exception.increment(); 
					}
					catch(Exception e) {
						closeLog();
						setState(e); 
						return; 
					}
				}
				closeLog();
				setState(exception);
			}
		}).start();
	}
	
	private void report (SQLException exception) {
		try { writer.append(exception.getMessage() + "\n"); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void resetLog() {
		File file = new File("./res/log.txt");
		if (file.exists()) 
			file.delete();			
	}
	
	private void openLog() {			
		try {
			File file = new File("./res/log.txt");
			if (!file.exists())
				file.createNewFile();
			writer = new FileWriter(file);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void closeLog() {
		try { writer.close(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
}
