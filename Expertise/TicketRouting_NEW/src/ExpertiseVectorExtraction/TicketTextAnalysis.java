package ExpertiseVectorExtraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.tartarus.snowball.englishStemmer;

public class TicketTextAnalysis {
	public static int minFrequency = 50; //this is for bi-grams
	public static double N = 87000.0; //number of docs in train set!
	public static int M = 10;
	public static double ratio = 0.9;

	public static void main(String[] args) throws Exception {
		//To create feature vectors for Incidents
		//1. Unigrams (TF-IDF): 1) set the value of N (number of docs in train set), 2) extractUsefullWords, 3) createFeatureVector_uniGram
		//2. BiGrams (TF-IDF): 1) set the value of N, 2) extractUsefullBiGrams, 3) createFeatureVectorForTickets_BG_TFIDF
		//3. I also developed a language model technique, but I'm not sure that it's a good idea for creating feature vector.
		
//		getContentForTickets();
//		createExpertToResolvedTransferred();
		
//		ExtractFrequentResolversTransferrersAndCorrespondingIncidents(100);
//		filterOutTheRestOfData();
//		createTrainDevSets(true);
//		createTrainDevSets(false);
//		
//		extractUsefullWords();
//		createFeatureVectorIncidents_uniGram();
//		createFeatureVectorExperts_UniGram(true);
//		createFeatureVectorExperts_UniGram(false);
//		
//		fitLambdaForEachExpertGroup(true);
//		fitLambdaForEachExpertGroup(false);
		
//		lmBasedExpertiseIncidentSimilarity(false, true);
		
//		lmBasedExpertiseIncidentSimilarityAllPossiblePairs(false);
		
//		createFeatureVectorIncidents_uniGram(); //should be run in order to have vectors for entire set of incidents, instead of just a subset.
//		cosineBasedExpertiseIncidentSimilarityAllPossiblePairs(false);
		
//		createExpertToResolvedTransferred();
//		createTrainDevSets(true);
//		createTrainDevSets(false);
		
//		createFeatureVectorForTickets_BiGram_TFIDF();
		createFeatureVectorExperts_BiGram(true);
		createFeatureVectorExperts_BiGram(false);
	}	
	
	public static void getContentForTickets() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(path + "TRAIN 2015-03-01 to 2016-02-29 EZ Path Report.csv"));
		String line = br.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "IncidentsDescriptionTrain.txt"));
		bw.write("IncidentID\tDescription\n");
		StringBuilder incident = new StringBuilder();
		while((line= br.readLine()) != null){
			line = line.replace(",\",", "\",");

			if(line.startsWith("IM0") && line.length() > 10 && line.substring(10, 11).equals(",") && incident.toString().length() > 0){
				String[] content = incident.toString().split(",\"");
				try{
					if(incident.toString().split(",")[1].length() <= 10)
						bw.write(content[0].split(",")[0] + "\t" + content[1].replace("\t", " ") + "\n");
				}
				catch(Exception e){					
				}
				incident = new StringBuilder();				
			}			
			incident.append(line + " ");
		}
		bw.close();
	}

	public static String snowballStemmer(String word, englishStemmer stemmer){			
		stemmer.setCurrent(word);		  
		stemmer.stem();
		String output = stemmer.getCurrent();		
		return output;
	}
	
	public static void extractUsefullWords() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
//		int minFrequency = 20; //We have about 900 Expert groups and 130,000 incidents. On average, each expert group resolved about 150 tickets.
		//numbers are not useful
		//punctuation marks are not useful
		//Dates are not useful
		//Words with frequency less than ''minFrequency'' are not useful
		//Stop words are not useful
		//Stemming is required to extract important part of the words
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();
		//initialize stemmer
//		englishStemmer stemmer = new englishStemmer();
				
		for(int i=0;i<sl.size();i++){
//			String w = snowballStemmer(sl.get(i).toLowerCase(), stemmer); 
			String w = sl.get(i).toLowerCase();
			stopWords.put(w, true);
		}
		
		HashMap<String, Integer> wordFrequency = new HashMap<String, Integer>();		
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
				
		//Extract Words
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i);
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');	
			input = input.replace("'", "");
			
			input = input.replaceAll("[ ]+", " ");
			HashMap<String, Boolean> thisTicketWords = new HashMap<String, Boolean>();		
			
			String[] words = input.split(" ");
			for(int j=0;j<words.length;j++){
				String word = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
//				word = snowballStemmer(word, stemmer);
				
				if(isNumber(word) || stopWords.containsKey(word))
					continue;
								
				if(thisTicketWords.containsKey(word))
					continue;
				thisTicketWords.put(word, true);
				
				if(wordFrequency.containsKey(word))
					wordFrequency.put(word, wordFrequency.get(word) + 1);
				else
					wordFrequency.put(word, 1);
			}
		}		
		
		//Process words and print
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "usefulWords.txt"));
		bw.write("Id\tWord\tDF\n");
		Iterator it = wordFrequency.entrySet().iterator();
		int id = 1;
		while(it.hasNext()){
			Map.Entry<String, Integer> wf = (Entry<String, Integer>) it.next();
			if(wf.getValue() < minFrequency)
				continue;
			bw.write(id + "\t" + wf.getKey() + "\t" + wf.getValue() + "\n");
			id++;
		}
		bw.close();
	}
	
	public static Boolean isNumber(String input){
		try{
			double d = Double.parseDouble(input);
		}
		catch(Exception e){
			return false;
		}
		return true;
	}

	public static void createFeatureVectorIncidents_uniGram() throws IOException{		
		
//		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		
		//load useful words and assign identifier to them
		List<String> ws = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> wordDF = new HashMap<String, Integer>();
		HashMap<String, Integer> wordId = new HashMap<String, Integer>();
		for(int i=1;i<ws.size();i++){
			String[] parts = ws.get(i).split("\t");
			wordDF.put(parts[1], Integer.parseInt(parts[2]));
			wordId.put(parts[1], Integer.parseInt(parts[0]));
		}
		
		//Create feature vector for each incident
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "IncidentsUnigramVectors.txt"));
		bw.write("Incident\tVector\n");
		
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split("\t");
			String input = parts[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			
			HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
			String[] words = input.split(" ");
			for(int j=0;j<words.length;j++){
				String word = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
//				word = snowballStemmer(word, stemmer);
				
				if(!wordDF.containsKey(word))
					continue;
				
				if(termFrequency.containsKey(word))
					termFrequency.put(word, termFrequency.get(word) + 1);
				else
					termFrequency.put(word, 1);
			}

			//Create Vector
			StringBuilder sb = new StringBuilder();			
			Iterator it = termFrequency.entrySet().iterator();			
			while(it.hasNext()){
				Map.Entry<String, Integer> wf = (Entry<String, Integer>) it.next();
				int df = wordDF.get(wf.getKey());
				double tfIdfScore = Math.round((wf.getValue() * Math.log(N/df))*1000.000)/1000.000;
				sb.append("," + wordId.get(wf.getKey()) + ":" + tfIdfScore);				
			}
			if(sb.length() > 0)
				bw.write(parts[0] + "\t" + sb.toString().substring(1) + "\n");			
		}
		bw.close();
	}

	public static void extractUsefullBiGrams() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		//We have about 900 Expert groups and 130,000 incidents. On average, each expert group resolved about 150 tickets.
		//numbers are not useful
		//punctuation marks are not useful
		//Dates are not useful
		//Words with frequency less than ''minFrequency'' are not useful
		//Stop words are not useful
		//Stemming is required to extract important part of the words
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();
		//initialize stemmer
		englishStemmer stemmer = new englishStemmer();
				
		for(int i=0;i<sl.size();i++){
//			String w = snowballStemmer(sl.get(i).toLowerCase(), stemmer); 
			String w = sl.get(i).toLowerCase();
			stopWords.put(w, true);
		}
		
		HashMap<String, Integer> biGramFrequency = new HashMap<String, Integer>();		
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
				
		//Extract Words
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			HashMap<String, Boolean> thisTicketBiGrams = new HashMap<String, Boolean>();		
			
			String[] words = input.split(" ");			
			for(int j=0;j<words.length-1;j++){
				String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
//				w1 = snowballStemmer(w1, stemmer);				
				if(isNumber(w1) || stopWords.containsKey(w1))
					continue;
				boolean flag = true;
				
				for(int k=j+1;k<words.length && flag; k++){
					String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
//					w2 = snowballStemmer(w2, stemmer);				
					if(isNumber(w2) || stopWords.containsKey(w2))
						continue;			
					
					String biGram = w1 + "-" + w2;
					flag = false;
					
					if(thisTicketBiGrams.containsKey(biGram))
						continue;
					thisTicketBiGrams.put(biGram, true);									
					
					if(biGramFrequency.containsKey(biGram))
						biGramFrequency.put(biGram, biGramFrequency.get(biGram) + 1);
					else
						biGramFrequency.put(biGram, 1);
				}
			}
		}		
		
		//Process words and print
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "usefulBiGrams.txt"));
		bw.write("Id\tBiGram\tDF\n");
		Iterator it = biGramFrequency.entrySet().iterator();
		int idx = 1;
		while(it.hasNext()){
			Map.Entry<String, Integer> wf = (Entry<String, Integer>) it.next();
			if(wf.getValue() < minFrequency)
				continue;
			bw.write(idx + "\t" + wf.getKey() + "\t" + wf.getValue() + "\n");
			idx++;
		}
		bw.close();
	}

	public static void additiveBigramSmoothing_LambdaEstimation() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();	
		for(int i=0;i<sl.size();i++){ 
			String w = sl.get(i).toLowerCase();
			stopWords.put(w, true);
		}
						
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescription.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		List<String> trainIncidents = new ArrayList<>();
		List<String> testIncidents = new ArrayList<>();
		
		//specify test and train set to learn lambda
		for(int i=1;i<lines.size();i++){
			double rnd = (new Random()).nextFloat();
			if(rnd<0.8)
				trainIncidents.add(lines.get(i));
			else
				testIncidents.add(lines.get(i));
		}
		
		//Count Bi-Grams
		HashMap<String, Integer> biGramFrequency = new HashMap<String, Integer>();	
		
		for(int i=0;i<trainIncidents.size();i++){
			String input = trainIncidents.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			HashMap<String, Boolean> thisTicketBiGrams = new HashMap<String, Boolean>();		
			
			String[] words = input.split(" ");			
			for(int j=0;j<words.length-1;j++){
				String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
				if(isNumber(w1) || stopWords.containsKey(w1))
					continue;
				boolean flag = true;
				
				for(int k=j+1;k<words.length && flag; k++){
					String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
					if(isNumber(w2) || stopWords.containsKey(w2))
						continue;			
					
					String biGram = w1 + "-" + w2;
					flag = false;
					
					if(thisTicketBiGrams.containsKey(biGram))
						continue;
					thisTicketBiGrams.put(biGram, true);									
					
					if(biGramFrequency.containsKey(biGram))
						biGramFrequency.put(biGram, biGramFrequency.get(biGram) + 1);
					else
						biGramFrequency.put(biGram, 1);
				}
			}
		}	
		
		//Count combination for the first part of BiGram
		HashMap<String, Integer> combinationsForFirstPartOfBiGram = new HashMap<String, Integer>();
		Iterator it = biGramFrequency.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Integer> bg = (Entry<String, Integer>) it.next();
			if(bg.getValue() < minFrequency)
				continue;
			String[] parts = bg.getKey().split("-");
			if(combinationsForFirstPartOfBiGram.containsKey(parts[0]))
				combinationsForFirstPartOfBiGram.put(parts[0], combinationsForFirstPartOfBiGram.get(parts[0])+bg.getValue());
			else
				combinationsForFirstPartOfBiGram.put(parts[0], bg.getValue());
		}
		
		//Use test set and obtained BiGrams frequency to find the best alpha value
		double maxLogProb = -1*Double.MAX_VALUE;
		double bestLambda = -1;
		for (double lambda = 0.0005; lambda < 0.0009; lambda+=0.00001){
//		for (double lambda = 1; lambda < 5; lambda+=1){
						
			double logProb = 0;
			for(int i=0;i<testIncidents.size();i++){
				
				String input = testIncidents.get(i).split("\t")[1];
				for(int j=0;j<puncs.length;j++)
					input = input.replace(puncs[j], ' ');
				input = input.replace("'", "");
				input = input.replaceAll("[ ]+", " ");
				
				String[] words = input.split(" ");			
				for(int j=0;j<words.length-1;j++){
					String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
					if(isNumber(w1) || stopWords.containsKey(w1))
						continue;
					boolean flag = true;
					
					for(int k=j+1;k<words.length && flag; k++){
						String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
						if(isNumber(w2) || stopWords.containsKey(w2))
							continue;			
						
						String biGram = w1 + "-" + w2;
						flag = false;
					
						double freq = 0;						
						if(biGramFrequency.containsKey(biGram) && biGramFrequency.get(biGram)>=minFrequency)
							freq = biGramFrequency.get(biGram);
						double firstPartFreq = 0;
						if(combinationsForFirstPartOfBiGram.containsKey(w1))
							firstPartFreq = combinationsForFirstPartOfBiGram.get(w1);
						logProb += Math.log10((freq + lambda)/(firstPartFreq + lambda*biGramFrequency.size()));
					}
				}
				
			}
			
			System.out.println("For lambda: " + lambda + " the prob is: " + logProb);
			if(logProb > maxLogProb){
				maxLogProb = logProb;
				bestLambda = lambda;
			}
			
		}
		
		System.out.println("Best Lambda: " + bestLambda);
		
		//final result: with min frequency as 10, Best Lambda: 7.000000000000005E-4 or simply 0.0007
	}

	public static void createFeatureVectorForTickets_LM() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
				
		//lambda: smoothing parameter
		double lambda = 0.0007;
		
		//Load bi-grams with indexes
		List<String> bs = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		HashMap<String, Integer> biGrams = new HashMap<String, Integer>();
		HashMap<String, Integer> combinationsForFirstPartOfBiGram = new HashMap<String, Integer>();		
		for(int i=1;i<bs.size();i++){
			String[] parts = bs.get(i).split("\t");
			biGrams.put(parts[1], Integer.parseInt(parts[0]));
			String[] _parts = parts[1].split("-");
			
			if(combinationsForFirstPartOfBiGram.containsKey(_parts[0]))
				combinationsForFirstPartOfBiGram.put(_parts[0], 
						combinationsForFirstPartOfBiGram.get(_parts[0])+Integer.parseInt(parts[2]));
			else
				combinationsForFirstPartOfBiGram.put(_parts[0], Integer.parseInt(parts[2]));
		}
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();
		for(int i=0;i<sl.size();i++){
			String w = sl.get(i).toLowerCase(); 
			stopWords.put(w, true);
		}
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescription_sample.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		BufferedWriter bw = new  BufferedWriter(new FileWriter(path + "IncidentsBigramVectors.txt"));
		
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');	
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			HashMap<String, Integer> thisTicketBiGrams = new HashMap<String, Integer>();		
			
			String[] words = input.split(" ");			
			for(int j=0;j<words.length-1;j++){
				String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
				if(isNumber(w1) || stopWords.containsKey(w1))
					continue;
				boolean flag = true;
				
				for(int k=j+1;k<words.length && flag; k++){
					String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
					if(isNumber(w2) || stopWords.containsKey(w2))
						continue;			
					
					String biGram = w1 + "-" + w2;
					flag = false;
					
					if(thisTicketBiGrams.containsKey(biGram))
						thisTicketBiGrams.put(biGram, thisTicketBiGrams.get(biGram)+1);
					thisTicketBiGrams.put(biGram, 1);
				}
			}
			
			Iterator it = thisTicketBiGrams.entrySet().iterator();
			StringBuilder vector = new StringBuilder();
			vector.append(lines.get(i).split("\t")[0]);
			while(it.hasNext()){
				Map.Entry<String, Integer> bg = (Entry<String, Integer>) it.next();				
				double freq = 0;
				int id = 0;
				if(biGrams.containsKey(bg.getKey())){
					freq = bg.getValue();
					id = biGrams.get(bg.getKey());
				}
				double firstPartFreq = 0;
				String[] w = bg.getKey().split("-");
				if(combinationsForFirstPartOfBiGram.containsKey(w[0]))
					firstPartFreq = combinationsForFirstPartOfBiGram.get(w[0]);
				double v = (freq + lambda)/(firstPartFreq + lambda*(biGrams.size()+1));//the +1 is for <UNK>
				v = Math.round(v*1000.000)/1000.000;
				vector.append("\t" + id + ":" + v);
			}
			bw.write(vector.toString() + "\n");
		}
		
		bw.close();		
	}

	public static void createFeatureVectorForTickets_BiGram_TFIDF() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";

		//Load bi-grams with indexes
		List<String> bs = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		HashMap<String, String> biGrams = new HashMap<String, String>();	
		for(int i=1;i<bs.size();i++){
			String[] parts = bs.get(i).split("\t");
			biGrams.put(parts[1], parts[0]+"\t"+parts[2]);
		}
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();
		for(int i=0;i<sl.size();i++){
			String w = sl.get(i).toLowerCase(); 
			stopWords.put(w, true);
		}
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		BufferedWriter bw = new  BufferedWriter(new FileWriter(path + "IncidentsBigramVectors.txt"));
		bw.write("Incident\tVector\n");
		
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');	
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			HashMap<String, Integer> thisTicketBiGrams = new HashMap<String, Integer>();		
			
			String[] words = input.split(" ");			
			for(int j=0;j<words.length-1;j++){
				String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
				if(isNumber(w1) || stopWords.containsKey(w1))
					continue;
				boolean flag = true;
				
				for(int k=j+1;k<words.length && flag; k++){
					String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
					if(isNumber(w2) || stopWords.containsKey(w2))
						continue;			
					
					String biGram = w1 + "-" + w2;
					flag = false;
					
					if(thisTicketBiGrams.containsKey(biGram))
						thisTicketBiGrams.put(biGram, thisTicketBiGrams.get(biGram)+1);
					thisTicketBiGrams.put(biGram, 1);
				}
			}
			
			Iterator it = thisTicketBiGrams.entrySet().iterator();
			StringBuilder vector = new StringBuilder();			
			while(it.hasNext()){
				Map.Entry<String, Integer> bg = (Entry<String, Integer>) it.next();				
				
				if(biGrams.containsKey(bg.getKey())){
					double tf = bg.getValue();
					int id = Integer.parseInt(biGrams.get(bg.getKey()).split("\t")[0]);
					double idf = Double.parseDouble(biGrams.get(bg.getKey()).split("\t")[1]); 
					double v = tf * Math.log(N/idf);
					v = Math.round(v*1000.000)/1000.000;
					vector.append("," + id + ":" + v);
				}
			}
			if(vector.length() > 0)
				bw.write(lines.get(i).split("\t")[0] + "\t" + vector.toString().substring(1) + "\n");
		}
		
		bw.close();		
	}

	public static void createExpertToResolvedTransferred() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		List<String> lines = Files.readAllLines(Paths.get(path + "Transfer Time Intervals 2015-03-01 to 2016-02-29_train.csv"));
		
		HashMap<String, HashMap<String, Boolean>> resolved = new HashMap<>();
		HashMap<String, HashMap<String, Boolean>> transferred = new HashMap<>();
		
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split(",");
			//Incident ID,Transfer Group,Start,End,Elapsed Time,Effective SL Time,Type
			
			if(parts[6].equals("Resolution")){
				HashMap<String, Boolean> rs = new HashMap<>();
				if(resolved.containsKey(parts[1]))
					rs = resolved.get(parts[1]);
				rs.put(parts[0], true);
				resolved.put(parts[1], rs);
			}
			
			else if(parts[6].equals("Progress")){
				HashMap<String, Boolean> pr = new HashMap<>();
				if(transferred.containsKey(parts[1]))
					pr = transferred.get(parts[1]);
				pr.put(parts[0], true);
				transferred.put(parts[1], pr);
			}
				
		}
		
		//write expert to resolved
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "ExpertToResolved.txt"));
		bw.write("Group\tResolvedIncidents\n");
		Iterator it = resolved.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> g = (Entry<String, HashMap<String, Boolean>>) it.next();
			StringBuilder sb = new StringBuilder();
			Iterator it2 = g.getValue().entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				sb.append("," + t.getKey());
			}
			bw.write(g.getKey() + "\t" + sb.toString().substring(1) + "\n");
		}
		bw.close();
		
		
		//write expert to transferred
		bw = new BufferedWriter(new FileWriter(path + "ExpertToTransferred.txt"));
		bw.write("Group\tTransferredIncidents\n");
		it = transferred.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> g = (Entry<String, HashMap<String, Boolean>>) it.next();
			StringBuilder sb = new StringBuilder();
			Iterator it2 = g.getValue().entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				sb.append("," + t.getKey());
			}
			bw.write(g.getKey() + "\t" + sb.toString().substring(1) + "\n");
		}
		bw.close();
	}
			
	public static void createFeatureVectorExperts_UniGram(boolean ResolveBased) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		HashMap<String, String> incidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts  = lines.get(i).split("\t");
			incidents.put(parts[0], parts[1]);
		}
		
		//load useful words and assign identifier to them
		List<String> ws = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> wordDF = new HashMap<String, Integer>(); //A better approach was to first specify a dev set, then count the word and inverse word frequencies. 
		HashMap<String, Integer> wordId = new HashMap<String, Integer>();
		for(int i=1;i<ws.size();i++){
			String[] parts = ws.get(i).split("\t");
			wordDF.put(parts[1], Integer.parseInt(parts[2]));
			wordId.put(parts[1], Integer.parseInt(parts[0]));
		}
		
		//Load list of incidents for each expert
		if(ResolveBased)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		
		HashMap<String, HashMap<String, Character>> expertToIncidents = new HashMap<>();
		for(int i=0;i<lines.size();i++){
			String exp = lines.get(i).split("\t")[0];
			String[] ts = lines.get(i).split("\t")[1].split(",");
			HashMap<String, Character> rsv = new HashMap<>();
			for(int j=0;j<ts.length;j++){
				String[] t_i = ts[j].split("=");
				rsv.put(t_i[0], t_i[1].charAt(0));
			}
			expertToIncidents.put(exp, rsv);
		}
		
		//Create feature vector for each expert
		String output_path = path + "ExpertsUnigramVectors";
		if(ResolveBased)
			output_path += "_ResolveBased.txt";
		else 
			output_path += "_TransferredBased.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(output_path));
		bw.write("ExpertGroup\tUniGramVector\n");
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
				
		Iterator it = expertToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Character>> g = (Entry<String, HashMap<String, Character>>) it.next();
			Iterator it2 = g.getValue().entrySet().iterator();
			HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
			
			while(it2.hasNext()){
				Map.Entry<String, Character> t = (Entry<String, Character>) it2.next();
				if(t.getValue() == 'D') //means this incident belongs to dev set!
					continue;
				
				if(!incidents.containsKey(t.getKey()))
					continue;
				String input = incidents.get(t.getKey());
				
				for(int j=0;j<puncs.length;j++)
					input = input.replace(puncs[j], ' ');		
				input = input.replace("'", "");
				input = input.replaceAll("[ ]+", " ");
				String[] words = input.split(" ");
				
				for(int j=0;j<words.length;j++){
					String word = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");					
					if(!wordDF.containsKey(word))
						continue;					
					if(termFrequency.containsKey(word))
						termFrequency.put(word, termFrequency.get(word) + 1);
					else
						termFrequency.put(word, 1);
				}
			}
			
			//Create Vector
			StringBuilder sb = new StringBuilder();
			
			it2 = termFrequency.entrySet().iterator();			
			while(it2.hasNext()){
				Map.Entry<String, Integer> wf = (Entry<String, Integer>) it2.next();
				int df = wordDF.get(wf.getKey());
				double tfIdfScore = Math.round((wf.getValue() * Math.log(N/df))*1000.000)/1000.000;
				//Here, N and df are estimation of expected values. Basically, we are creating pseudo docs based on all resolved incidents
				// by an expert. So, the N should be #Experts and df should be frequency of the word which is appeared in pseudo docs. 
				sb.append("," + wordId.get(wf.getKey()) + ":" + tfIdfScore);				
			}
			if(sb.length() > 0)
				bw.write(g.getKey() + "\t" + sb.toString().substring(1) + "\n");	
		}
		
		bw.close();
	}

	public static void createFeatureVectorExperts_BiGram(boolean ResolveBased) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		HashMap<String, String> incidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts  = lines.get(i).split("\t");
			incidents.put(parts[0], parts[1]);
		}
		
		//Load bi-grams with indexes
		List<String> bs = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		HashMap<String, String> biGrams = new HashMap<String, String>();	
		for(int i=1;i<bs.size();i++){
			String[] parts = bs.get(i).split("\t");
			biGrams.put(parts[1], parts[0]+"\t"+parts[2]);
		}
		
		//Load stop words
		List<String> sl = Files.readAllLines(Paths.get(path + "generic stop words.txt"));
		HashMap<String, Boolean> stopWords = new HashMap<String, Boolean>();
		for(int i=0;i<sl.size();i++){
			String w = sl.get(i).toLowerCase(); 
			stopWords.put(w, true);
		}

		//Load list of resolved incidents for each expert
		if(ResolveBased)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		
		HashMap<String, HashMap<String, Boolean>> expertToResolved = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String exp = lines.get(i).split("\t")[0];
			String[] ts = lines.get(i).split("\t")[1].split(",");
			HashMap<String, Boolean> rsv = new HashMap<>();
			for(int j=0;j<ts.length;j++)
				rsv.put(ts[j], true);
			expertToResolved.put(exp, rsv);
		}
		
		//Create feature vector for each expert
		String output_path = path + "ExpertsBiGramVectors";		
		if(ResolveBased)
			output_path += "_ResolveBased.txt";
		else
			output_path += "_TransferredBased.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(output_path));
		
		bw.write("ExpertGroup\tBiGramVector\n");
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
				
		Iterator it = expertToResolved.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> g = (Entry<String, HashMap<String, Boolean>>) it.next();
			Iterator it2 = g.getValue().entrySet().iterator();
			HashMap<String, Integer> thisExpertBiGrams = new HashMap<String, Integer>();
			
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				
				if(!incidents.containsKey(t.getKey()))
					continue;
				
				String input = incidents.get(t.getKey());
				for(int j=0;j<puncs.length;j++)
					input = input.replace(puncs[j], ' ');	
				input = input.replace("'", "");
				input = input.replaceAll("[ ]+", " ");						
				
				String[] words = input.split(" ");			
				for(int j=0;j<words.length-1;j++){
					String w1 = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
					if(isNumber(w1) || stopWords.containsKey(w1))
						continue;
					boolean flag = true;
					
					for(int k=j+1;k<words.length && flag; k++){
						String w2 = words[k].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
						if(isNumber(w2) || stopWords.containsKey(w2))
							continue;			
						
						String biGram = w1 + "-" + w2;
						flag = false;
						
						if(thisExpertBiGrams.containsKey(biGram))
							thisExpertBiGrams.put(biGram, thisExpertBiGrams.get(biGram)+1);
						thisExpertBiGrams.put(biGram, 1);
					}
				}
			}
			
			it2 = thisExpertBiGrams.entrySet().iterator();
			StringBuilder vector = new StringBuilder();
			while(it2.hasNext()){
				Map.Entry<String, Integer> bg = (Entry<String, Integer>) it2.next();				
				
				if(biGrams.containsKey(bg.getKey())){
					double tf = bg.getValue();
					int id = Integer.parseInt(biGrams.get(bg.getKey()).split("\t")[0]);
					double idf = Double.parseDouble(biGrams.get(bg.getKey()).split("\t")[1]); 
					double v = tf * Math.log(N/idf);
					v = Math.round(v*1000.000)/1000.000;
					vector.append("," + id + ":" + v);
				}
			}
			if(vector.length() > 0)
				bw.write(g.getKey() + "\t" + vector.toString().substring(1) + "\n");	
		}
		
		bw.close();
	}

	public static void getPathForTickets() throws IOException{
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/TrainTestRandomSplit/";
		BufferedReader br = new BufferedReader(new FileReader(path + "Test 2015-03-01 to 2016-02-29 EZ Path Report.csv"));
		String line = br.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "Path Report_Test.csv"));
		bw.write("Incident ID,Priority,Owning Group,Assignment Group,Closed Group,Path\n");
		HashMap<String, Integer> priorities = new HashMap<String, Integer>();
		StringBuilder incident = new StringBuilder();
		while((line= br.readLine()) != null){
			line = line.replace(",\",", "\",");
			if(line.startsWith("IM0") && incident.toString().length() > 0){
				String[] content = incident.toString().split(",\"");
				String filteredContent = "";
				for(int i=0;i<content.length;i++){
					if(content[i].contains("\"")){
						String[] ps = content[i].split("\"");
						if(ps.length > 1)
							filteredContent += ",NULL" + ps[1];
						else
							filteredContent += ",NULL";
					}
					else
						filteredContent += content[i];
				}
				String[] parts = filteredContent.split(",");
				if(parts.length == 30){					
					bw.write(parts[0] + "," + parts[7] + "," + parts[9] + "," + parts[10] + "," + parts[11] + "," + parts[26] + "\n");
					if(priorities.containsKey(parts[7]))
						priorities.put(parts[7], priorities.get(parts[7]) + 1);
					else 
						priorities.put(parts[7], 1);
				}
				incident = new StringBuilder();				
			}
			incident.append(line);
		}
		bw.close();
		
		System.out.println(priorities);
		
	}
	
	public static void createTrainDevSets(boolean resolvedBase) throws IOException{
		//M: the minimum number of incidents that an expert should had transferred/resolved to use part of it's incidents as dev set
		//ratio: shows the ration of data which will be used for train. The rest will be used for dev
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		List<String> experts;
		if(resolvedBase)
			experts = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		else
			experts = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		
		HashMap<String, ArrayList<String>> expert_icident = new HashMap<>();		
		for(String line:experts){
			if(line.startsWith("Group"))
				continue;
			String exp = line.split("\t")[0];
			String[] incidents = line.split("\t")[1].split(",");
			ArrayList<String> ticketList = new ArrayList<>();
			
			if(incidents.length < M){				
				for(String t:incidents)
					ticketList.add(t + "=T");				
			}
			
			else{
				for(String t:incidents){
					double prob = (new Random()).nextDouble();
					if(prob <= ratio)
						ticketList.add(t + "=T");
					else
						ticketList.add(t + "=D");
				}
			}
			
			expert_icident.put(exp, ticketList);
		}
		
		//writing ExpertToResolved with Train and Dev specified into the file
		BufferedWriter bw;
		if(resolvedBase)
			bw = new BufferedWriter(new FileWriter(path + "ExpertToResolved_TD.txt"));
		else
			bw = new BufferedWriter(new FileWriter(path + "ExpertToTransferred_TD.txt"));
		
		Set<String> keys = expert_icident.keySet();
		for(String exp:keys){
			String row = exp + "\t" + (expert_icident.get(exp).toString()).replace("[", "").replace("]","").replace(" ", "");
			bw.write(row + "\n");
		}
		bw.close();
		
	}

	public static void ExtractFrequentResolversTransferrersAndCorrespondingIncidents(int n) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved data/";
		List<String> lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		HashSet<String> selectedIncidents = new HashSet<>();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "frequentResolvers.txt"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(path + "selectedIncidents.txt"));
		
		//Resolve Based
		for(String ln:lines){
			String[] parts = ln.split("\t")[1].split(",");
			if(parts.length >= n){
				bw.write(ln.split("\t")[0] + "\n");
				for(String p:parts){
					String t = p.split("=")[0];
					if(!selectedIncidents.contains(t)){
						bw2.write(t + "\n");
						selectedIncidents.add(t);
					}
				}
			}
		}		
		bw.close();
		
		//Transfer Based
		bw = new BufferedWriter(new FileWriter(path + "frequentTransferers.txt"));
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		for(String ln:lines){
			String[] parts = ln.split("\t")[1].split(",");
			if(parts.length >= n){
				bw.write(ln.split("\t")[0] + "\n");
				for(String p:parts){
					String t = p.split("=")[0];
					if(!selectedIncidents.contains(t)){
						bw2.write(t + "\n");
						selectedIncidents.add(t);
					}
				}
			}
		}		
		bw.close();		
		bw2.close();
	}

	public static void filterOutTheRestOfData() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
		
		//1. load frequent resolvers
		HashSet<String> frequentResolvers = new HashSet<>();
		List<String> lines = Files.readAllLines(Paths.get(path + "Improved Data/frequentResolvers.txt"));
		for(String ln:lines)
			frequentResolvers.add(ln);
		
		//2. Load frequent transferrers 
		HashSet<String> frequentTransferers = new HashSet<>();
		lines = Files.readAllLines(Paths.get(path + "Improved Data/frequentTransferers.txt"));
		for(String ln:lines)
			frequentTransferers.add(ln);
		
		//3. 
		HashSet<String> selectedIncidents = new HashSet<>();
		lines = Files.readAllLines(Paths.get(path + "Improved Data/selectedIncidents.txt"));
		for(String ln:lines)
			selectedIncidents.add(ln);
		
		//4. Filter out Expert-To-Resolve
		lines = Files.readAllLines(Paths.get(path + "Improved Data/ExpertToResolved.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "Improved Data/ExpertToResolved.txt"));
		for(String ln:lines)
			if(frequentResolvers.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
		
		//5. Filter out Expert-To-Transfer
		lines = Files.readAllLines(Paths.get(path + "Improved Data/ExpertToTransferred.txt"));
		bw = new BufferedWriter(new FileWriter(path + "Improved Data/ExpertToTransferred.txt"));
		for(String ln:lines)
			if(frequentTransferers.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
		
		//6. Filter incident description
		lines = Files.readAllLines(Paths.get(path + "Improved Data/IncidentsDescriptionTrain.txt"));
//		bw = new BufferedWriter(new FileWriter(path + "Improved Data/IncidentsDescriptionTrain.txt"));
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + "Improved Data/IncidentsDescriptionTrain.txt"), "UTF-8"));
		out.write("IncidentID\tDescription\n");
		for(String ln:lines)
			if(selectedIncidents.contains(ln.split("\t")[0]))
				out.write(ln + "\n");
		out.close();
		
	}
	
	public static HashMap<String, HashMap<String, Boolean>> loadExpertToTransferOrResolveData(String path, 
			boolean resolvedBased) throws IOException{
		//load expert to resolve/transfer data
		HashMap<String, HashMap<String, Boolean>> experts = new HashMap<>();
		List<String> lines;
		if(resolvedBased)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		
		for(String ln:lines){
			String[] parts = ln.split("\t")[1].split(",");
			HashMap<String, Boolean> incidents = new HashMap<>();
			for(String p:parts)
				incidents.put(p.split("=")[0], (p.split("=")[1].equals("T")));
			experts.put(ln.split("\t")[0], incidents);
		}
		
		return experts;
	}
	
	public static void fitLambdaForEachExpertGroup(boolean ResolvedBase) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		//Load useful words
		List<String> lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashSet<String> usefulWords = new HashSet<>();	
		for(int i=1;i<lines.size();i++){ 
			String[] parts = lines.get(i).split("\t");
			usefulWords.add(parts[1]);
		}
						
		lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		HashMap<String, String> incidents = new HashMap<>();
		//specify test and train set to learn lambda
		for(int i=1;i<lines.size();i++){
			String ln = lines.get(i).split("\t")[1];
			ln = ln.toLowerCase();
			for(char pnc:puncs)
				ln = ln.replace(pnc, ' ');
			ln = ln.replace("'", "");
			ln = ln.replaceAll("[ ]+", " ");
			String[] parts = ln.split(" ");
			ln = "";
			for(String prt:parts)
				if(usefulWords.contains(prt))
					ln += " " + prt;
			if(ln.length() == 0)
				continue;
			ln = ln.substring(1);
			incidents.put(lines.get(i).split("\t")[0], ln);
		}
		
		//load expert to Resolve/Transfer and then create Train and Dev set to fit Lambda values
		HashMap<String, HashMap<String, Boolean>> experts = new HashMap<>();
		if(ResolvedBase)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		
		for(String ln:lines){
			String[] parts = ln.split("\t")[1].split(",");
			HashMap<String, Boolean> inc = new HashMap<>();
			for(int i=0;i<parts.length;i++){
				String[] prt = parts[i].split("=");
				if(prt[1].equals("D"))
					continue;
				double rnd = (new Random()).nextDouble();
				if(rnd < 0.8)
					inc.put(prt[0], true); //use this sample as a train sample to fit the lambda for corresponding expert
				else
					inc.put(prt[0], false);	//use this sample as a dev sample to fit the lambda for corresponding expert
			}
			experts.put(ln.split("\t")[0], inc);
		}
		
		//Process of Lambda fitting for each expert group
		if(ResolvedBase)
			path = path + "bestLambda_ResolvedBase.txt";
		else
			path = path + "bestLambda_TransferredBase.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		
		double down = 0.0001, up = 0.099, step = 0.0001;
		 
		Set<String> groups = experts.keySet();
		for(String exp:groups){
			
			Set<String> tickets = experts.get(exp).keySet();
			
			//Learn biGram values
			HashMap<String, Integer> biGramFrequency = new HashMap<String, Integer>();
			HashMap<String, Integer> uniGramFrequency = new HashMap<String, Integer>();
			for(String t:tickets)
				if(experts.get(exp).get(t) && incidents.containsKey(t)){ //means this is a train sample
					String[] words = incidents.get(t).split(" ");
					for(int i=0;i<words.length-1;i++){
						//update bigram
						String bg = words[i] + "$" + words[i+1];
						if(biGramFrequency.containsKey(bg))
							biGramFrequency.put(bg, biGramFrequency.get(bg)+1);
						else
							biGramFrequency.put(bg, 1);
						//update unigram
						if(uniGramFrequency.containsKey(words[i]))
							uniGramFrequency.put(words[i], uniGramFrequency.get(words[i])+1);
						else
							uniGramFrequency.put(words[i], 1);
					}
				}
			
			//Estimate lambda
			double bestLambda = 0, maxLogLikelihood = -1*Double.MAX_VALUE;
			for(double lm=down;lm<=up;lm+=step){
				
				double logLike = 0;
				for(String t:tickets)
					if(!experts.get(exp).get(t) && incidents.containsKey(t)){ //means this is a dev sample
						String[] words = incidents.get(t).split(" ");
						
						double ll = 0;
						int len = 0;
						for(int i=0;i<words.length-1;i++){
							String bg = words[i] + "$" + words[i+1];
							double biFreq = 0;
							if(biGramFrequency.containsKey(bg))
								biFreq = (double) biGramFrequency.get(bg);
							double uniFreq = 0;
							if(uniGramFrequency.containsKey(words[i]))
								uniFreq = (double) uniGramFrequency.get(words[i]);
//							logLike += (Math.log10((biFreq + lambda)/(uniFreq + lambda*bigrams.size())));
							ll += Math.log10((biFreq + lm)/(uniFreq + lm*biGramFrequency.size()));	
							len++;
						}
						if(ll == 0)
							continue;
						ll = (1.0/len)*ll;
						logLike += ll;
					}
				
				if(logLike > maxLogLikelihood){
					bestLambda = lm;
					maxLogLikelihood = logLike;
				}
			}
			
			bw.write(exp + "\t" + bestLambda + "\n");
			System.out.println(exp + ": " + bestLambda);
		}
		
		bw.close();
		System.out.println();
		
	}
	
	public static void lmBasedExpertiseIncidentSimilarity(boolean resolvedBased, boolean inverse) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		//load expert to resolve/transfer data
		HashMap<String, HashMap<String, Boolean>> experts = loadExpertToTransferOrResolveData(path, resolvedBased);
		
		
		//load useful words
		List<String> lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> usefulWords = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split("\t");
			usefulWords.put(parts[1], Integer.parseInt(parts[0]));
		}
		
		//load incidents path length
		lines = Files.readAllLines(Paths.get(path + "TrainIncidents_Length_Path.txt"));
		HashMap<String, Integer> pathLength = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split("\t");
			pathLength.put(parts[0], Integer.parseInt(parts[1]));
		}
		
		//load incident data
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));		
		HashMap<String, String> incidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');	
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			String[] parts = input.split(" ");
			StringBuilder sb = new StringBuilder();
			for(String p:parts){
				p = p.toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
				if(usefulWords.containsKey(p))
					sb.append(" " + p);
			}
			if(sb.toString().length() > 0)
				incidents.put(lines.get(i).split("\t")[0], sb.toString().substring(1));
		}
		
		//load best lambda values
		if(resolvedBased)
			lines = Files.readAllLines(Paths.get(path + "bestLambda_ResolvedBase.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "bestLambda_TransferredBase.txt"));
		HashMap<String, Double> bestLambda = new HashMap<>();
		for(String ln:lines)
			bestLambda.put(ln.split("\t")[0], Double.parseDouble(ln.split("\t")[1]));
		
		//Building Language Model for each expert
		HashMap<String, HashMap<String, Integer>> bigrams = new HashMap<>();
		HashMap<String, HashMap<String, Integer>> unigrams = new HashMap<>();
		
		Set<String> exps = experts.keySet();
		for(String e:exps){
			HashMap<String, Integer> biGramVec = new HashMap<>();
			HashMap<String, Integer> uniGramVec = new HashMap<>();
			HashMap<String, Boolean> incs = experts.get(e);
			Set<String> tickets = incs.keySet();
			for(String t:tickets)
				if(incidents.containsKey(t) && incs.get(t)){
					String[] words = incidents.get(t).split(" ");
					//for unigram
					for(int i=0;i<words.length-1;i++){
						String ug = words[i];
						if(uniGramVec.containsKey(ug))
							uniGramVec.put(ug, uniGramVec.get(ug)+1);
						else
							uniGramVec.put(ug, 1);
					}
					//for bigram
					for(int i=0;i<words.length-1;i++){
						String bg = usefulWords.get(words[i]) + "$" + usefulWords.get(words[i+1]);
						if(biGramVec.containsKey(bg))
							biGramVec.put(bg, biGramVec.get(bg)+1);
						else
							biGramVec.put(bg, 1);
					}
				}
			bigrams.put(e, biGramVec);
			unigrams.put(e, uniGramVec);
		}
		
		
		//Reload Expert Data
		experts = loadExpertToTransferOrResolveData(path, inverse);
		
		BufferedWriter bw;
		if(resolvedBased && inverse)
			bw = new BufferedWriter(new FileWriter("ExpertiseByResolve_LM_resolved.csv"));
		else if(resolvedBased && !inverse)
			bw = new BufferedWriter(new FileWriter("ExpertiseByResolve_LM_transferred.csv"));
		else if(!resolvedBased && inverse)
			bw = new BufferedWriter(new FileWriter("ExpertiseByTransfer_LM_resolved.csv"));
		else
			bw = new BufferedWriter(new FileWriter("ExpertiseByTransfer_LM_transferred.csv"));
		
		bw.write("Similarity,GroupId,Incident,PathLength\n");
		
		//Calculate the similarity between a dev incident and corresponding expert's language model
		double totalLL=0, count=0, total=0;
		double lambda = 0.001;
		exps = experts.keySet();
		for(String e:exps){
			if(!unigrams.containsKey(e)) //Then, don't have any expertise data for this expert
				continue;
			if(bestLambda.containsKey(e))
				lambda = bestLambda.get(e);
			
			HashMap<String, Integer> biGramVec = bigrams.get(e);
			HashMap<String, Integer> uniGramVec = unigrams.get(e);
			HashMap<String, Boolean> incs = experts.get(e);
			Set<String> tickets = incs.keySet();
			//Now, calculate the log likelihood of probability
			double logLike = 0;			
			for(String t:tickets)
				if(incidents.containsKey(t) && !incs.get(t)){ //a dev incident					
					String[] words = incidents.get(t).split(" ");
					double ll = 0;
					int len = 0;
					for(int i=0;i<words.length-1;i++){
						String bg = usefulWords.get(words[i]) + "$" + usefulWords.get(words[i+1]);
						double biFreq = 0;
						if(biGramVec.containsKey(bg))
							biFreq = (double) biGramVec.get(bg);
						double uniFreq = 0;
						if(uniGramVec.containsKey(words[i]))
							uniFreq = (double) uniGramVec.get(words[i]);
//						logLike += (Math.log10((biFreq + lambda)/(uniFreq + lambda*bigrams.size())));
						ll += Math.log10((biFreq + lambda)/(uniFreq + lambda*bigrams.size()));	
						len++;
					}
					if(ll == 0)
						continue;
					ll = -(1.0/len)*ll;
					//incident resolve path length
					int pl = 0;
					if(pathLength.containsKey(t))
						pl = pathLength.get(t);
//					bw.write(ll + "," + e + "," + t + "," + pl + "\n");
					bw.write(ll + "," + e + "," + t + "\n");
					logLike += ll;
					total ++;
				}	
			if(logLike != 0){
				totalLL += logLike;
				count += 1;
//				System.out.println(e + ": " + logLike/tickets.size());
			}
		}
		
		bw.close();
		System.out.println(totalLL/total + " for: " + count + " expert group!");
	}

	public static void lmBasedExpertiseIncidentSimilarityAllPossiblePairs(boolean resolvedBased) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Entire Data/Improved Data/";
		
		//load expert to resolve/transfer data
		HashMap<String, HashMap<String, Boolean>> experts = loadExpertToTransferOrResolveData(path, resolvedBased);		
		
		//load useful words
		List<String> lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> usefulWords = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split("\t");
			usefulWords.put(parts[1], Integer.parseInt(parts[0]));
		}
			
		//load incident data
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		lines = Files.readAllLines(Paths.get(path + "IncidentsDescription.txt"));		
		HashMap<String, String> incidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String input = lines.get(i).split("\t")[1];
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');	
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			String[] parts = input.split(" ");
			StringBuilder sb = new StringBuilder();
			for(String p:parts){
				p = p.toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
				if(usefulWords.containsKey(p))
					sb.append(" " + p);
			}
			if(sb.toString().length() > 0)
				incidents.put(lines.get(i).split("\t")[0], sb.toString().substring(1));
		}
		
		//load best lambda values
		if(resolvedBased)
			lines = Files.readAllLines(Paths.get(path + "bestLambda_ResolvedBase.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "bestLambda_TransferredBase.txt"));
		HashMap<String, Double> bestLambda = new HashMap<>();
		for(String ln:lines)
			bestLambda.put(ln.split("\t")[0], Double.parseDouble(ln.split("\t")[1]));
		
		//Building Language Model for each expert
		HashMap<String, HashMap<String, Integer>> bigrams = new HashMap<>();
		HashMap<String, HashMap<String, Integer>> unigrams = new HashMap<>();
		
		Set<String> exps = experts.keySet();
		for(String e:exps){
			HashMap<String, Integer> biGramVec = new HashMap<>();
			HashMap<String, Integer> uniGramVec = new HashMap<>();
			HashMap<String, Boolean> incs = experts.get(e);
			Set<String> tickets = incs.keySet();
			for(String t:tickets)
				if(incidents.containsKey(t) && incs.get(t)){
					String[] words = incidents.get(t).split(" ");
					//for unigram
					for(int i=0;i<words.length-1;i++){
						String ug = words[i];
						if(uniGramVec.containsKey(ug))
							uniGramVec.put(ug, uniGramVec.get(ug)+1);
						else
							uniGramVec.put(ug, 1);
					}
					//for bigram
					for(int i=0;i<words.length-1;i++){
						String bg = usefulWords.get(words[i]) + "$" + usefulWords.get(words[i+1]);
						if(biGramVec.containsKey(bg))
							biGramVec.put(bg, biGramVec.get(bg)+1);
						else
							biGramVec.put(bg, 1);
					}
				}
			bigrams.put(e, biGramVec);
			unigrams.put(e, uniGramVec);
		}	
		
		//Calculate the similarity All incidents and corresponding expert group Language Model
		HashMap<String, Object> incidentExpertSimilarity = new HashMap<>();
		
		//Load all experts to resolve/transfer data
		experts = new HashMap<>();
		lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		lines.addAll(Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt")));		
		for(String ln:lines){
			String exp = ln.split("\t")[0];
			String[] parts = ln.split("\t")[1].split(",");
			HashMap<String, Boolean> incList = new HashMap<>();
			if(experts.containsKey(exp))
				incList = experts.get(exp);
			for(String p:parts)
				incList.put(p, true);
			experts.put(exp, incList);
		}
		
		double lambda = 0.001;
		exps = experts.keySet();
		for(String e:exps){
			if(!unigrams.containsKey(e)) //Then, don't have any expertise data for this expert
				continue;
			if(bestLambda.containsKey(e))
				lambda = bestLambda.get(e);
			
			HashMap<String, Integer> biGramVec = bigrams.get(e);
			HashMap<String, Integer> uniGramVec = unigrams.get(e);
			HashMap<String, Boolean> incs = experts.get(e);
			Set<String> tickets = incs.keySet();
			//Now, calculate the log likelihood of probability					
			for(String t:tickets)
				if(incidents.containsKey(t)){//Any incident is welcomed!		
					String[] words = incidents.get(t).split(" ");
					double ll = 0;
					int len = 0;
					for(int i=0;i<words.length-1;i++){
						String bg = usefulWords.get(words[i]) + "$" + usefulWords.get(words[i+1]);
						double biFreq = 0;
						if(biGramVec.containsKey(bg))
							biFreq = (double) biGramVec.get(bg);
						double uniFreq = 0;
						if(uniGramVec.containsKey(words[i]))
							uniFreq = (double) uniGramVec.get(words[i]);
						ll += Math.log10((biFreq + lambda)/(uniFreq + lambda*bigrams.size()));	
						len++;
					}
					if(len==0 || ll==0)
						incidentExpertSimilarity.put(e + "$" + t, "N/A");
					else{					
						ll = -(1.0/len)*ll;	
						ll = Math.round(ll*10000)/10000.0000;
						incidentExpertSimilarity.put(e + "$" + t, ll);
					}
				}
		}
		
		//Now, scan the input file and fill-in similarity values
		lines = Files.readAllLines(Paths.get(path + "QtimesResolution_Builder.csv"));
		BufferedWriter bw;
		if(resolvedBased)
			bw = new BufferedWriter(new FileWriter(path + "QtimesResolution_Builder_RB_LM.csv"));
		else
			bw = new BufferedWriter(new FileWriter(path + "QtimesResolution_Builder_TB_LM.csv"));
		bw.write(lines.get(0) + ",LM\n");
		int count = 0;
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split(",");
			String key = parts[2] + "$" + parts[1];
			String lm = "N/A"; 
			if(incidentExpertSimilarity.containsKey(key))
				lm = incidentExpertSimilarity.get(key).toString();
			if(!lm.equals("N/A"))
				count++;
			bw.write(lines.get(i) + "," + lm + "\n");
		}
		bw.close();
		
		System.out.println(count);
	}

	public static HashMap<String, HashMap<Integer, Double>> loadVectors(String path, String type, boolean uniOrBi, String R_or_T) throws IOException{
		// type: E stands for Expert, and I stands for Incidents
		// uniOrBi as true means the type of vector should be Unigram. 
		// R_or_T: 'R' stands for loading expertise vectors based on resolved incidents. 'T', stands for loading expertise based on transferred incidents. 
		// The last parameter just work for experts
	
		List<String> lines = null;		
		
		if(type.equals("I") && uniOrBi)
			lines = Files.readAllLines(Paths.get(path + "IncidentsUnigramVectors.txt"));
		else if(type.equals("I") && !uniOrBi)
			lines = Files.readAllLines(Paths.get(path + "IncidentsBigramVectors.txt"));
		else if(type.equals("E") && uniOrBi && R_or_T.equals("R"))
			lines = Files.readAllLines(Paths.get(path + "ExpertsUnigramVectors_ResolveBased.txt"));
		else if(type.equals("E") && !uniOrBi && R_or_T.equals("R"))
			lines = Files.readAllLines(Paths.get(path + "ExpertsBiGramVectors_ResolveBased.txt"));
		else if(type.equals("E") && uniOrBi && R_or_T.equals("T"))
			lines = Files.readAllLines(Paths.get(path + "ExpertsUnigramVectors_TransferredBased.txt"));
		else if(type.equals("E") && !uniOrBi && R_or_T.equals("T"))
			lines = Files.readAllLines(Paths.get(path + "ExpertsBiGramVectors_TransferredBased.txt"));
		
		HashMap<String, HashMap<Integer, Double>> vectors = new HashMap<>();
		
		for(int i=1;i<lines.size();i++){
			HashMap<Integer, Double> vector = new HashMap<>();
			String[] parts = lines.get(i).split("\t")[1].split(",");
			for(int j=0;j<parts.length;j++)
				vector.put(Integer.parseInt(parts[j].split(":")[0]), Double.parseDouble(parts[j].split(":")[1]));
			vectors.put(lines.get(i).split("\t")[0], vector);
		}
		
		return vectors;
	}
	
	public static void cosineBasedExpertiseIncidentSimilarityAllPossiblePairs(boolean resolvedBased) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Entire Data/Improved data/";

		//load useful words
		List<String> lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> usefulWords = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split("\t");
			usefulWords.put(parts[1], Integer.parseInt(parts[0]));
		}
			
		//load incident data
		HashMap<String, HashMap<Integer, Double>> incidents = loadVectors(path, "I", true, "");
		incidents = MLP.normalizeFeatures_zScore(incidents);
		
		//load expertise data
		HashMap<String, HashMap<Integer, Double>> expertise;
		if(resolvedBased)
			expertise = loadVectors(path, "E", true, "R");
		else
			expertise = loadVectors(path, "E", true, "T");
		expertise = MLP.normalizeFeatures_zScore(expertise);
		
		//Calculate the similarity All incidents and corresponding expert group by Cosine
		HashMap<String, Double> incidentExpertSimilarity = new HashMap<>();
		
		//Load all experts to resolve/transfer data
		HashMap<String, HashMap<String, Boolean>> experts = new HashMap<>();
		lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		lines.addAll(Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt")));		
		for(String ln:lines){
			String exp = ln.split("\t")[0];
			String[] parts = ln.split("\t")[1].split(",");
			HashMap<String, Boolean> incList = new HashMap<>();
			if(experts.containsKey(exp))
				incList = experts.get(exp);
			for(String p:parts)
				incList.put(p, true);
			experts.put(exp, incList);
		}
	
		Set<String> exps = experts.keySet();
		for(String e:exps){
			
			if(!expertise.containsKey(e))
				continue;
			
			HashMap<String, Boolean> incList = experts.get(e);
			HashMap<Integer, Double> exp_feat = expertise.get(e);
			Set<String> tickets = incList.keySet();
			for(String t:tickets){
				if(!incidents.containsKey(t))
					continue;
			
				HashMap<Integer, Double> inc_feat = incidents.get(t);
				
				double nominator = 0;
				double exp_size = 0, inc_size =0;
				Set<Integer> features = inc_feat.keySet();
				for(int f:features){
					if(exp_feat.containsKey(f)){
						nominator += exp_feat.get(f)*inc_feat.get(f);
						exp_size += Math.pow(exp_feat.get(f), 2);
					}
					inc_size += Math.pow(inc_feat.get(f), 2);
				}
				
				double cosine = nominator;
				if(exp_size > 0){
					cosine /= (Math.sqrt(exp_size)*Math.sqrt(inc_size));
					cosine = Math.round(cosine*1000)/1000.000;
				}
				else 
					cosine = 0;				
				
				incidentExpertSimilarity.put(e + "$" + t, cosine);
			}
		}
		
		//Now, scan the input file and fill-in similarity values
		BufferedWriter bw;
		if(resolvedBased){
			lines = Files.readAllLines(Paths.get(path + "QtimesResolution_Builder_RB_LM.csv"));
			bw = new BufferedWriter(new FileWriter(path + "QtimesResolution_Builder_RB_LM_Cosine.csv"));
		}
		else{
			lines = Files.readAllLines(Paths.get(path + "QtimesResolution_Builder_TB_LM.csv"));
			bw = new BufferedWriter(new FileWriter(path + "QtimesResolution_Builder_TB_LM_Cosine.csv"));
		}		 
		bw.write(lines.get(0) + ",Cosine\n");
		int count = 0;
		for(int i=1;i<lines.size();i++){
			String[] parts = lines.get(i).split(",");
			String key = parts[2] + "$" + parts[1];
			String lm = "N/A"; 
			if(incidentExpertSimilarity.containsKey(key))
				lm = incidentExpertSimilarity.get(key).toString();
			if(!lm.equals("N/A"))
				count++;
			bw.write(lines.get(i) + "," + lm + "\n");
		}
		bw.close();
		
		System.out.println(count);
	}
}