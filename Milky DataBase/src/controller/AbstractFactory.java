package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractFactory<T> {

	public abstract List<T> create(ResultSet set) throws SQLException;
}
