package model;

import java.sql.PreparedStatement;
import java.util.List;

import controller.DataSource;

public abstract class Repository<C, R, U, D> {

	protected DataSource dataSource;
	
	public abstract void persist(C entity) throws Exception ;
	public abstract List<R> read(PreparedStatement statement) throws Exception ;
	public abstract void update(U entity) throws Exception ;
	public abstract void delete(D entity) throws Exception ;
	
	protected void release(AutoCloseable... resources) {
		for (AutoCloseable resource : resources) {
			try { resource.close(); }
			catch (NullPointerException ignore) { }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}
