package controller;

import model.Ion;

public class IonRepository {
	
	private static IonRepository me;
	protected IonRepository() {
		this.dataSource = new DataSource();
	}
	public static synchronized IonRepository instance() {
		if (me == null) me = new IonRepository();
		return me;
	}
	
	private DataSource dataSource;
	
	public void persist(Ion ion) throws Exception {
		
	}
	
	private void release(AutoCloseable... resources) throws Exception {
		for (AutoCloseable resource : resources)
			resource.close();
	}
}
