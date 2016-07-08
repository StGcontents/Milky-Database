package model;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class CSV_Parser_flux_file {
	public static void main(String[] args) {
	      String [] nextLine;
	      List<Object> FluxLines = new ArrayList<Object>();
	      try {
	          //csv file containing data ,CHANGE THE PATH!!!!
	          String strFile = "/home/federico/Scrivania/MRTable4_flux (copia).csv";
	          //following constructor allows to skip the first n lines of the file (in our case, skip the header)
	          CSVReader reader = new CSVReader(new FileReader(strFile),';', CSVParser.DEFAULT_QUOTE_CHARACTER,28);
	          
	          while ((nextLine = reader.readNext()) != null) {
	        	  int ColumnNumber = 0;
	        	  
	        	  for (String s:nextLine){
	        		  s = s.replaceAll("\\s","");
	        		 
	        		  //String Checker
	        		  if (ColumnNumber==0||ColumnNumber==22){
	        			  FluxLines.add(s);
	        			  ColumnNumber++;
	        			  //System.out.println(s);
	        			  continue;}
	        		  
	        		 
	        		  
	        		  //double checker
	        		  if (ColumnNumber==2||ColumnNumber==3||ColumnNumber==5||ColumnNumber==6||ColumnNumber==8||ColumnNumber==9||ColumnNumber==11||ColumnNumber==12||ColumnNumber==14||ColumnNumber==15||ColumnNumber==17||ColumnNumber==18||ColumnNumber==20||ColumnNumber==21){
	        			  if(s.isEmpty()){
	        				  FluxLines.add(s);
	        				  ColumnNumber++;
	        				  continue;
	        			  }
	        			  else {
	        				  double value = Double.parseDouble(s);
	        			  FluxLines.add(value);
	        			  ColumnNumber++;
	        			  //System.out.println(value);
	        			  continue;}
	        		  }
	        		  
	        		  //Flag Checker
	        		  if (ColumnNumber==1||ColumnNumber==4||ColumnNumber==7||ColumnNumber==10||ColumnNumber==13||ColumnNumber==16||ColumnNumber==19){
	        			  
	        			  if (s.equals("<")){
	        				  FluxLines.add(true);
	        				  ColumnNumber++;
	        				  //System.out.println(1);
	        				  continue;
	        			  }
	        			  else
	        			  {
	        				  FluxLines.add(false);
	        				  ColumnNumber++;
	        				  //System.out.println(0);
	        				  continue;
	        			  }
	        			  	}
	      }
	      
	    } 
	          reader.close();
	      }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
				}
	    for (Object a:FluxLines){
	    	System.out.println(a);
	    }
	    	}
}
