package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test case for the import file routine of the application. This test is divided in two distinct parts:
 * a first run whose objective is to check if the application is able to find a parser for the submitted
 * file, and a second one whose objective is to insert records into the database. Although it can be
 * interpreted as a unit testing for the ImportFileController class methods, this test actually emulates the class
 * main behavior, which is by its nature two-phased, and checking the integrity of each step.
 * This test case maps requisites #4 and #4.1, the first being tested through the entire test and the second
 * inherently true, because Parser objects altogether skip unwanted columns. 
 * @author stg
 */
@RunWith(Suite.class)
@SuiteClasses({ ParseCSVTest.class, InsertRecordsTest.class })
public class ImportFileTestSuite { }
