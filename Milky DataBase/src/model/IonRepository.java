package model;

import controller.DataSource;

public class IonRepository extends Repository {
	
	private static IonRepository me;
	protected IonRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public static synchronized IonRepository instance(DataSource dataSource) {
		if (me == null) me = new IonRepository(dataSource);
		return me;
	}
	
	public void persist(Ion ion) throws Exception {
		
	}
}
