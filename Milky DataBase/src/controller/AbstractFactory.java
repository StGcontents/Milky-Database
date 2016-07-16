package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/*
 * Abstract Factory pattern for project POJOs.
 */
public abstract class AbstractFactory<T> {

	public abstract List<T> create(ResultSet set) throws SQLException;
}
