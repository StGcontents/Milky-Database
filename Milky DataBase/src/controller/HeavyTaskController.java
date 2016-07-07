package controller;

import java.awt.Panel;

import model.FluxRepository;
import model.Priviledge;
import view.HeavyTaskView;

public class HeavyTaskController {
	
	private static HeavyTaskController me;
	private HeavyTaskController() {
		priviledgeLevel = Priviledge.instance().retrieveState();
		repo = new FluxRepository(DataSource.byPriviledge());
	}
	public static synchronized HeavyTaskController instance() {
		if (me == null || me.priviledgeLevel != Priviledge.instance().retrieveState()) 
			me = new HeavyTaskController();
		return me;
	}
	
	private int priviledgeLevel;
	private HeavyTaskView view;
	private FluxRepository repo;
	
	public Panel callView() {
		view = HeavyTaskView.instance();
		view.getObserver().setSubject(repo.getStatSubject());
		return view.generatePanel();
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
