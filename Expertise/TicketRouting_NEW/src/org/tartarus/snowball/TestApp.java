
package org.tartarus.snowball;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestApp {

    public static void main(String [] args) throws Throwable {	
    	
    	englishStemmer stemmer = new englishStemmer();

		String path = "C:/Users/moovas1/Desktop/Au 2015/DM/Lab Assignment/First/Analysis/";		
		List<String> bWords = Files.readAllLines(Paths.get(path + "body_words.txt") , StandardCharsets.ISO_8859_1);
		List<String> tWords = Files.readAllLines(Paths.get(path + "title_words.txt") , StandardCharsets.ISO_8859_1);
		
		HashMap<String , String> stems = new HashMap<String, String>();
		
		for(int i=0; i < bWords.size(); i++){
			String[] parts = bWords.get(i).split("\t");
			String stem  = snowballStemmer(parts[0], stemmer);
			stems.put(parts[0], stem);
		}
		
		for(int i=0; i < tWords.size(); i++){
			String[] parts = tWords.get(i).split("\t");
			if(!stems.containsKey(parts[0])){
				String stem  = snowballStemmer(parts[0], stemmer);
				stems.put(parts[0], stem);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "wordsStem.txt"));
		Iterator it = stems.entrySet().iterator();
		
		while(it.hasNext()){
			Map.Entry<String, String> entry = (Entry<String, String>) it.next();
			bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		bw.close();
		
    }
    
    public static String snowballStemmer(String word, englishStemmer stemmer){
    					
		stemmer.setCurrent(word);		  
		stemmer.stem();
		String output = stemmer.getCurrent();		
		return output;    	
		
	}
	  
}
