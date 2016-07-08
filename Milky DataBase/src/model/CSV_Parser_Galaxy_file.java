package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import controller.DataSource;
import controller.GalaxyFactory;
import model.Galaxy.Luminosity;

public class CSV_Parser_Galaxy_file {
	
	private final static List<Integer> INT_ENUM = Arrays.asList(new Integer[] { 1, 2, 5, 6 });
	private final static List<Integer> DOUBLE_ENUM = Arrays.asList(new Integer[] { 3, 7, 8, 9, 17, 19, 21, 22, 23 });
	
	public static void main(String[] args) {
		String[] nextLine;
		List<Galaxy> galaxies = new ArrayList<Galaxy>();
		try {
			System.out.println("BEGIN");
			// csv file containing data ,CHANGE THE PATH!!!!
			String strFile = "/home/stg/Downloads/progetto15161/MRTable3_sample.csv";
			// following constructor allows to skip the first n lines of the
			// file (in our case, skip the header)
			CSVReader reader = new CSVReader(new FileReader(strFile), ';', CSVParser.DEFAULT_QUOTE_CHARACTER, 64);
			System.out.println("READ");
			while ((nextLine = reader.readNext()) != null) {
				System.out.println("NEW LINE");

				Galaxy galaxy;
				String name = null;
				List<Integer> intValues = new ArrayList<>();
				List<Double> doubleValues = new ArrayList<>();
				boolean sign = true, upper = false;
				String spectre = null;
				Luminosity[] luminosities = new Luminosity[3];
				String[] alternativeNames = new String[] { };

				String s = nextLine[0];
				for (int i = 0; i < nextLine.length - 1; ++i, s = nextLine[i]) {

					if (i == 0) name = parseName(s); 
					else if (i == 4) sign = parseSimbol(s, "+");					
					// Integer Checker
					else if (INT_ENUM.contains(i)) 
						intValues.add(parseInt(s));
					// double checker
					else if (DOUBLE_ENUM.contains(i)) 
						doubleValues.add(parseDouble(s));

					else if (i == 11) 
						spectre = s.replaceAll("\\s", "");

					else if (i >= 16 && i <= 21) {//ColumnNumber == 16 || ColumnNumber == 18 || ColumnNumber == 20) {
						if (i % 2 == 0) 
							upper = parseSimbol(s, "<");
						else {
							Double d = parseDouble(s);
							if (d == null) continue;
							else luminosities[(i - 17) / 2] = new Luminosity(d, upper);
						}
					}
					else if (i == 25) 
						alternativeNames = parseAlterNames(s);
				}
				
				galaxy = GalaxyFactory.
						instance().
						createGalaxy(name, intValues.get(0), intValues.get(1), doubleValues.get(0), 
								sign, intValues.get(2), intValues.get(3), doubleValues.get(1), 
								doubleValues.get(2), doubleValues.get(3), spectre, luminosities, 
								doubleValues.get(4), doubleValues.get(5));
				galaxy.setAlternativeNames(alternativeNames);
				
				galaxies.add(galaxy);
				System.out.println("ADDING");
			}
			
			reader.close();
			System.out.println("CLOSED");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GalaxyRepository repo = new GalaxyRepository(DataSource.instance(DataSource.ADMIN));
		for (Galaxy g : galaxies) {
			try {
				System.out.println("PERSISTING " + g.toString());
				repo.persist(g); 
				}
			catch (Exception e) {
				System.out.println("CIAONE " + e.getMessage());
			}
		}
	}

	private static Integer parseInt(String s) throws NumberFormatException {
		if (s == null)
			return null;

		s = s.replaceAll(" ", "");
		if (s.isEmpty())
			return null;

		return Integer.parseInt(s);
	}

	private static Double parseDouble(String s) throws NumberFormatException {
		if (s == null)
			return null;

		s = s.replaceAll(" ", "");
		if (s.isEmpty())
			return null;

		return Double.parseDouble(s);
	}

	private static Boolean parseSimbol(String s, String simbol) {
		if (s == null)
			return false;
		return s.contains(simbol);
	}

	private static String[] parseAlterNames(String s) {
		String alterNames[] = s.split(", ");
		if ("".equals(parseName(alterNames[0]))) return null;
		
		for (int i = 0; i < alterNames.length; ++i) 
			alterNames[i] = parseName(alterNames[i]);
		
		return alterNames;
  }
	
	private static String parseName(String s) {
		s = s.replaceAll("\\t", "");
		while (s.length() > 0 && " ".equals(s.substring(0, 1))) 
			s = s.substring(1);
		
		while (s.length() > 0 && ' ' == s.charAt(s.length() - 1)) {
			System.out.println(s);
			s = s.substring(0, s.length() - 1);
			System.out.println(s);
		}
			
		return s;
	}
}