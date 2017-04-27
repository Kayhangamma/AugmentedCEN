package fileHandler;
//Code Author: Kayhan Moharreri 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GroupFileHandler {

	
	private String groupFilePath;
	private ArrayList<String []> groupsOfWords = new ArrayList<String []>() ;
	private String [] mergedWords;
	private String [] mergedWordsDelimited;
	
	public GroupFileHandler(String path) throws IOException {
		groupFilePath = path;
		BufferedReader br = new BufferedReader(new FileReader(groupFilePath));
		String line;
		while ((line = br.readLine()) != null) {
			groupsOfWords.add(line.split(" "));			
		}
		br.close();
		
		
		mergedWords = new String [groupsOfWords.size()];
		mergedWordsDelimited = new String [groupsOfWords.size()];
		for (int i=0; i<groupsOfWords.size(); i++)
		{
			mergedWords[i] = merge(groupsOfWords.get(i), " "); 
			mergedWordsDelimited[i] = merge(groupsOfWords.get(i), "");
		}
		
		
	}
	
	private String merge (String groupLine[], String Delimiter)
	{
		String merged ="";
		for (int i = 0; i<groupLine.length; i++) {
			merged = merged + groupLine[i];
			if (i!=groupLine.length-1)
			{
				merged = merged + Delimiter;
			}
		}

		return merged;
		
	}
	
	public String replaceByGroup (String Line){
	
		
		String finalReplaced = Line;
		//String lineLowered = Line.toLowerCase();
		//String finalReplaced = lineLowered;
		
		//String initialGroup ="";
		//String targetCode = ""; 
		
		for (int index=0; index<mergedWords.length; index++) {
	    	
			//initialGroup = mergedWords[index].toLowerCase();
			//targetCode = mergedWordsDelimited[index].toLowerCase();
			
	        //finalReplaced = finalReplaced.replaceAll(initialGroup,targetCode);
	        
	        finalReplaced = finalReplaced.replaceAll("(?i)" + mergedWords[index], mergedWordsDelimited[index]);
	        
		}    

        /*
        int j=0;
        for (int i=0; i<Line.length(); i++ )
        {
        	if (lineLowered.charAt(i) == Character.toLowerCase(finalReplaced.charAt(j))) 
			{
        		finalReplaced = finalReplaced.substring(0, j) + Line.charAt(i)+ finalReplaced.substring(j+1); 
        		//For space issue
        		j++;
        		
			}
        	else 
        	{
        		//j++;
        		//For space issue
        		i++;
        	}
    		//j++;
        	
        	
        }
        */
    
		
		return finalReplaced;
		
	}
	
}
