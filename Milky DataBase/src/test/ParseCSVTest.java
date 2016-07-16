package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import controller.ImportFileController;
import exception.ParserException;
import model.Galaxy;
import parser.AbstractCSVParser;

public class ParseCSVTest {
	private static final String paths[] = 
			new String[] { "./res/csv/MRTable3_sample.csv", 
					"./res/csv/MRTable4_flux.csv",
					"./res/csv/MRTable6_cont.csv",
					"./res/csv/MRTable8_irs.csv",
					"./res/csv/MRTable11_flux.csv" };
	private File file;
	private int count;
	public static int index[];
	public static List<Galaxy> results;
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Before
	public void initialize() {		
		Random random = new Random(new Date().getTime());
		count = random.nextInt(6);
		if (count == 5) { 
			expected.expect(ParserException.class);
			file = new File("./res/useless.csv");
			
			try {
				if (!file.exists()) 
					file.createNewFile();
			
				FileWriter writer = new FileWriter(file);
				int i = random.nextInt(20);
				++i;
				String toWrite = "";
				while (i > 0) {
					toWrite += ";";
					--i;
				}
				writer.write(toWrite);
				writer.close();
			} 
			catch (IOException e) { e.printStackTrace(); }
		}
		else 
			file = new File(paths[count]);
	}
	
	/**
	 * This test checks whether or not the application is able to find a Parser for the
	 * chosen file. CSV files are chosen randomly, with a chance of constructing a
	 * broken file, for whom is expected not to find any.
	 * @throws Exception
	 */
	@Test
	public void testImport() throws Exception {
		results = null;
		index = new int[1];
		results = ImportFileController.instance().parseResults(file, index);
		
		switch (count) {
		case 0:
			assertTrue(index[0] == AbstractCSVParser.GLXY);
			break;
		case 1: case 4:
			assertTrue(index[0] == AbstractCSVParser.PACS_LINE);
			break;
		case 2:
			assertTrue(index[0] == AbstractCSVParser.PACS_CON);
			break;
		case 3:
			assertTrue(index[0] == AbstractCSVParser.IRS);
			break;
		default: assertTrue(false);
		}
	}	
}
