package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SearchByNameTest.class, SearchByRedshiftTest.class, SearchInRangeTest.class })
public class SearchGalaxyTestSuite {
	
}
