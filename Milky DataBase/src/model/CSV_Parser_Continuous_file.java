package model;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
public class CSV_Parser_Continuous_file {
	public static void main(String[] args) {
	      String [] nextLine;
	      List<Object> ContinuousLines = new ArrayList<Object>();
	    try {
	      //csv file containing data ,CHANGE THE PATH!!!!
	      String strFile = "/home/federico/Scrivania/MRTable6_cont.csv";
	      //following constructor allows to skip the first n lines of the file (in our case, skip the header)
	      CSVReader reader = new CSVReader(new FileReader(strFile),';', CSVParser.DEFAULT_QUOTE_CHARACTER,33);
	      while ((nextLine = reader.readNext()) != null) {
        	  int ColumnNumber = 0;
        	  
        	  for (String s:nextLine){
        		  s = s.replaceAll("\\s","");
        		 
        		  //String Checker
        		  if (ColumnNumber==0||ColumnNumber==21){
        			  ContinuousLines.add(s);
        			  ColumnNumber++;
        			  //System.out.println(s);
        			  continue;}
        		  
        		 
        		  
        		  //double checker
        		  if (ColumnNumber==1||ColumnNumber==2||ColumnNumber==3||ColumnNumber==4||ColumnNumber==6||ColumnNumber==7||ColumnNumber==9||ColumnNumber==10||ColumnNumber==12||ColumnNumber==13||ColumnNumber==15||ColumnNumber==16||ColumnNumber==18||ColumnNumber==19){
        			  if(s.isEmpty()){
        				  ContinuousLines.add(s);
        				  ColumnNumber++;
        				  continue;
        			  }
        			  else {
        				  double value = Double.parseDouble(s);
        			  ContinuousLines.add(value);
        			  ColumnNumber++;
        			  //System.out.println(value);
        			  continue;}
        		  }
        		  
        		  //Flag Checker
        		  if (ColumnNumber==5||ColumnNumber==8||ColumnNumber==11||ColumnNumber==14||ColumnNumber==17){

        			  
        			  if (s.equals("<")){
        				  ContinuousLines.add(true);
        				  ColumnNumber++;
        				  //System.out.println(1);
        				  continue;
        			  }
        			  else
        			  {
        				  ContinuousLines.add(false);
        				  ColumnNumber++;
        				  //System.out.println(0);
        				  continue;
        			  }
        			  
   
        			  
        			  	}
        		  //Reference Checker
     			  if (ColumnNumber==20){
    				  ColumnNumber++;
    				  //System.out.println(0);
    				  continue;
    			  }
      }
      
    } 
          reader.close();
      }catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
			}
    for (Object a:ContinuousLines){
    	System.out.println(a);
    }
    	}
}
