package controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Galaxy;
import model.Galaxy.Luminosity;

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
					
					int hours, minutes, degrees, arcmin;
					double seconds, arcsec;
					boolean sign;
					
					hours = set.getInt(2);
					minutes = set.getInt(3);
					seconds = set.getDouble(4);
					sign = "+".equals(set.getString(5));
					degrees = set.getInt(6);
					arcmin = set.getInt(7);
					arcsec = set.getDouble(8);
				
					Galaxy.Coordinates coordinates = 
							new Galaxy.Coordinates(hours, minutes, seconds, sign, degrees, arcmin, arcsec);
					galaxy.setCoordinates(coordinates);
					
					galaxy.setRedShift(set.getDouble(9));
					int distance = set.getInt(10);
					if (!set.wasNull()) galaxy.setDistance(new Integer(distance));
					
					galaxy.setSpectre(set.getString(11));
					
					for (int i = 0; i < 3; i++) {
						double lum = set.getDouble(12 + 2 * i);
						if (!set.wasNull()) 
							galaxy.setLuminosity(new Luminosity(lum, set.getBoolean(12 + 2 * i + 1)), i);
					}
					
					int metallicity = set.getInt(18);
					if (!set.wasNull()) galaxy.setMetallicity(new Integer(metallicity));
					int metallicityError = set.getInt(19);
					if (!set.wasNull()) galaxy.setMetallicity(new Integer(metallicityError));
					
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
