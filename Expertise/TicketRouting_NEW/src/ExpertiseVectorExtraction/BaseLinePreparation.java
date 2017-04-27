package ExpertiseVectorExtraction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BaseLinePreparation {
	
	static double N = 87000.0;
	
	public static void main(String[] args) throws Exception {
		
		//1. Get Frequent Experts
//		HashSet<String> frequentExperts = getFrequentExperts();
		
		//2. Filter incidents
//		HashSet<String> selectedIncidents = getIncidentsForFrequentExperts(frequentExperts);
		
		//3. Filter Expert to Resolve and Transfer vectors
//		getExpertToResolveAndTransfer(frequentExperts);
		
		//4. Extract Useful words and useful bigrams
//		TicketTextAnalysis.extractUsefullWords();
//		TicketTextAnalysis.extractUsefullBiGrams();
		
		//5. Create uni-Gram vector for incidents
//		TicketTextAnalysis.createFeatureVectorIncidents_uniGram();
		
		//6. Create Expertise-Vectors
//		TicketTextAnalysis.createFeatureVectorExperts_UniGram(true);
//		TicketTextAnalysis.createFeatureVectorExperts_UniGram(false);
		
		//7. Fit best lambda values for Language Model
//		TicketTextAnalysis.fitLambdaForEachExpertGroup(true);
//		TicketTextAnalysis.fitLambdaForEachExpertGroup(false);
		
		//8. Get the folds data --> #folds: 10; true: Resolved base; false: Transfer base
		HashMap<String, HashMap<Integer, String>> expertsToKFolds_res = getK_FoldData(10, true);
		HashMap<String, HashMap<Integer, String>> expertsToKFolds_trans = getK_FoldData(10, false);
		
		//9. Obtain confusion matrix based on Cosine Similarity
//		getConfusionByCosine(expertsToKFolds_res, expertsToKFolds_trans);
		
		//10. Obtain confusion matrix based on Language Model
//		getConfusionByLanguageModel(expertsToKFolds_res, expertsToKFolds_trans);
		
		//11. Obtain confusion matrix based on Ensemble of Cosine and LM
//		getConfusionByEnsemble(expertsToKFolds_res, expertsToKFolds_trans);
		
		//12. Produce Detailed Prediction Report
		detailReportByLmAndCosine(expertsToKFolds_res, expertsToKFolds_trans);
	}
	
	public static HashSet<String> getFrequentExperts() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		//filter out experts: we need experts who at least resolved 100 incidents and transferred 100 incidents.
		List<String> frequentResolvers = Files.readAllLines(Paths.get(path + "frequentResolvers.txt"));
		List<String> frequentTransferers = Files.readAllLines(Paths.get(path + "frequentTransferers.txt"));
		
		HashSet<String> frequentExperts = new HashSet<>();
		HashMap<String, Integer> resolvedSize = new HashMap<>();
		HashMap<String, Integer> transferredSize = new HashMap<>();
		for(String exp:frequentResolvers)
			if(frequentTransferers.contains(exp))
				frequentExperts.add(exp);
		
		List<String> lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		for(String ln:lines)
			resolvedSize.put(ln.split("\t")[0], ln.split("\t")[1].split(",").length);
		
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		for(String ln:lines)
			transferredSize.put(ln.split("\t")[0], ln.split("\t")[1].split(",").length);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "BaseLine Data/frequentExperts.txt"));
		bw.write("Expert\t#Resolved\t#Transferred\n");
		for(String exp:frequentExperts)
			bw.write(exp + "\t" + resolvedSize.get(exp) + "\t" + transferredSize.get(exp) + "\n");
		bw.close();
		
		return frequentExperts;
	}
	
	public static HashSet<String> getIncidentsForFrequentExperts(HashSet<String> frequentExperts) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
	
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		HashMap<String, String> incidentsForFrequentExperts = new HashMap<>();
		
		for(int i=1;i<lines.size();i++)
			incidentsForFrequentExperts.put(lines.get(i).split("\t")[0], lines.get(i).split("\t")[1]);
		
		HashSet<String> selectedIncidents = new HashSet<>();
		
		lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0])){
				String[] inc_list = ln.split("\t")[1].split(",");
				for(int i=0;i<inc_list.length;i++)
					selectedIncidents.add(inc_list[i]);
			}
		
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0])){
				String[] inc_list = ln.split("\t")[1].split(",");
				for(int i=0;i<inc_list.length;i++)
					selectedIncidents.add(inc_list[i]);
			}
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path + "BaseLine Data/IncidentsDescriptionTrain.txt"), "UTF-8"));
		out.write("IncidentID\tDescription\n");
		for(String inc:selectedIncidents)
			out.write(inc + "\t" + incidentsForFrequentExperts.get(inc) + "\n");
		out.close();
		
		return selectedIncidents;
	}

	public static void getExpertToResolveAndTransfer(HashSet<String> frequentExperts) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		
		List<String> lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "BaseLine Data/ExpertToResolved.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
		
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		bw = new BufferedWriter(new FileWriter(path + "BaseLine Data/ExpertToTransferred.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
	
		lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		bw = new BufferedWriter(new FileWriter(path + "BaseLine Data/ExpertToResolved_TD.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
		
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		bw = new BufferedWriter(new FileWriter(path + "BaseLine Data/ExpertToTransferred_TD.txt"));
		for(String ln:lines)
			if(frequentExperts.contains(ln.split("\t")[0]))
				bw.write(ln + "\n");
		bw.close();
	}
	
	public static HashMap<String, HashMap<Integer, String>> getK_FoldData(int k, boolean resolvedBase) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		List<String> lines;
		if(resolvedBase)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		
		HashMap<String, HashMap<Integer, String>> expertToResTransFolds = new HashMap<>();
		
		for(String ln:lines){
			String[] incidents = shuffleTheArray(ln.split("\t")[1].split(","));
			int foldLength = incidents.length/k;
			HashMap<Integer, String> folds = new HashMap<>();
			StringBuilder sb = new StringBuilder();
			for(int f=0;f<k-1;f++){
				sb = new StringBuilder();
//				System.out.println(f + "\t" + f*foldLength + "\t" + ((f+1)*foldLength-1));
				for(int i=f*foldLength;i<(f+1)*foldLength&&i<incidents.length;i++)
					sb.append("," + incidents[i]);
				folds.put(f, sb.toString().substring(1));				
			}
			sb = new StringBuilder();
//			System.out.println(k-1 + "\t" + (k-1)*foldLength + "\t" + incidents.length);
			for(int i=(k-1)*foldLength;i<incidents.length;i++)
				sb.append("," + incidents[i]);
			folds.put(k-1, sb.toString().substring(1));
			
			expertToResTransFolds.put(ln.split("\t")[0], folds);		
		}
		
		return expertToResTransFolds;
	}
	
	public static String[] shuffleTheArray(String[] input){
		ArrayList<String> list = new ArrayList<>();
		for(String s:input)
			list.add(s);
		Collections.shuffle(list);
		
		for(int i=0;i<list.size();i++)
			input[i] = list.get(i);
		
		return input;
	}

	public static void getConfusionByCosine(HashMap<String, HashMap<Integer, String>> expertsToKFolds_res, 
			HashMap<String, HashMap<Integer, String>> expertsToKFolds_trans) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";

		//load incident data
		HashMap<String, HashMap<String, Integer>> incidents = loadIncidentDataUniGram();		
		
		//To report the baseline results
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "CosineBaseConfusionMatrices.csv"));
		bw.write("Expert,TruePositive,FalseNegative,FalsePositive,TrueNegative,Precision,Recall,F-Measure\n");
		
		Confusion comprehensiveConfusionMatrix = new Confusion();
		
		//iterate over all experts
		Set<String> keys = expertsToKFolds_res.keySet();		
		for(String exp:keys){
			
			//define the total Confusion
			Confusion totalConfusion = new Confusion();
			
			//iterate over all folds
			Set<Integer> folds = expertsToKFolds_res.get(exp).keySet();			
			for(int f:folds){
				//create expertise based on all folds data except fold 'f'
				
				//1. Create train and dev sets based on resolved and transferred
				HashSet<String> trainIncidents_res = new HashSet<>();
				HashSet<String> trainIncidents_trans = new HashSet<>();
				
				HashSet<String> devIncidents_res = new HashSet<>();
				HashSet<String> devIncidents_trans = new HashSet<>();
				
				for(int k:folds){					
					String[] parts = expertsToKFolds_res.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_res.add(p);
						else
							devIncidents_res.add(p);
					}
					
					parts = expertsToKFolds_trans.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_trans.add(p);
						else
							devIncidents_trans.add(p);
					}
				}
				
				//2. Learn Expertise based on train incidents
				HashMap<String, Double> expertise_res = returnUniGramExpertVector(trainIncidents_res, incidents);
				HashMap<String, Double> expertise_trans = returnUniGramExpertVector(trainIncidents_trans, incidents);
				Confusion thisFoldConf = classifyByCosineSimilarity(expertise_res, expertise_trans, devIncidents_res, devIncidents_trans, incidents);
				
				totalConfusion.truePositive += thisFoldConf.truePositive;
				totalConfusion.falsePositive += thisFoldConf.falsePositive;
				totalConfusion.falseNegative += thisFoldConf.falseNegative;
				totalConfusion.trueNegative += thisFoldConf.trueNegative;
			}
			
			//report the result for current expert
			HashMap<String, Double> res = returnEvaluationMetrics(totalConfusion);
			bw.write(exp + "," + totalConfusion.truePositive + "," + totalConfusion.falseNegative + "," +
					totalConfusion.falsePositive + "," + totalConfusion.trueNegative);
			bw.write("," + res.get("precision") + "," + 
					res.get("recall") + "," + res.get("f_measure") + "\n");
			
//			System.out.println(exp + "\t" + totalConfusion.truePositive + "\t" + totalConfusion.falseNegative + "\t" +
//					totalConfusion.falsePositive + "\t" + totalConfusion.trueNegative);
			
			comprehensiveConfusionMatrix.truePositive += totalConfusion.truePositive;
			comprehensiveConfusionMatrix.falsePositive += totalConfusion.falsePositive;
			comprehensiveConfusionMatrix.falseNegative += totalConfusion.falseNegative;
			comprehensiveConfusionMatrix.trueNegative += totalConfusion.trueNegative;
		}
		
		bw.close();
		System.out.println("'Cosine' Comprehensive Confusion Matrix: ");
		System.out.println("TP: " + comprehensiveConfusionMatrix.truePositive + "\tFN: " + comprehensiveConfusionMatrix.falseNegative);
		System.out.println("FP: " + comprehensiveConfusionMatrix.falsePositive + "\tTN: " + comprehensiveConfusionMatrix.trueNegative);
		
		//calculate metrics
		System.out.println(returnEvaluationMetrics(comprehensiveConfusionMatrix));
		System.out.println();
		
	}
	
	public static HashMap<String, HashMap<String, Integer>> loadIncidentDataUniGram() throws IOException{
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
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
		for(int i=1;i<ws.size();i++){
			String[] parts = ws.get(i).split("\t");
			wordDF.put(parts[1], Integer.parseInt(parts[2]));
		}
	
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		HashMap<String, HashMap<String, Integer>> incidentsWords = new HashMap<>();
		
		Set<String> tickets = incidents.keySet();
		for(String inc:tickets){
			String input = incidents.get(inc);
			HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
			
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
			
			if(termFrequency.size() > 0)
				incidentsWords.put(inc, termFrequency);
		}
		
		return incidentsWords;
	}
	
	public static HashMap<String, Double> returnUniGramExpertVector(HashSet<String> trainIncidents, 
			HashMap<String, HashMap<String, Integer>> incidents) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		//load useful words and assign identifier to them
		List<String> ws = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		HashMap<String, Integer> wordDF = new HashMap<String, Integer>(); //A better approach was to first specify a dev set, then count the word and inverse word frequencies. 
		for(int i=1;i<ws.size();i++){
			String[] parts = ws.get(i).split("\t");
			wordDF.put(parts[1], Integer.parseInt(parts[2]));
		}
	
		HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
		double docLenght = 0;
		for(String inc:trainIncidents)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> wordFreq = incidents.get(inc);
				Set<String> words = wordFreq.keySet();
				for(String w:words){
					if(termFrequency.containsKey(w))
						termFrequency.put(w, termFrequency.get(w) + wordFreq.get(w));
					else
						termFrequency.put(w, wordFreq.get(w));
					
					docLenght += wordFreq.get(w);
				}					
			}
		
		//Create Vector
		HashMap<String, Double> vector = new HashMap<>();		
		Set<String> terms = termFrequency.keySet();
		for(String w:terms){
			int df = wordDF.get(w);
			double tfIdfScore = Math.round(((termFrequency.get(w)/docLenght) * Math.log(N/df))*10000)/10000.0000;
			vector.put(w, tfIdfScore);
		}
		
		return vector;
			
	}
	
	public static Confusion classifyByCosineSimilarity(HashMap<String, Double> expertise_res, HashMap<String, Double> expertise_trans, 
			HashSet<String> devIncidents_res, HashSet<String> devIncidents_trans, HashMap<String, HashMap<String, Integer>> incidents){
		Confusion res = new Confusion();
		
		//count for true positive and false negative
		for(String inc:devIncidents_res)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> incVector = incidents.get(inc);
				double cosineByResolve = returnCosine(expertise_res, incVector);
				double cosineByTransfer = returnCosine(expertise_trans, incVector);
				
				if(cosineByResolve >= cosineByTransfer)
					res.truePositive++;
				else
					res.falseNegative++;
			}
		
		//count for true negative and false positive
		for(String inc:devIncidents_trans)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> incVector = incidents.get(inc);
				double cosineByResolve = returnCosine(expertise_res, incVector);
				double cosineByTransfer = returnCosine(expertise_trans, incVector);
				
				if(cosineByTransfer >= cosineByResolve)
					res.trueNegative++;
				else
					res.falsePositive++;
			}
		
		return res;
	}
	
	public static double returnCosine(HashMap<String, Double> expertise, HashMap<String, Integer> incident){
		//get size of ticket
		double size = 0;
		Set<String> features = incident.keySet();
		for(String f:features)
			size += incident.get(f);
		
		double nominator = 0;
		double exp_size = 0, inc_size =0;
		features = incident.keySet();
		for(String f:features){
			if(expertise.containsKey(f)){
				nominator += expertise.get(f)*incident.get(f);
				exp_size += Math.pow(expertise.get(f), 2);
			}
			inc_size += Math.pow(incident.get(f)/size, 2);
		}
		
		double cosine = nominator/size; //normalize incident feature values by size of incidents; simplification instead of devision of each feature by size
		if(exp_size > 0)
			cosine /= (Math.sqrt(exp_size)*Math.sqrt(inc_size));
	
		return cosine;
	}

	public static HashMap<String, Double> returnEvaluationMetrics(Confusion input){
		HashMap<String, Double> metrics = new HashMap<>();
		
		//calculate the accuracy
//		double accuracy = 0;
//		accuracy = (double)(input.truePositive + input.trueNegative)/
//				(input.truePositive + input.trueNegative + input.falsePositive + input.falseNegative);
//		metrics.put("accuracy", Math.round(accuracy*1000)/1000.000);
		
		//calculate size of both classes
//		int c1 = input.truePositive + input.falseNegative;
//		int c2 = input.falsePositive + input.trueNegative; 
		
		double precision = 0;
		double p_1 = input.truePositive/(double)(input.truePositive+input.falsePositive);
		double p_2 = input.trueNegative/(double)(input.trueNegative+input.falseNegative);
//		precision = (p_1*c1 + p_2*c2)/(c1+c2);
		precision = (p_1 + p_2)/2.0;
		metrics.put("precision", Math.round(precision*1000)/1000.000);
		
		double recall = 0;
		double r_1 = input.truePositive/(double)(input.truePositive+input.falseNegative);
		double r_2 = input.trueNegative/(double)(input.trueNegative+input.falsePositive);
//		recall = (r_1*c1 + r_2*c2)/(c1+c2);
		recall = (r_1 + r_2)/2.0;
		metrics.put("recall", Math.round(recall*1000)/1000.000);
		
		double f_measure = 0;
//		double f_1 = 2.0*(p_1*r_1)/(p_1+r_1); 
//		double f_2 = 2.0*(p_2*r_2)/(p_2+r_2);
//		f_measure = (f_1*c1 + f_2*c2)/(c1+c2);
		f_measure = 2.0*(precision*recall)/(precision+recall);
		metrics.put("f_measure", Math.round(f_measure*1000)/1000.000);
	
		return metrics;
	}
	
	public static HashMap<String, HashMap<String, Integer>> loadIncidentDataBiGram() throws IOException{
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		//Load incident data
		List<String> lines = Files.readAllLines(Paths.get(path + "IncidentsDescriptionTrain.txt"));
		HashMap<String, String> incidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			String[] parts  = lines.get(i).split("\t");
			incidents.put(parts[0], parts[1]);
		}
		
		//load useful words and assign identifier to them
		List<String> bs = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		HashMap<String, Integer> bigramDF = new HashMap<String, Integer>(); //A better approach was to first specify a dev set, then count the word and inverse word frequencies. 
		for(int i=1;i<bs.size();i++){
			String[] parts = bs.get(i).split("\t");
			bigramDF.put(parts[1], Integer.parseInt(parts[2]));
		}
	
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		HashMap<String, HashMap<String, Integer>> incidentsBiGrams = new HashMap<>();
		
		Set<String> tickets = incidents.keySet();
		for(String inc:tickets){
			String input = incidents.get(inc);
			HashMap<String, Integer> biGrams = new HashMap<String, Integer>();
			
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');		
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			String[] words = input.split(" ");
			
			for(int j=0;j<words.length;j++)
				words[j] = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");
			
			for(int j=0;j<words.length-1;j++){
				String bg = words[j] + "-" + words[j+1];
				if(!bigramDF.containsKey(bg))
					continue;
				
				if(biGrams.containsKey(bg))
					biGrams.put(bg, biGrams.get(bg)+1);
				else
					biGrams.put(bg, 1);
			}
			
			if(biGrams.size() > 0)
				incidentsBiGrams.put(inc, biGrams);
		}
		
		return incidentsBiGrams;
	}
	
	public static HashMap<String, HashMap<String, Integer>> returnBiGramExpertVector(HashSet<String> trainIncidents, 
			HashMap<String, HashMap<String, Integer>> incidents) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
		HashMap<String, Integer> biGramVec = new HashMap<>();
		HashMap<String, Integer> uniGramVec = new HashMap<>();
		for(String inc:trainIncidents)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> biGrams = incidents.get(inc);
				Set<String> bgList = biGrams.keySet();
				for(String bg:bgList){
					//update bigrams
					if(biGramVec.containsKey(bg))
						biGramVec.put(bg, biGramVec.get(bg) + biGrams.get(bg));
					else
						biGramVec.put(bg, biGrams.get(bg));
					
					//update unigrams
					String[] parts = bg.split("-");
					if(uniGramVec.containsKey(parts[0]))
						uniGramVec.put(parts[0], uniGramVec.get(parts[0]) + biGrams.get(bg));
					else
						uniGramVec.put(parts[0], biGrams.get(bg));
				}
			}
		
		//Prepare output
		HashMap<String, HashMap<String, Integer>> out = new HashMap<>();
		out.put("unigram", uniGramVec);
		out.put("bigram", biGramVec);
	
		return out;
			
	}
	
	public static double returnLM(HashMap<String, HashMap<String, Integer>> expertise, HashMap<String, Integer> incident, double lambda, int len){
		
		//get unigram and bigrams
		HashMap<String, Integer> uniGrams = expertise.get("unigram"); 
		HashMap<String, Integer> biGrams = expertise.get("bigram"); 
					
		double lm = 0;		
		Set<String> inc = incident.keySet();
		for(String bg:inc){
			double biFreq = biGrams.containsKey(bg)? biGrams.get(bg):0;
			double uniFreq = uniGrams.containsKey(bg.split("-")[0])? uniGrams.get(bg.split("-")[0]):0;
			lm += Math.log10((biFreq + lambda)/(uniFreq + lambda*biGrams.size()));
		}
		
		lm = -(1.0/len)*lm;
		
		return lm;
	}
	
	public static Confusion classifyByLanguageModel(HashMap<String, HashMap<String, Integer>> expertise_res, 
			HashMap<String, HashMap<String, Integer>> expertise_trans, HashSet<String> devIncidents_res, 
			HashSet<String> devIncidents_trans, HashMap<String, HashMap<String, Integer>> incidents,
			HashMap<String, Integer> length, double resolveLambda, double transferLambda){
		
		Confusion res = new Confusion();
		
		//count for true positive and false negative
		for(String inc:devIncidents_res)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> incVector = incidents.get(inc);
				int len = length.get(inc);				
				double lmByResolve = returnLM(expertise_res, incVector, resolveLambda, len);
				double lmByTransfer = returnLM(expertise_trans, incVector, transferLambda, len);
				
				if(lmByResolve < lmByTransfer) //the smaller value the better
					res.truePositive++;
				else
					res.falseNegative++;
			}
		
		//count for true negative and false positive
		for(String inc:devIncidents_trans)
			if(incidents.containsKey(inc)){
				HashMap<String, Integer> incVector = incidents.get(inc);
				int len = length.get(inc);
				double lmByResolve = returnLM(expertise_res, incVector, resolveLambda, len);
				double lmByTransfer = returnLM(expertise_trans, incVector, transferLambda, len);
				
				if(lmByTransfer < lmByResolve) //the smaller value the better
					res.trueNegative++;
				else
					res.falsePositive++;
			}
		
		return res;
	}
	
	public static void getConfusionByLanguageModel(HashMap<String, HashMap<Integer, String>> expertsToKFolds_res, 
			HashMap<String, HashMap<Integer, String>> expertsToKFolds_trans) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";

		//load incident data
		HashMap<String, HashMap<String, Integer>> incidents = loadIncidentDataBiGram();	 
		
		//load incident length
		HashMap<String, Integer> length = loadIncidentLength();
		
		//load best lambda values for both resolved and trasnferred
		List<String> lamLines = Files.readAllLines(Paths.get(path + "bestLambda_ResolvedBase.txt"));
		HashMap<String, Double> resolvedLambda = new HashMap<>();
		for(String exp:lamLines)
			resolvedLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		lamLines = Files.readAllLines(Paths.get(path + "bestLambda_TransferredBase.txt"));
		HashMap<String, Double> transferredLambda = new HashMap<>();
		for(String exp:lamLines)
			transferredLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		//To report the baseline results
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "LMBaseConfusionMatrices.csv"));
		bw.write("Expert,TruePositive,FalseNegative,FalsePositive,TrueNegative,Precision,Recall,F-Measure\n");
		
		Confusion comprehensiveConfusionMatrix = new Confusion();
		
		//iterate over all experts
		Set<String> keys = expertsToKFolds_res.keySet();		
		for(String exp:keys){
			
			//define the total Confusion
			Confusion totalConfusion = new Confusion();
			
			//iterate over all folds
			Set<Integer> folds = expertsToKFolds_res.get(exp).keySet();			
			for(int f:folds){
				//create expertise based on all folds data except fold 'f'
				
				//1. Create train and dev sets based on resolved and transferred
				HashSet<String> trainIncidents_res = new HashSet<>();
				HashSet<String> trainIncidents_trans = new HashSet<>();
				
				HashSet<String> devIncidents_res = new HashSet<>();
				HashSet<String> devIncidents_trans = new HashSet<>();
				
				for(int k:folds){					
					String[] parts = expertsToKFolds_res.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_res.add(p);
						else
							devIncidents_res.add(p);
					}
					
					parts = expertsToKFolds_trans.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_trans.add(p);
						else
							devIncidents_trans.add(p);
					}
				}
				
				//2. Learn Expertise based on train incidents
				HashMap<String, HashMap<String, Integer>> expertise_res = returnBiGramExpertVector(trainIncidents_res, incidents);
				HashMap<String, HashMap<String, Integer>> expertise_trans = returnBiGramExpertVector(trainIncidents_trans, incidents);
				Confusion thisFoldConf = classifyByLanguageModel(expertise_res, expertise_trans, devIncidents_res, 
						devIncidents_trans, incidents, length, resolvedLambda.get(exp), transferredLambda.get(exp));
				
				totalConfusion.truePositive += thisFoldConf.truePositive;
				totalConfusion.falsePositive += thisFoldConf.falsePositive;
				totalConfusion.falseNegative += thisFoldConf.falseNegative;
				totalConfusion.trueNegative += thisFoldConf.trueNegative;
			}
			
			//report the result for current expert
			HashMap<String, Double> res = returnEvaluationMetrics(totalConfusion);
			bw.write(exp + "," + totalConfusion.truePositive + "," + totalConfusion.falseNegative + "," +
					totalConfusion.falsePositive + "," + totalConfusion.trueNegative);
			bw.write("," + res.get("precision") + "," + 
					res.get("recall") + "," + res.get("f_measure") + "\n");
			
//			System.out.println(exp + "\t" + totalConfusion.truePositive + "\t" + totalConfusion.falseNegative + "\t" +
//					totalConfusion.falsePositive + "\t" + totalConfusion.trueNegative);
			
			comprehensiveConfusionMatrix.truePositive += totalConfusion.truePositive;
			comprehensiveConfusionMatrix.falsePositive += totalConfusion.falsePositive;
			comprehensiveConfusionMatrix.falseNegative += totalConfusion.falseNegative;
			comprehensiveConfusionMatrix.trueNegative += totalConfusion.trueNegative;
		}
		
		bw.close();
		System.out.println("'LM' Comprehensive Confusion Matrix: ");
		System.out.println("TP: " + comprehensiveConfusionMatrix.truePositive + "\tFN: " + comprehensiveConfusionMatrix.falseNegative);
		System.out.println("FP: " + comprehensiveConfusionMatrix.falsePositive + "\tTN: " + comprehensiveConfusionMatrix.trueNegative);
		
		//calculate metrics
		System.out.println(returnEvaluationMetrics(comprehensiveConfusionMatrix));
		System.out.println();
	}

	public static HashMap<String, Integer> loadIncidentLength() throws IOException{
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		
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
		for(int i=1;i<ws.size();i++){
			String[] parts = ws.get(i).split("\t");
			wordDF.put(parts[1], Integer.parseInt(parts[2]));
		}
	
		char[] puncs = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
				'(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'};
		
		HashMap<String, Integer> incidentsLength = new HashMap<>();
		
		Set<String> tickets = incidents.keySet();
		for(String inc:tickets){
			String input = incidents.get(inc);
			
			for(int j=0;j<puncs.length;j++)
				input = input.replace(puncs[j], ' ');		
			input = input.replace("'", "");
			input = input.replaceAll("[ ]+", " ");
			String[] words = input.split(" ");
			int len = 0;
			
			for(int j=0;j<words.length;j++){
				String word = words[j].toLowerCase().replaceAll("[0-9]+(st|th|rd|am|pm|s|k)", "0");					
				if(wordDF.containsKey(word))
					len++;
			}
			
			incidentsLength.put(inc, len);
		}
		
		return incidentsLength;
	}
	
	public static void getConfusionByEnsemble(HashMap<String, HashMap<Integer, String>> expertsToKFolds_res, 
			HashMap<String, HashMap<Integer, String>> expertsToKFolds_trans) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";

		//load incident data
		HashMap<String, HashMap<String, Integer>> incidents_UniGram = loadIncidentDataUniGram();
		HashMap<String, HashMap<String, Integer>> incidents_BiGram = loadIncidentDataBiGram();	
		
		//load length of incidents
		HashMap<String, Integer> length = loadIncidentLength();
		
		//load best lambda values for both resolved and transferred
		List<String> lamLines = Files.readAllLines(Paths.get(path + "bestLambda_ResolvedBase.txt"));
		HashMap<String, Double> resolvedLambda = new HashMap<>();
		for(String exp:lamLines)
			resolvedLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		lamLines = Files.readAllLines(Paths.get(path + "bestLambda_TransferredBase.txt"));
		HashMap<String, Double> transferredLambda = new HashMap<>();
		for(String exp:lamLines)
			transferredLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		//To report the baseline results
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "ensembleBaseConfusionMatrices.csv"));
		bw.write("Expert,TruePositive,FalseNegative,FalsePositive,TrueNegative,Precision,Recall,F-Measure\n");
		
		Confusion comprehensiveConfusionMatrix = new Confusion();
		
		//iterate over all experts
		Set<String> keys = expertsToKFolds_res.keySet();		
		for(String exp:keys){
			
			//define the total Confusion
			Confusion totalConfusion = new Confusion();
			
			//iterate over all folds
			Set<Integer> folds = expertsToKFolds_res.get(exp).keySet();			
			for(int f:folds){
				//create expertise based on all folds data except fold 'f'
				
				//1. Create train and dev sets based on resolved and transferred
				HashSet<String> trainIncidents_res = new HashSet<>();
				HashSet<String> trainIncidents_trans = new HashSet<>();
				
				HashSet<String> devIncidents_res = new HashSet<>();
				HashSet<String> devIncidents_trans = new HashSet<>();
				
				for(int k:folds){					
					String[] parts = expertsToKFolds_res.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_res.add(p);
						else
							devIncidents_res.add(p);
					}
					
					parts = expertsToKFolds_trans.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_trans.add(p);
						else
							devIncidents_trans.add(p);
					}
				}
				
				//2. Learn Expertise by unigarm
				HashMap<String, Double> expertise_res_UniGram = returnUniGramExpertVector(trainIncidents_res, incidents_UniGram);
				HashMap<String, Double> expertise_trans_UniGram = returnUniGramExpertVector(trainIncidents_trans, incidents_UniGram);
				
				//3. Learn Expertise by BiGram
				HashMap<String, HashMap<String, Integer>> expertise_res_BiGram = returnBiGramExpertVector(trainIncidents_res, incidents_BiGram);
				HashMap<String, HashMap<String, Integer>> expertise_trans_BiGram = returnBiGramExpertVector(trainIncidents_trans, incidents_BiGram);
				
				Confusion thisFoldConf = classifyByEnsemble(expertise_res_UniGram, expertise_trans_UniGram, 
						expertise_res_BiGram, expertise_trans_BiGram, incidents_UniGram, incidents_BiGram, length,
						devIncidents_res, devIncidents_trans, resolvedLambda.get(exp), transferredLambda.get(exp));
				
				totalConfusion.truePositive += thisFoldConf.truePositive;
				totalConfusion.falsePositive += thisFoldConf.falsePositive;
				totalConfusion.falseNegative += thisFoldConf.falseNegative;
				totalConfusion.trueNegative += thisFoldConf.trueNegative;
			}
			
			//report the result for current expert
			HashMap<String, Double> res = returnEvaluationMetrics(totalConfusion);
			bw.write(exp + "," + totalConfusion.truePositive + "," + totalConfusion.falseNegative + "," +
					totalConfusion.falsePositive + "," + totalConfusion.trueNegative);
			bw.write("," + res.get("precision") + "," + 
					res.get("recall") + "," + res.get("f_measure") + "\n");
			
//			System.out.println(exp + "\t" + totalConfusion.truePositive + "\t" + totalConfusion.falseNegative + "\t" +
//					totalConfusion.falsePositive + "\t" + totalConfusion.trueNegative);
			
			comprehensiveConfusionMatrix.truePositive += totalConfusion.truePositive;
			comprehensiveConfusionMatrix.falsePositive += totalConfusion.falsePositive;
			comprehensiveConfusionMatrix.falseNegative += totalConfusion.falseNegative;
			comprehensiveConfusionMatrix.trueNegative += totalConfusion.trueNegative;
		}
		
		bw.close();
		System.out.println("'Ensemble' Comprehensive Confusion Matrix: ");
		System.out.println("TP: " + comprehensiveConfusionMatrix.truePositive + "\tFN: " + comprehensiveConfusionMatrix.falseNegative);
		System.out.println("FP: " + comprehensiveConfusionMatrix.falsePositive + "\tTN: " + comprehensiveConfusionMatrix.trueNegative);
		
		//calculate metrics
		System.out.println(returnEvaluationMetrics(comprehensiveConfusionMatrix));
		System.out.println();
	}

	public static Confusion classifyByEnsemble(HashMap<String, Double> expertise_res_UniGram,
			HashMap<String, Double> expertise_trans_UniGram, HashMap<String, HashMap<String, Integer>> expertise_res_BiGram,
			HashMap<String, HashMap<String, Integer>> expertise_trans_BiGram, HashMap<String, HashMap<String, Integer>> incidents_UniGram,
			HashMap<String, HashMap<String, Integer>> incidents_BiGram, HashMap<String, Integer> length,  HashSet<String> devIncidents_res, 
			HashSet<String> devIncidents_trans,	double resolveLambda, double transferLambda) {
		
		Confusion res = new Confusion();
		
		//count for true positive and false negative
		for(String inc:devIncidents_res)
			if(incidents_UniGram.containsKey(inc) && incidents_BiGram.containsKey(inc)){
				int len = length.get(inc);
				
				HashMap<String, Integer> incVectorU = incidents_UniGram.get(inc);
				double cosineByResolve = returnCosine(expertise_res_UniGram, incVectorU);
				double cosineByTransfer = returnCosine(expertise_trans_UniGram, incVectorU);
				
				HashMap<String, Integer> incVectorB = incidents_BiGram.get(inc);
				double lmByResolve = -1 * returnLM(expertise_res_BiGram, incVectorB, resolveLambda, len);
				double lmByTransfer = -1 * returnLM(expertise_trans_BiGram, incVectorB, transferLambda, len);
				
				double p_cos_res = cosineByResolve;
//				if(cosineByResolve + cosineByTransfer > 0)
				p_cos_res /= (cosineByResolve + cosineByTransfer);
//				double p_cos_trans = 1.0-p_cos_res;
				
				double p_lm_res = Math.exp(lmByResolve);
//				if(lmByResolve+lmByTransfer) != 0)
				p_lm_res /= (Math.exp(lmByResolve) + Math.exp(lmByTransfer));
				
				double p_res = (Math.abs(p_cos_res-0.5) > Math.abs(p_lm_res-0.5)) ? p_cos_res : p_lm_res;
				double p_trans = 1.0-p_res;
//				double p_trans = (Math.abs(p_cos_trans-0.5) > Math.abs(p_lm_trans-0.5)) ? p_cos_trans : p_lm_trans;
				
				if(p_res > p_trans)
					res.truePositive++;
				else
					res.falseNegative++;
			}
		
		//count for true negative and false positive
		for(String inc:devIncidents_trans)
			if(incidents_UniGram.containsKey(inc) && incidents_BiGram.containsKey(inc)){
				int len = length.get(inc);
				
				HashMap<String, Integer> incVectorU = incidents_UniGram.get(inc);
				double cosineByResolve = returnCosine(expertise_res_UniGram, incVectorU);
				double cosineByTransfer = returnCosine(expertise_trans_UniGram, incVectorU);
				
				HashMap<String, Integer> incVectorB = incidents_BiGram.get(inc);
				double lmByResolve = -1 * returnLM(expertise_res_BiGram, incVectorB, resolveLambda, len);
				double lmByTransfer = -1 * returnLM(expertise_trans_BiGram, incVectorB, transferLambda, len);
				
				double p_cos_res = cosineByResolve;
//				if(cosineByResolve + cosineByTransfer > 0)
				p_cos_res /= (cosineByResolve + cosineByTransfer);
				//double p_cos_trans = 1.0-p_cos_res;
				
				double p_lm_res = Math.exp(lmByResolve);
//				if(Math.abs(lmByResolve+lmByTransfer) > 0)
				p_lm_res /= (Math.exp(lmByResolve) + Math.exp(lmByTransfer));
				//double p_lm_trans = 1.0 - p_lm_res;				
				
				double p_res = (Math.abs(p_cos_res-0.5) > Math.abs(p_lm_res-0.5)) ? p_cos_res : p_lm_res;
				double p_trans = 1.0-p_res;
				
				if(p_trans > p_res)
					res.trueNegative++;
				else
					res.falsePositive++;
			}
		
		return res;
	}

	public static void detailReportByLmAndCosine(HashMap<String, HashMap<Integer, String>> expertsToKFolds_res, 
			HashMap<String, HashMap<Integer, String>> expertsToKFolds_trans) throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";

		//load incident data
		HashMap<String, HashMap<String, Integer>> incidents_UniGram = loadIncidentDataUniGram();
		HashMap<String, HashMap<String, Integer>> incidents_BiGram = loadIncidentDataBiGram();	
		
		//load length of incidents
		HashMap<String, Integer> length = loadIncidentLength();
		
		//load best lambda values for both resolved and transferred
		List<String> lamLines = Files.readAllLines(Paths.get(path + "bestLambda_ResolvedBase.txt"));
		HashMap<String, Double> resolvedLambda = new HashMap<>();
		for(String exp:lamLines)
			resolvedLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		lamLines = Files.readAllLines(Paths.get(path + "bestLambda_TransferredBase.txt"));
		HashMap<String, Double> transferredLambda = new HashMap<>();
		for(String exp:lamLines)
			transferredLambda.put(exp.split("\t")[0], Double.parseDouble(exp.split("\t")[1]));
		
		//To report the baseline results
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "DetailPredictionReport.csv"));
		bw.write("#incidentID,Expert,ActualLabel,Prediction,LM_ResRawValue,LM_TransRawValue,"
				+"Cos_ResRawValue,Cos_TransRawValue,LM_ResProbability,LM_TransProbability,"
				+"Cos_ResProbability,Cos_TransProbability,FoldNumber\n");
		
		//iterate over all experts
		Set<String> keys = expertsToKFolds_res.keySet();		
		for(String exp:keys){
			
			//iterate over all folds
			Set<Integer> folds = expertsToKFolds_res.get(exp).keySet();			
			for(int f:folds){
				//create expertise based on all folds data except fold 'f'
				
				//1. Create train and dev sets based on resolved and transferred
				HashSet<String> trainIncidents_res = new HashSet<>();
				HashSet<String> trainIncidents_trans = new HashSet<>();
				
				HashSet<String> devIncidents_res = new HashSet<>();
				HashSet<String> devIncidents_trans = new HashSet<>();
				
				for(int k:folds){					
					String[] parts = expertsToKFolds_res.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_res.add(p);
						else
							devIncidents_res.add(p);
					}
					
					parts = expertsToKFolds_trans.get(exp).get(k).split(",");
					for(String p:parts){
						if(k != f)
							trainIncidents_trans.add(p);
						else
							devIncidents_trans.add(p);
					}
				}
				
				//2. Learn Expertise by unigarm
				HashMap<String, Double> expertise_res_UniGram = returnUniGramExpertVector(trainIncidents_res, incidents_UniGram);
				HashMap<String, Double> expertise_trans_UniGram = returnUniGramExpertVector(trainIncidents_trans, incidents_UniGram);
				
				//3. Learn Expertise by BiGram
				HashMap<String, HashMap<String, Integer>> expertise_res_BiGram = returnBiGramExpertVector(trainIncidents_res, incidents_BiGram);
				HashMap<String, HashMap<String, Integer>> expertise_trans_BiGram = returnBiGramExpertVector(trainIncidents_trans, incidents_BiGram);
				
				labelAndWrite(exp, bw, f+1, expertise_res_UniGram, expertise_trans_UniGram, 
						expertise_res_BiGram, expertise_trans_BiGram, incidents_UniGram, incidents_BiGram, length,
						devIncidents_res, devIncidents_trans, resolvedLambda.get(exp), transferredLambda.get(exp));			
			}
		}
		
		bw.close();
		
	}
	
	public static void labelAndWrite(String exp, BufferedWriter bw, int fold, HashMap<String, Double> expertise_res_UniGram,
			HashMap<String, Double> expertise_trans_UniGram, HashMap<String, HashMap<String, Integer>> expertise_res_BiGram,
			HashMap<String, HashMap<String, Integer>> expertise_trans_BiGram, HashMap<String, HashMap<String, Integer>> incidents_UniGram,
			HashMap<String, HashMap<String, Integer>> incidents_BiGram, HashMap<String, Integer> length,  HashSet<String> devIncidents_res, 
			HashSet<String> devIncidents_trans,	double resolveLambda, double transferLambda) throws IOException {
	
		//count for true positive and false negative
		for(String inc:devIncidents_res)
			if(incidents_UniGram.containsKey(inc) && incidents_BiGram.containsKey(inc)){
				int len = length.get(inc);
				
				HashMap<String, Integer> incVectorU = incidents_UniGram.get(inc);
				double cosineByResolve = returnCosine(expertise_res_UniGram, incVectorU);
				double cosineByTransfer = returnCosine(expertise_trans_UniGram, incVectorU);
				
				HashMap<String, Integer> incVectorB = incidents_BiGram.get(inc);
				double lmByResolve = -1 * returnLM(expertise_res_BiGram, incVectorB, resolveLambda, len);
				double lmByTransfer = -1 * returnLM(expertise_trans_BiGram, incVectorB, transferLambda, len);
				
				double p_cos_res = cosineByResolve;
				p_cos_res /= (cosineByResolve + cosineByTransfer);
				double p_cos_trans = 1.0-p_cos_res;
				
				double p_lm_res = Math.exp(lmByResolve);
				p_lm_res /= (Math.exp(lmByResolve) + Math.exp(lmByTransfer));
				double p_lm_trans = 1.0 - p_lm_res;
				
				double p_res = (Math.abs(p_cos_res-0.5) > Math.abs(p_lm_res-0.5)) ? p_cos_res : p_lm_res;
				double p_trans = 1.0-p_res;
		
				if(p_res > p_trans)
					bw.write(inc + "," + exp + "," + "Res" + "," + "Res" + "," + lmByResolve + "," + lmByTransfer + "," + 
							cosineByResolve + "," + cosineByTransfer + "," + p_lm_res + "," + p_lm_trans + "," + p_cos_res + 
							"," + p_cos_trans + "," + fold + "\n");
				else
					bw.write(inc + "," + exp + "," + "Res" + "," + "Trans" + "," + lmByResolve + "," + lmByTransfer + "," + 
							cosineByResolve + "," + cosineByTransfer + "," + p_lm_res + "," + p_lm_trans + "," + p_cos_res + 
							"," + p_cos_trans + "," + fold + "\n");
			}
		
		//count for true negative and false positive
		for(String inc:devIncidents_trans)
			if(incidents_UniGram.containsKey(inc) && incidents_BiGram.containsKey(inc)){
				int len = length.get(inc);
				
				HashMap<String, Integer> incVectorU = incidents_UniGram.get(inc);
				double cosineByResolve = returnCosine(expertise_res_UniGram, incVectorU);
				double cosineByTransfer = returnCosine(expertise_trans_UniGram, incVectorU);
				
				HashMap<String, Integer> incVectorB = incidents_BiGram.get(inc);
				double lmByResolve = -1 * returnLM(expertise_res_BiGram, incVectorB, resolveLambda, len);
				double lmByTransfer = -1 * returnLM(expertise_trans_BiGram, incVectorB, transferLambda, len);
				
				double p_cos_res = cosineByResolve;
				p_cos_res /= (cosineByResolve + cosineByTransfer);
				double p_cos_trans = 1.0-p_cos_res;
				
				double p_lm_res = Math.exp(lmByResolve);
				p_lm_res /= (Math.exp(lmByResolve) + Math.exp(lmByTransfer));
				double p_lm_trans = 1.0 - p_lm_res;
				
				double p_res = (Math.abs(p_cos_res-0.5) > Math.abs(p_lm_res-0.5)) ? p_cos_res : p_lm_res;
				double p_trans = 1.0-p_res;
				
				if(p_trans > p_res)
					bw.write(inc + "," + exp + "," + "Trans" + "," + "Trans" + "," + lmByResolve + "," + lmByTransfer + "," + 
							cosineByResolve + "," + cosineByTransfer + "," + p_lm_res + "," + p_lm_trans + "," + p_cos_res + 
							"," + p_cos_trans + "," + fold + "\n");
				else
					bw.write(inc + "," + exp + "," + "Trans" + "," + "Res" + "," + lmByResolve + "," + lmByTransfer + "," + 
							cosineByResolve + "," + cosineByTransfer + "," + p_lm_res + "," + p_lm_trans + "," + p_cos_res + 
							"," + p_cos_trans + "," + fold + "\n");
			}
	}
}
