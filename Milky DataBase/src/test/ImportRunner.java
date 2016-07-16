package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Runner class for import testing
 * @author stg
 *
 */
public class ImportRunner {
	public static void main(String[] args) {
		for (int i = 0; i < 100; ++i) {
	      Result result = JUnitCore.runClasses(ImportFileTestSuite.class);
	      if (!result.wasSuccessful()) {
	    	  for (Failure failure : result.getFailures()) 
	    		  System.out.println(failure.getMessage());
	    	break;  
	      }
	      else System.out.println("TEST #" + i + " SUCCESSFUL");
	   }
	}
}
