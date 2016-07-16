package parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

import controller.FluxFactory;
import model.Galaxy;
import model.Ion;
import model.IonPool;

/**
 * Parser that imports data from Spitzer satellite files.
 * @author federico
 *
 */
public class IRSFluxCSVParser extends FluxCSVParser {
	
	private final static Integer[] FLAG_COLUMNS = new Integer[] { 1, 4, 7, 10, 13, 16, 19, 22, 25 };
	private final static Integer[] VALUES_COLUMNS = new Integer[] { 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21, 23, 24, 26, 27 };

	private static IRSFluxCSVParser me;
	private IRSFluxCSVParser() { 
		initEnums();
	}
	public static void main(String args[]) throws Exception {
		instance().parseFile(new File(args[0]));
	}
	
	protected static synchronized FluxCSVParser instance() {
		if (me == null) me = new IRSFluxCSVParser();
		return me;
	}
	
	@Override
	protected void initIonEnum() {
		ION_ENUM.addAll(Arrays.asList(new Ion[] {
				IonPool.getByInfo("S", 4, 10.5),
				IonPool.getByInfo("Ne", 2, 12.8),
				IonPool.getByInfo("Ne", 5, 14.3),
				IonPool.getByInfo("Ne", 3, 15.6),
				IonPool.getByInfo("S", 3, 18.7),
				IonPool.getByInfo("Ne", 5, 24.3),
				IonPool.getByInfo("O", 4, 25.9),
				IonPool.getByInfo("S", 3, 33.5),
				IonPool.getByInfo("Si", 2, 34.8)
		}));
	}

	@Override
	protected void initValueEnums() {
		FLAG_ENUM.addAll(Arrays.asList(FLAG_COLUMNS));
		DOUBLE_ENUM.addAll(Arrays.asList(VALUES_COLUMNS));
	}

	@Override
	protected FluxFactory getFactory() {
		return FluxFactory.getLineFluxFactory();
	}

	@Override
	public List<Galaxy> parseFile(File file) throws Exception {
		String[] nextLine;
		List<Galaxy> galaxies = new ArrayList<Galaxy>();
		CSVReader reader = null;
		
		try {
			reader = initReader(file);

			while ((nextLine = reader.readNext()) != null) {
				
				String name = parseName(nextLine[0]), 
					resolution = parseName(nextLine[28]);
				
				values.clear();
				flags.clear();

				String s = nextLine[1];
				for (int i = 1; i < 28; ++i, s = nextLine[i]) {
					//double checker
					if (DOUBLE_ENUM.contains(i)) 
						values.add(parseDouble(s));
					// Flag Checker
					else if (FLAG_ENUM.contains(i)) 
						flags.add(parseSimbol(s, "<"));
				}
				
				Galaxy galaxy = new Galaxy();
				galaxy.setName(name);
				fillGalaxy(galaxy, resolution);	
				galaxies.add(galaxy);
			}
		} 
		finally { 
			try { reader.close(); } 
			catch (Exception ignore) { } 
		}
		
		return galaxies;
	}
}
