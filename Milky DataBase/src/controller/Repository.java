package controller;

public abstract class Repository {
	
	protected DataSource dataSource;
	
	protected void release(AutoCloseable... resources) throws Exception {
		for (AutoCloseable resource : resources)
			resource.close();
	}
}
