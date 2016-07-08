package parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import model.Galaxy;
import model.Ion;
import model.IonPool;

public abstract class PACSFluxCSVParser extends FluxCSVParser {
	
	@Override
	public List<Galaxy> parseFile(File file) {
		String[] nextLine;
		List<Galaxy> galaxies = new ArrayList<Galaxy>();
		CSVReader reader = null;
		
		try {
			reader = new CSVReader(new FileReader(file), ';', CSVParser.DEFAULT_QUOTE_CHARACTER);

			while ((nextLine = reader.readNext()) != null) {
				
				String name = parseName(nextLine[0]), 
					aperture = parseName(nextLine[nextLine.length - 2]);
				
				clearValues();

				int limit = nextLine.length - 2;
				String s = nextLine[1];
				for (int i = 1; i < limit; ++i, s = nextLine[i]) {
					//double checker
					if (DOUBLE_ENUM.contains(i)) 
						values.add(parseDouble(s));
					// Flag Checker
					else if (FLAG_ENUM.contains(i)) 
						flags.add(parseSimbol(s, "<"));
				}
				
				Galaxy galaxy = new Galaxy();
				galaxy.setName(name);
				fillGalaxy(galaxy, aperture);	
				galaxies.add(galaxy);
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
		catch (Exception e) { e.printStackTrace(); }
		finally { 
			try { reader.close(); } 
			catch (Exception ignore) {} 
		}
		
		return galaxies;
	}
	
	protected void clearValues() {
		values.clear();
		flags.clear();
	}

	@Override
	protected void initIonEnum() {
		ION_ENUM.addAll(Arrays.asList(new Ion[] {
				IonPool.getByInfo("O", 3, 52),
				IonPool.getByInfo("N", 3, 57),
				IonPool.getByInfo("O", 1, 63),
				IonPool.getByInfo("O", 3, 88),
				IonPool.getByInfo("N", 2, 122),
				IonPool.getByInfo("O", 1, 145),
				IonPool.getByInfo("C", 2, 158)
		}));
	}		
}