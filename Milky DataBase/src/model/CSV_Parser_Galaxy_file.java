package model;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class CSV_Parser_Galaxy_file {
  public static void main(String[] args) {
      String [] nextLine;
      List<Object> GalaxyLines = new ArrayList<Object>();
      List<Object> Galaxy_alt_names_Lines = new ArrayList<Object>();
    try {
      //csv file containing data ,CHANGE THE PATH!!!!
      String strFile = "/home/federico/Scrivania/MRTable3_sample.csv";
      //following constructor allows to skip the first n lines of the file (in our case, skip the header)
      CSVReader reader = new CSVReader(new FileReader(strFile),';', CSVParser.DEFAULT_QUOTE_CHARACTER,64);
      
      while ((nextLine = reader.readNext()) != null) {
    	  int ColumnNumber = 0;
    	  
    	  for (String s:nextLine){
			  s = s.replaceAll("\\s","");
    		  
			  if (ColumnNumber==0){
    			  GalaxyLines.add(s);
    			  Galaxy_alt_names_Lines.add(s);
    			  ColumnNumber++;
    			  //System.out.println(s);
    			  continue;}
    		  

    		  if (ColumnNumber==4){

    			  if (s.equals("+")){
    				  GalaxyLines.add(1);
    				  //System.out.println(1);
    				  ColumnNumber++;
    				  continue;
    				  }
    			  else {
    				  GalaxyLines.add(-1);
    				  //System.out.println(-1);
    				  ColumnNumber++;
    				  continue;
    			  }
    			  }
    		  
    		 //Integer Checker
    		  if (ColumnNumber==1||ColumnNumber==2||ColumnNumber==5||ColumnNumber==6){
    			 // s = s.replaceAll(" ","");
    			  if (s.equals("")){
    				  GalaxyLines.add(s);
    				  ColumnNumber++;
    				  continue;
    			  }
    			  int foo = Integer.parseInt(s);
    			  GalaxyLines.add(foo);
    			  ColumnNumber++;
    			  //System.out.println(s);
    			  continue;
    		  }
    		  
    		  //double checker
    		  if (ColumnNumber==3||ColumnNumber==7||ColumnNumber==8||ColumnNumber==9||ColumnNumber==17||ColumnNumber==19||ColumnNumber==21||ColumnNumber==22||ColumnNumber==23){
    			  s = s.replaceAll(" ","");
    			  if(s.isEmpty()){
    				  GalaxyLines.add(s);
    				  ColumnNumber++;
    				  continue;
    			  }
    			  else {
    				  double value = Double.parseDouble(s);
    			  GalaxyLines.add(value);
    			  ColumnNumber++;
    			  //System.out.println(value);
    			  continue;}
    		  }
    		  
    		  if (ColumnNumber==11){
    			  GalaxyLines.add(s);
    			  ColumnNumber++;
    			  //System.out.println(s);
    			  continue;}
    		  
    		  if (ColumnNumber==16||ColumnNumber==18||ColumnNumber==20){
    			  if (s.equals("<")){
    				  GalaxyLines.add(true);
    				  ColumnNumber++;
    				  //System.out.println(1);
    				  continue;
    			  }
    			  else
    			  {
    				  GalaxyLines.add(false);
    				  ColumnNumber++;
    				  //System.out.println(0);
    				  continue;
    			  }
    			  	}

    		  if (ColumnNumber==10||ColumnNumber==12||ColumnNumber==13||ColumnNumber==14||ColumnNumber==15){
    			  ColumnNumber++;
    			  continue;
    		  }
    		 
    		  if(ColumnNumber >=25){
    			  Galaxy_alt_names_Lines.add(s);
    			  ColumnNumber++;
    			  //System.out.println(s);
    		  }
    		  
    		 
    	  }
        
        //System.out.println("Line # " + lineNumber);

        // nextLine[] is an array of values from the line
        //System.out.println(nextLine[0]);
      }
      reader.close();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
			}
    for (Object a:GalaxyLines){
    	System.out.println(a);
    }
    
    for (Object b:Galaxy_alt_names_Lines){
    	System.out.println(b);
    }
    	}
  }