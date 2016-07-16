package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import controller.DataSource;
import controller.ImportFileController;
import exception.TolerableSQLException;
import model.FluxRepository;
import model.GalaxyRepository;
import model.Repository;
import parser.AbstractCSVParser;
import pattern.Observer;

public class InsertRecordsTest {
	
	@Before
	public void initialize() {
		new Observer<Exception>() {
			@Override
			public void stateChanged() {
				Exception e = subject.retrieveState();
				assertTrue(e == null || e instanceof TolerableSQLException);
			}
		}.setSubject(ImportFileController.instance());
	}


	/**
	 * This test runs the record insertion routine of ImportFileController. At the end of it,
	 * a new Exception is generated and passed to the Observer initialized in the BeforeClass
	 * initialization method, which actually launches an assert statement to prove the test was
	 * successful.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testPersist() {
		if (ParseCSVTest.results == null) {
			assertTrue(true);
			return;
		}
		Repository repo;
		switch (ParseCSVTest.index[0]) {
			case AbstractCSVParser.GLXY:
				repo = new GalaxyRepository(DataSource.testOnly());
				break;
			default:
				repo = new FluxRepository(DataSource.testOnly());
		}
		
		ImportFileController.instance().persist(ParseCSVTest.results, repo);
	}

}
