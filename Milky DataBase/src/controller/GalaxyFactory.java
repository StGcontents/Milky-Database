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
					String name = set.getString(1);
					
					int h, min, deg, arcmin;
					double sec, arcsec;
					boolean sign;
					
					h = set.getInt(2);
					min = set.getInt(3);
					sec = set.getDouble(4);
					sign = "+".equals(set.getString(5));
					deg = set.getInt(6);
					arcmin = set.getInt(7);
					arcsec = set.getDouble(8);
				
					double redshift = set.getDouble(9);
					Double distance = set.getDouble(10);
					if (set.wasNull()) distance = null;
					
					String spectre = set.getString(11);
					
					Luminosity luminosities[] = new Luminosity[3];
					for (int i = 0; i < 3; i++) {
						double lum = set.getDouble(12 + 2 * i);
						if (!set.wasNull()) 
							luminosities[i] = new Luminosity(lum, set.getBoolean(12 + 2 * i + 1));
					}
					
					Double metallicity = set.getDouble(18);
					if (set.wasNull()) metallicity = null;
					Double metallicityError = set.getDouble(19);
					if (set.wasNull()) metallicityError = null;
					
					Galaxy galaxy = createGalaxy(name, h, min, sec, sign, deg, arcmin, arcsec,
							redshift, distance, spectre, luminosities, metallicity, metallicityError);
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
	
	public Galaxy createGalaxy(String name, int h, int min, double sec, 
			boolean sign, int deg, int arcmin, double arcsec, double redshift, 
			Double distance, String spectre, Luminosity[] luminosities, 
			Double metallicity, Double metallicityError) {
		Galaxy galaxy = new Galaxy();
		galaxy.setName(name);
		
		Galaxy.Coordinates coordinates = 
				new Galaxy.Coordinates(h, min, sec, sign, deg, arcmin, arcsec);
		galaxy.setCoordinates(coordinates);
		
		galaxy.setRedShift(redshift);
		galaxy.setDistance(distance);
		galaxy.setMetallicity(metallicity);
		galaxy.setMetallicityError(metallicityError);
		galaxy.setSpectre(spectre);
		for (int i = 0; i < 3; ++i) 
			galaxy.setLuminosity(luminosities[i], i);
		
		return galaxy;
	}
}
