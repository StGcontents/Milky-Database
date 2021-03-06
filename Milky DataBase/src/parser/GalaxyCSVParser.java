package parser;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

import controller.GalaxyFactory;
import model.Galaxy;
import model.Galaxy.Luminosity;

/**
 * Parser to import data from the MR3 table (galaxies and their properties)
 * Skips unwanted columns.
 * @author federico
 *
 */

public class GalaxyCSVParser extends AbstractCSVParser<Galaxy> {
	
	private final static List<Integer> INT_ENUM = Arrays.asList(new Integer[] { 1, 2, 5, 6 });
	private final static List<Integer> DOUBLE_ENUM = Arrays.asList(new Integer[] { 3, 7, 8, 9, 22, 23 });
	
	private static GalaxyCSVParser me;
	private GalaxyCSVParser() { }
	
	protected static synchronized GalaxyCSVParser instance() {
		if (me == null) me = new GalaxyCSVParser();
		return me;
	}
	
	@Override
	public List<Galaxy> parseFile(File file) throws Exception {
		String[] nextLine;
		List<Galaxy> galaxies = new ArrayList<Galaxy>();
		CSVReader reader = null;
		try {
			reader = initReader(file);
			
			while ((nextLine = reader.readNext()) != null) {

				Galaxy galaxy;
				String name = parseName(nextLine[0]),
						spectre = parseName(nextLine[11]);
				List<Integer> intValues = new ArrayList<>();
				List<Double> doubleValues = new ArrayList<>();
				boolean sign = parseSimbol(nextLine[4], "+"),
						upper = false;
				Double lum = null;
				Luminosity[] luminosities = new Luminosity[3];
				String[] alternativeNames = parseAlterNames(nextLine[25]);

				String s = nextLine[1];
				for (int i = 1; i < 25; ++i, s = nextLine[i]) {			
					// Integer Checker
					if (INT_ENUM.contains(i)) 
						intValues.add(parseInt(s));
					// double checker
					else if (DOUBLE_ENUM.contains(i)) 
						doubleValues.add(parseDouble(s));
					else if (i >= 16 && i <= 21) {
						if (i % 2 == 0) 
							upper = parseSimbol(s, "<");
						else {
							lum = parseDouble(s);
							if (lum == null) continue;
							else luminosities[(i - 17) / 2] = new Luminosity(lum, upper);
						}
					}
				}
				
				galaxy = GalaxyFactory.
						instance().
						createGalaxy(name, intValues.get(0), intValues.get(1), doubleValues.get(0), 
								sign, intValues.get(2), intValues.get(3), doubleValues.get(1), 
								doubleValues.get(2), doubleValues.get(3), spectre, luminosities, 
								doubleValues.get(4), doubleValues.get(5));
				galaxy.setAlternativeNames(alternativeNames);
				
				galaxies.add(galaxy);
			}
		} 
		finally {
			try { reader.close(); }
			catch (Exception ignore) { }
		}
		
		return galaxies;
	}

	private static String[] parseAlterNames(String s) {
		String alterNames[] = s.split(", ");
		if ("".equals(parseName(alterNames[0]))) return null;
		
		for (int i = 0; i < alterNames.length; ++i) 
			alterNames[i] = parseName(alterNames[i]);
		
		return alterNames;
  }
}