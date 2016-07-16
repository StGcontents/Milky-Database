package model;

import java.sql.PreparedStatement;
import java.util.List;

import controller.DataSource;
/**
 * Repositories have the job to connect with the Database and execute SQL query/operations on it
 * through the use of jdbc driver.
 * Overridden methods can be found in proper repositories, specific to the entity treated.
 * Every CRUD method possibly manages different kinds of objects.
 * 
 * @author federico
 *
 * @param <C>: Type of objects persisted.
 * @param <R>: Type of objects retrieved.
 * @param <U>: Type of objects updated.
 * @param <D>: Type of objects deleted.
 */

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
