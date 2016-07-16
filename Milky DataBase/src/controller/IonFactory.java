package controller;

import java.sql.ResultSet;
import java.util.List;

import model.Ion;
import model.IonPool;

/**
 * Concrete Factory for Ion pojos, automatically cached into the IonPool cache.
 * @author stg
 *
 */
public class IonFactory extends AbstractFactory<Ion> {
	
	private static IonFactory me;
	private IonFactory() { }
	public static synchronized IonFactory instance() {
		if (me == null) me = new IonFactory();
		return me;
	}

	@Override
	public List<Ion> create(ResultSet set) {
		try {
			while (set.next()) {
				try {
					int id = set.getInt(1);
					String name = set.getString(2);
					int charges = set.getInt(3);
					double line = set.getDouble(4);
					Ion ion = new Ion(id, name, charges, line);
					IonPool.insert(ion);
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

}
