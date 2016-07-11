package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

/**
 * Follows abstract factory pattern to instantiate the parser specific to a determinated file.
 * Offers methods to work with the type of data present in the .csv file
 * (integers, doubles, booleans, strings")
 * @author federico
 *
 * @param <T>
 */

public abstract class AbstractCSVParser<T> {
	public static final int PACS_LINE = 0, 
							PACS_CON = 1, 
							IRS = 2, 
							GLXY = 3;
	
	@SuppressWarnings("rawtypes")
	public static AbstractCSVParser instance(int type) {
		if (type == GLXY) 
			return GalaxyCSVParser.instance();
		else if (type == PACS_LINE) 
			return PACSLineFluxParser.instance();
		else if (type == PACS_CON) 
			return PACSContinuousFluxParser.instance();
		else if (type == IRS) 
			return IRSFluxCSVParser.instance();
		else return null;
	}
	
	public abstract List<T> parseFile(File file) throws Exception;
	
	protected CSVReader initReader(File file) throws FileNotFoundException {
		return new CSVReader(new FileReader(file), ';', CSVParser.DEFAULT_QUOTE_CHARACTER);
	}
	
	protected static Integer parseInt(String s) throws NumberFormatException {
		if (s == null)
			return null;

		s = s.replaceAll(" ", "");
		if (s.isEmpty())
			return null;

		return Integer.parseInt(s);
	}

	protected static Double parseDouble(String s) throws NumberFormatException {
		if (s == null)
			return null;

		s = s.replaceAll(" ", "");
		if (s.isEmpty())
			return null;

		return Double.parseDouble(s);
	}

	protected static Boolean parseSimbol(String s, String simbol) {
		if (s == null)
			return false;
		return s.contains(simbol);
	}
	
	protected static String parseName(String s) {
		while (s.length() > 0 && " ".equals(s.substring(0, 1))) 
			s = s.substring(1);
		
		while (s.length() > 0 && ' ' == s.charAt(s.length() - 1)) 
			s = s.substring(0, s.length() - 1);
			
		return s;
	}
}
