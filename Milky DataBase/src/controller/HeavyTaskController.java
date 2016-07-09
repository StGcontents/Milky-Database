package controller;

import java.awt.Container;

import model.FluxRepository;
import pattern.ExceptionObserverAdapter;
import pattern.ExceptionSubject;
import pattern.StatisticsObserverAdapter;
import view.HeavyTaskView;

public class HeavyTaskController extends ExceptionSubject {
	
	private static HeavyTaskController me;
	private HeavyTaskController() {
		repo = new FluxRepository(DataSource.byPriviledge());
		view = HeavyTaskView.instance();
		new StatisticsObserverAdapter(view).setSubject(repo.getStatSubject());
		new ExceptionObserverAdapter(view).setSubject(this);
	}
	public static synchronized HeavyTaskController instance() {
		if (me == null) me = new HeavyTaskController();
		return me;
	}
	
	private HeavyTaskView view;
	private FluxRepository repo;
	
	public Container callView() {
		return view.generateView();
	}
	
	public void calculate(String spectralGroup, String apertureSize) {
		final String param0 = spectralGroup;
		final String param1 = apertureSize;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { repo.calculate(param0, param1); }
				catch (Exception e) { e.printStackTrace(); }	
			}
		}).start();
	}
}
