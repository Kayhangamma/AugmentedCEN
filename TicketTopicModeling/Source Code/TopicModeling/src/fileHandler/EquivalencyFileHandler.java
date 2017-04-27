package fileHandler;
//Code Author: Kayhan Moharreri 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import preprocessing.MyComparator;


public class EquivalencyFileHandler {

	private String EqFilePath; 
	private HashMap<String, String> parent;
	private ArrayList<String []> RHS = new ArrayList<String []>() ;
	private String[] TBR ; 
	
	
	public EquivalencyFileHandler(String eqFile) throws IOException {
	
		parent = new HashMap<String, String>();
		
		EqFilePath = eqFile;
		String [] twoSides;
		
		BufferedReader br = new BufferedReader(new FileReader(EqFilePath));
		String line;
		int lineNo = 0;
		while ((line = br.readLine()) != null) {
			
			//Line Error handler
			twoSides = line.split("=");
			if (twoSides.length>2){
				lineNo++;
				System.err.println("Error in the equivalency list, wrong use of = at line "+ lineNo );
				System.exit(0);
			}
			if (twoSides.length<2){
			
				lineNo++;
				System.err.println("Line detected without '=' , At line "+ lineNo );
				System.exit(0);
			}
			
			
			for (int i=0;i<2;i++)
			{twoSides[i] = removeSpaceFromSides(twoSides[i]);}
			String[] thisLineRHS = twoSides[1].split(",");
			for (int i=0; i<thisLineRHS.length; i++)
			{
				thisLineRHS[i] = removeSpaceFromSides(thisLineRHS[i]);
			}	
			
			
			RHS.add(thisLineRHS);
			associateParent (twoSides[0],thisLineRHS);
			lineNo++;
						
		}
		br.close();
		
		
		Object[] objectArray = parent.keySet().toArray();
		TBR = Arrays.copyOf(objectArray, objectArray.length, String[].class); //parent.keySet().toArray();
		
		ArrayList<String> TBRlist = new ArrayList<String>(Arrays.asList(TBR));
		Collections.sort (TBRlist, new MyComparator());
		TBR = TBRlist.toArray(TBR);
		
	}


	private void associateParent(String leftSide, String[] RightSide) {
		for (int i=0; i<RightSide.length;i++)
		{
			parent.put(RightSide[i], leftSide);
		}
		
	}


	private String removeSpaceFromSides(String test) {	
		// remove all the space in the start and in the end
		int i=0;
		
		//before spaces
		while( test.charAt(i)==' ' ){
			i++;
		}
		test = test.substring(i);
		
		// after spaces
		i=test.length()-1;
		while( test.charAt(i)==' ' ){
			i--;
		}
		test = test.substring(0, i+1);
		return test;
		
	}
	
	
	public String replaceByParent (String Line){
		
		String finalReplaced = Line;
		
		
		for (int index=0; index<TBR.length; index++) {			
			//Warning: punctuation marks are being ignored
			String regex = "[\\\"\\s\\.\\,\\;\\$\\=\\+\\-\\_\\)\\(\\*\\&\\^\\%\\#\\@\\!\\?\\>\\<\\:\\'\\{\\}\\[\\]\\~\\`\\d\\\\]"; 
			finalReplaced = finalReplaced.replaceAll(regex+"(?i)"+TBR[index]+regex, " "+parent.get(TBR[index])+" " );
			
			
		}
		
		//Integrity check
		if (finalReplaced.charAt(finalReplaced.length()-1)!='\"')
			finalReplaced = finalReplaced +"\"";
		if (finalReplaced.charAt(0)!='\"')
			finalReplaced = "\""+finalReplaced;
	
		return finalReplaced;
		
		
	}
	
	

}
