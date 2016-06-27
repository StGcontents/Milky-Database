package controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Galaxy;

public class GalaxyFactory extends AbstractFactory<Galaxy> {
	
	private static GalaxyFactory me;
	protected GalaxyFactory() { }
	public static synchronized GalaxyFactory instance() {
		if (me == null) me = new GalaxyFactory();
		return me;
	}

	@Override
	public List<Galaxy> create(ResultSet set) {
		List<Galaxy> galaxies = new ArrayList<>();
		try {
			while (set.next()) {
				try {
					Galaxy galaxy = new Galaxy();
					galaxy.setName(set.getString(1));
					//etc
					
					galaxies.add(galaxy);
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return galaxies;
	}

	

}
