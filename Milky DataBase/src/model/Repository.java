package model;

import controller.DataSource;

public abstract class Repository {
	
	protected DataSource dataSource;
	
	protected void release(AutoCloseable... resources) throws Exception {
		for (AutoCloseable resource : resources)
			resource.close();
	}
}
