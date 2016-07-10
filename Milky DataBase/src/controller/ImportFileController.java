package controller;

import java.awt.Panel;

import model.UserRepository;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import view.AddUserView;
import view.ImportFileView;
import parser.GalaxyCSVParser;
import parser.PACSLineFluxParser;
import parser.PACSContinuousFluxParser;
import parser.IRSFluxCSVParser;

public class ImportFileController extends ExceptionSubject {
	private static ImportFileController me;
	private UserRepository repo;
	private ImportFileView view;
	
	private ImportFileController() { 
		repo = new UserRepository(DataSource.byPriviledge());
		view = ImportFileView.instance();
		new ExceptionObserverAdapter(view).setSubject(this);
	}
	
	public static synchronized ImportFileController instance() {
		if (me == null) 
			me = new ImportFileController();
		return me;
	}
	
	public Panel callView() {
		view = ImportFileView.instance();
		return view.generateView();
	}
	
	public static void importCSV(String filePath[]) throws Exception{
		System.out.println("provo primo parser");
			GalaxyCSVParser.main(filePath);
			System.out.println("provo secondo parser");
			IRSFluxCSVParser.main(filePath);
			System.out.println("provo terzo parser");
			PACSLineFluxParser.main(filePath);
			System.out.println("provo quarto parser");
			PACSContinuousFluxParser.main(filePath);
	}
			

	}
