package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class LoginRunner {

	public static void main(String args[]) {
		for (int i = 0; i < 10000; ++i) {
		      Result result = JUnitCore.runClasses(LoginTest.class);
		      if (!result.wasSuccessful()) {
		    	  for (Failure failure : result.getFailures()) 
		    		  System.out.println(failure.toString());
		    	break;  
		      }
		      else System.out.println("TEST #" + i + " SUCCESSFUL");
		   }
	}
}
