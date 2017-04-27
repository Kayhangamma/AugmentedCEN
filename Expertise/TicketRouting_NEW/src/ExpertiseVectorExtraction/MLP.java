package ExpertiseVectorExtraction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import ExpertiseVectorExtraction.Node.Input;
import ExpertiseVectorExtraction.Node.Hidden;

import java.util.Map.Entry;

public class MLP {
	static boolean useUniGram = false;
	
	public static void main(String[] args) throws IOException {
		// Set Train Ratio for to Create Train and Dev Sets 
		double trainRation = 0.9;
		int M = 10; //this value will be used in time of creating Dev and Train sets, where experts with less than M resolved/transferred incident won't be used for dev set. 
		
		// Load incident vectors
		HashMap<String, HashMap<Integer, Double>> incidents = loadVectors("I", useUniGram, "");
		
		// Load Initial Expertise Vector
//		HashMap<String, HashMap<Integer, Double>> expertise = loadVectors("E", useUniGram, "R");
		
		// Load Expert to Resolved Incidents
		HashMap<String, HashMap<String, Boolean>> expertToIncidentsR = loadExpertToIncident(true);
		
		// Load Expert to Transferred Incidents
		HashMap<String, HashMap<String, Boolean>> expertToIncidentsT = loadExpertToIncident(false);
		
		// Specify Train and Dev Sets --> We have already specified Train and Dev sets! Don't need followings anymore. 
//		expertToIncidentsR = createTrainAndDevSets(expertToIncidentsR, trainRation, M);
//		expertToIncidentsT = createTrainAndDevSets(expertToIncidentsT, trainRation, M);
		
		
		// Normalize Feature Values: It seems that normalization is not a good idea!
//		expertise = normalizeFeatures_zScore(expertise);
		incidents = normalizeFeatures_zScore(incidents);
		
//		ArrayList<String> trainPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, true, "NSC-IDADMIN-AGENCY");
//		ArrayList<String> devPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, false, "NSC-IDADMIN-AGENCY");	
		
//		System.out.println("Jackard Similarity: ");
//		semiJaccardSimilarity(devPairs, incidents, expertise, false);
//		
//		System.out.println("\n\nCosine Similarity: ");
//		countCommonElements_Cosine(devPairs, incidents, expertise, false);
		
		
		// Learning parameters setup (heuristically) 
		double learningRate = 0.3;
		double momentum = 0.9;
		int maxEpochs = 10;		
		double maxError = 0.2; //Network update criteria
		
		// Get the Number of features	
		int N = getFeatureFrequency(useUniGram);
//		System.out.print("Enter number of hidden nodes: ");
//		String input = System.console().readLine();		
//		int H_N = Integer.parseInt(input);
		int H_N = 50;
		int I_N = 2*N+1; //14524 + 1. We considered one more for bias value.
		
		//Call back-propagation
//		backPropagation_2L_Net(incidents, expertise, expertToIncidentsR, expertToIncidentsT, learningRate, 
//				momentum, maxEpochs, maxError, N, I_N, H_N);
		
		List<String> listOfExperts = Files.readAllLines
				(Paths.get("C:/Users/sobhan/Desktop/Kayhan's Project/data/Data About Expert Selection By Kayhan/SolidGroups.txt"));
		for(String exp:listOfExperts){
//			writeFeatureVectorsForWeka(incidents, expertise, expertToIncidentsR, expertToIncidentsT, exp, N);
			writeFeatureVectorsForScikit(incidents, expertToIncidentsR, expertToIncidentsT, exp, N);
			System.out.println(exp + " is done!");
		}
		
	}

	public static HashMap<String, HashMap<Integer, Double>> loadVectors(String type, boolean uniOrBi, String R_or_T) throws IOException{
		// type: E stands for Expert, and I stands for Incidents
		// uniOrBi as true means the type of vector should be Unigram. 
		// R_or_T: 'R' stands for loading expertise vectors based on resolved incidents. 'T', stands for loading expertise based on transferred incidents. 
		// The last parameter just work for experts
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
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
	
	public static HashMap<String, HashMap<String, Boolean>> loadExpertToIncident(boolean resolved) throws IOException{
		//'resolved' as true means this function should return expert-to-incidents mapping based on incidents that an expert resolved.
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		List<String> lines = null;
		if(resolved)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved_TD.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred_TD.txt"));
		
		HashMap<String, HashMap<String, Boolean>> expertToIncidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			HashMap<String, Boolean> incidents = new HashMap<>();
			String[] tickets = lines.get(i).split("\t")[1].split(",");			
			for(int j=0;j<tickets.length;j++){
				String[] parts = tickets[j].split("=");
				if(parts[1].equals("T"))
					incidents.put(parts[0], true); //belongs to train set
				else
					incidents.put(parts[0], false); //belongs to dev set
			}
			expertToIncidents.put(lines.get(i).split("\t")[0], incidents);
		}
		return expertToIncidents;
	}

	public static HashMap<String, HashMap<String, Boolean>> createTrainAndDevSets(
			HashMap<String, HashMap<String, Boolean>> expertToIncidents, double trainRation, int M){
	
		HashMap<String, HashMap<String, Boolean>> expertToIncidents_TrainDev = new HashMap<>();
		Iterator it = expertToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> exp = (Entry<String, HashMap<String, Boolean>>) it.next();
			if(exp.getValue().size() < M){
				expertToIncidents_TrainDev.put(exp.getKey(), exp.getValue());
			}
			else{
				HashMap<String, Boolean> train_dev = new HashMap<>();
				Iterator it2 = exp.getValue().entrySet().iterator();
				while(it2.hasNext()){
					Map.Entry<String, Boolean> inc = (Entry<String, Boolean>) it2.next();
					double rand = (new Random()).nextDouble();
					if(rand >= trainRation)
						train_dev.put(inc.getKey(), false); //'false' means this incident will be considered in dev set				
					else
						train_dev.put(inc.getKey(), true); //'true' means this incident will be considered in train set					
				}
				expertToIncidents_TrainDev.put(exp.getKey(), train_dev);
			}
		}		
		return expertToIncidents_TrainDev;
	}
	
	public static List<HashMap<String, HashMap<Integer, Double>>> normalizeFeatures_MinMax(HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertise){
		
		//find the Minimum and Maximum values
		HashMap<Integer, Double> maxValues = new HashMap<>();
		HashMap<Integer, Double> minValues = new HashMap<>();
		
		//examine incidents
		Iterator it = incidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<Integer, Double>> inc = (Entry<String, HashMap<Integer, Double>>) it.next();
			Iterator it2 = inc.getValue().entrySet().iterator();
			
			while(it2.hasNext()){
				Map.Entry<Integer, Double> ft = (Entry<Integer, Double>) it2.next();
				if(!maxValues.containsKey(ft.getKey())){
					maxValues.put(ft.getKey(), ft.getValue());
					minValues.put(ft.getKey(), ft.getValue());
				}
				else{
					if(ft.getValue() > maxValues.get(ft.getKey()))
						maxValues.put(ft.getKey(), ft.getValue());
					if(ft.getValue() < minValues.get(ft.getKey()))
						minValues.put(ft.getKey(), ft.getValue());
				}
			}	
		}
		
		//examine expertise vectors based on resolved incidents
		it = expertise.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<Integer, Double>> exp = (Entry<String, HashMap<Integer, Double>>) it.next();
			Iterator it2 = exp.getValue().entrySet().iterator();
			
			while(it2.hasNext()){
				Map.Entry<Integer, Double> ft = (Entry<Integer, Double>) it2.next();
				if(!maxValues.containsKey(ft.getKey())){
					maxValues.put(ft.getKey(), ft.getValue());
					minValues.put(ft.getKey(), ft.getValue());
				}
				else{
					if(ft.getValue() > maxValues.get(ft.getKey()))
						maxValues.put(ft.getKey(), ft.getValue());
					if(ft.getValue() < minValues.get(ft.getKey()))
						minValues.put(ft.getKey(), ft.getValue());
				}
			}	
		}
				
		//Use Min-Max normalization to normalize feature values across train and dev sets.
		HashMap<String, HashMap<Integer, Double>> incidentsNormalized = new HashMap<>();
		HashMap<String, HashMap<Integer, Double>> expertiseNormalized = new HashMap<>();
		
		it = incidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<Integer, Double>> inc = (Entry<String, HashMap<Integer, Double>>) it.next();
			Iterator it2 = inc.getValue().entrySet().iterator();
			HashMap<Integer, Double> inc_n = new HashMap<>();
			while(it2.hasNext()){
				Map.Entry<Integer, Double> ft = (Entry<Integer, Double>) it2.next();
				double ft_n = (ft.getValue()-minValues.get(ft.getKey()))/(maxValues.get(ft.getKey())-minValues.get(ft.getKey()));
				if(maxValues.get(ft.getKey()) == minValues.get(ft.getKey()))
					ft_n = 1;
				inc_n.put(ft.getKey(), ft_n);
			}	
			incidentsNormalized.put(inc.getKey(), inc_n);
		}
		
		it = expertise.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<Integer, Double>> exp = (Entry<String, HashMap<Integer, Double>>) it.next();
			Iterator it2 = exp.getValue().entrySet().iterator();
			HashMap<Integer, Double> exp_n = new HashMap<>();
			while(it2.hasNext()){
				Map.Entry<Integer, Double> ft = (Entry<Integer, Double>) it2.next();
				double ft_n = (ft.getValue()-minValues.get(ft.getKey()))/(maxValues.get(ft.getKey())-minValues.get(ft.getKey()));
				if(maxValues.get(ft.getKey()) == minValues.get(ft.getKey()))
					ft_n = 1;
				exp_n.put(ft.getKey(), ft_n);
			}	
			expertiseNormalized.put(exp.getKey(), exp_n);
		}
						
		//Return statement
		List<HashMap<String, HashMap<Integer, Double>>> tbr = new ArrayList<>();
		tbr.add(incidentsNormalized);
		tbr.add(expertiseNormalized);
		return tbr; //TBR --> To Be Returned!
	}

	public static HashMap<String, HashMap<Integer, Double>> normalizeFeatures_zScore(HashMap<String, HashMap<Integer, Double>> vectors){
		
		//Calculating sum and count for each feature
		HashMap<Integer, Double> sum_mean = new HashMap<>();
		
		//calculate sum values
		Set<String> ids = vectors.keySet();
		for(String id: ids){
			Set<Integer> features = vectors.get(id).keySet();
			for(int f:features){
				if(!sum_mean.containsKey(f))
					sum_mean.put(f, vectors.get(id).get(f));
				else
					sum_mean.put(f, sum_mean.get(f)+vectors.get(id).get(f));
			}
		}		

		//calculate the mean values for features 
		Set<Integer> fts = sum_mean.keySet();
		for(int f:fts)
			sum_mean.put(f, sum_mean.get(f)/vectors.size());		
		
		//calculate std for features
		HashMap<Integer, Double> sum_std = new HashMap<>();
		HashMap<Integer, Integer> count = new HashMap<>();
		
		ids = vectors.keySet();
		for(String id: ids){
			Set<Integer> features = vectors.get(id).keySet();
			for(int f:features){
				double value = Math.pow(vectors.get(id).get(f) - sum_mean.get(f), 2);
				if(!sum_std.containsKey(f)){
					sum_std.put(f, value);
					count.put(f, 1);
				}
				else{
					sum_std.put(f, sum_std.get(f)+value);
					count.put(f, count.get(f)+1);
				}
			}
		}	
		
		//calculate the std values for features 
		fts = sum_std.keySet();
		for(int f:fts){
			double remain = (vectors.size() - count.get(f))*Math.pow(sum_mean.get(f), 2); //this reflect absent values which are considered as zero!		
			sum_std.put(f, Math.sqrt((sum_std.get(f)+remain)/vectors.size())); //division by size of vectors is to take all possible values into consideration. 
		}
		
		//At this point, we have both mean and standard deviation for each feature!
		
		//Use Z-Score normalization to normalize feature values across train and dev sets.
		HashMap<String, HashMap<Integer, Double>> normalizedVectors = new HashMap<>();
		
		ids = vectors.keySet();
		for(String id: ids){
			Set<Integer> features = vectors.get(id).keySet();
			HashMap<Integer, Double> vec = new HashMap<>();
			for(int f:features){
				double mean = sum_mean.get(f), std = sum_std.get(f);
				double ft_n = vectors.get(id).get(f) - mean;
				if(std > 0)
					ft_n = (ft_n/std) + (mean/std); //the added value of (mean/std) is to deal with zero-valued features
					//I tried to shift normal values by a unit to make the minimum number in normal scale as Zero! instead of a negative one. 
					//In this way, since we have sparse vectors, the zero-valued features in original vectors are already normalized!
				vec.put(f, ft_n);
			}
			normalizedVectors.put(id, vec);
		}		

		return normalizedVectors;
	}
	
	private static int getFeatureFrequency(boolean uniOrBi) throws IOException {
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/BaseLine Data/";
		List<String> lines = null;
		if(uniOrBi)
			lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		return (lines.size()-1);
	}

	public static double sigmoid(double in){
		//this is activation function: logistic sigmoid, with a = 1
		double out = 1.0 / (1.0 + Math.exp(-in));
		return out;
	}
	
	public static ArrayList<Object> BP_Train_Process(Input[] inputs, Hidden[] hiddens, Hidden output, ArrayList<String> trainPairs, 
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<Integer, Double>> expertise, 
			double learningRate, double momentum, double maxError, int N){
		
		boolean updateFlag = false;
		int numOfError = 0;
		int total = 0;
		int cnt = 0;
		
		int t_0_p_1 = 0;
		int t_1_p_0 = 0;
		
		for(String pair:trainPairs){
			String[] parts = pair.split("\\$");
			cnt++;
			HashMap<Integer, Double> exp = expertise.get(parts[0]);
			HashMap<Integer, Double> inc = incidents.get(parts[1]);
			double label = Double.parseDouble(parts[2]);
			
			if(exp==null || inc==null)
				continue;
			
			HashSet<Integer> nonZeroInput = new HashSet<>();
			
			inputs[0].value = 1.0; //bias
			nonZeroInput.add(0);			
			for(int i=1;i<inputs.length;i++)
				inputs[i].value = 0;
			
			Set<Integer> features = exp.keySet();
			for(int ft:features){
				inputs[ft].value = exp.get(ft);
				nonZeroInput.add(ft);
			}
			
			features = inc.keySet();
			for(int ft:features){
				inputs[N + ft].value = inc.get(ft);
				nonZeroInput.add(N+ft);
			}
			
			//Hidden Layer Calculation
			hiddens[0].y = 1.0; //this is bias
			for(int i=1;i<hiddens.length;i++){
				hiddens[i].v = 0;
				for(int j:nonZeroInput)
					hiddens[i].v += inputs[j].value * inputs[j].weights[i-1]; 
				hiddens[i].y = sigmoid(hiddens[i].v);
			}					
			
			//Output Layer Calculation
			output.v = 0;
			for(int i=0; i < hiddens.length; i++)
				output.v += hiddens[i].y * hiddens[i].weight;
			output.y = sigmoid(output.v);
			
			//Calculate the error value
			double error = label - output.y;			
			total++;
			if(Math.abs(error) > maxError){ //this means, we need to update the network
				numOfError++;
				updateFlag = true; //this is not the last epoch! no early stop can be applied
				
				if(label==0)
					t_0_p_1++;
				else
					t_1_p_0++;
				
				//Computing deltaK
				double deltaK = error * sigmoid(output.v) * (1.0 - sigmoid(output.v)); //DeltaK = ek * phi'(vk)
				if(deltaK != 0)
					deltaK =deltaK;
				
				//Update W(kj)
				for(int j=0; j < hiddens.length; j++){
					hiddens[j].oldWeight = hiddens[j].weight;
					double weightChange = learningRate*deltaK*hiddens[j].y + (momentum * hiddens[j].lastWeightUpdate);
					hiddens[j].weight += weightChange;
					hiddens[j].lastWeightUpdate = weightChange;
				}
				
				//Update W(ji)
				for(int l:nonZeroInput){
					for(int j=0; j <inputs[l].weights.length; j++){
						double deltaJ = sigmoid(hiddens[j+1].v) * (1.0 - sigmoid(hiddens[j+1].v)) * 
								hiddens[j+1].oldWeight * deltaK;
						double weightChange = learningRate*deltaJ*inputs[l].value + (momentum * inputs[l].lastWeightChanges[j]);
						inputs[l].weights[j] += weightChange;
						inputs[l].lastWeightChanges[j] = weightChange;
					}
				}						
			}			
		}		
	
		ArrayList<Object> tbr = new ArrayList<>();
		tbr.add(inputs);
		tbr.add(hiddens);
		tbr.add(updateFlag);
		tbr.add(numOfError);
		tbr.add(total);
		return tbr;
	}
	
	public static double reportErrorOnDevSet(Input[] inputs, Hidden[] hiddens, Hidden output, 
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<Integer, Double>> expertise, 
			ArrayList<String> devPairs, double maxError, int N){
		
		//initialize Mean Squared Error (MSR)
		double errorSum = 0;
		double numOfPatterns = 0;		
		
		for(String pair:devPairs){
			String[] parts = pair.split("\\$");
			
			HashMap<Integer, Double> exp = expertise.get(parts[0]);
			HashMap<Integer, Double> inc = incidents.get(parts[1]);
			double label = Double.parseDouble(parts[2]);
			
			if(exp==null || inc==null)
				continue;
			
			HashSet<Integer> nonZeroInput = new HashSet<>();
			
			inputs[0].value = 1.0; //bias
			nonZeroInput.add(0);			
			for(int i=1;i<inputs.length;i++)
				inputs[i].value = 0;
			
			Set<Integer> features = exp.keySet();
			for(int ft:features){
				inputs[ft].value = exp.get(ft);
				nonZeroInput.add(ft);
			}
			
			features = inc.keySet();
			for(int ft:features){
				inputs[N + ft].value = inc.get(ft);
				nonZeroInput.add(N+ft);
			}
			
			//Hidden Layer Calculation
			hiddens[0].y = 1.0; //this is bias
			for(int i=1;i<hiddens.length;i++){
				hiddens[i].v = 0;
				for(int j:nonZeroInput)
					hiddens[i].v += inputs[j].value * inputs[j].weights[i-1]; 
				hiddens[i].y = sigmoid(hiddens[i].v);
			}					
			
			//Output Layer Calculation
			output.v = 0;
			for(int i=0; i < hiddens.length; i++)
				output.v += hiddens[i].y * hiddens[i].weight;
			output.y = sigmoid(output.v);
			
			//Calculate the error value
			double error = label - output.y;			
			numOfPatterns++;
			if(Math.abs(error) > maxError)
				errorSum += 1.0;					
		}
		
		double mse = Math.round((errorSum/numOfPatterns)*1000)/1000.000;		
		return mse;
	}

	public static void backPropagation_2L_Net(HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertise, HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, double learningRate, double momentum, 
			int maxEpochs, double maxError, int N, int I_N, int H_N) throws IOException{
		
		//** Code for a 2 Layer Network with Back-Propagation using Gradient Descent training Algorithm **		
		//get the train pairs
		ArrayList<String> trainPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, true);
		//get dev pairs
		ArrayList<String> devPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, false);
				
		Input[] inputs = new Input[I_N];
		Hidden[] hiddens = new Hidden[H_N + 1];
		Hidden output = new Hidden();
		
		//Initialization of weights
		//setting normalization rates
		double upFactor = 1.0;
		double downFactor = 0.0;
		
		Random rnd = new Random();
		for(int i=0;i<inputs.length;i++){
			inputs[i] = new Input();
			inputs[i].value = 0;
			inputs[i].weights = new double[H_N];
			inputs[i].lastWeightChanges = new double[H_N];
			for(int j=0;j<inputs[i].weights.length;j++)
				inputs[i].weights[j] = rnd.nextDouble() * upFactor - downFactor;
		}
		
		for(int i=0;i<hiddens.length;i++){
			hiddens[i] = new Hidden();
			hiddens[i].weight = rnd.nextDouble() * upFactor - downFactor;
		}

		int epoch = 0;
		double totalError = Double.MAX_VALUE;
		int numOfError = 0;
		
		while(epoch<maxEpochs){
			epoch++;
			numOfError = 0;
			int total = 0;
			
			long seed = System.nanoTime();			
			Collections.shuffle(trainPairs, new Random(seed));
			
			long cur = System.currentTimeMillis();			
			boolean updateFlag = false;
			
			System.out.println("Train started for epoch: " + epoch);
			
			ArrayList<Object> tbr = BP_Train_Process(inputs, hiddens, output, trainPairs, incidents, expertise,   
					learningRate, momentum, maxError, N);
			inputs = (Input[]) tbr.get(0);
			hiddens = (Hidden[]) tbr.get(1);
			updateFlag = (boolean) tbr.get(2);
			numOfError += (int)tbr.get(3);
			total += (int)tbr.get(4);					
			
			double err = Math.round((numOfError/(double)total)*1000.000)/1000.000;
			double devError = reportErrorOnDevSet(inputs, hiddens, output, incidents, expertise, devPairs, maxError, N);
			System.out.println("Epoch: " + epoch + "\tTrainError: " + err + "\tDevError: " + devError + "\tTime: "+ (System.currentTimeMillis()-cur)/1000 + " sec");
			
			if(!updateFlag)
				break;
		}		
	
	}

	public static ArrayList<String> prepareTrainDevPairs(HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, boolean isTrain){
		//isTrain: true means train related data; false means development related data
		
		ArrayList<String> trainPairs = new ArrayList<>();
		
		Set<String> keys = expertToIncidentsR.keySet();
		for(String s:keys){
			Set<String> inc = expertToIncidentsR.get(s).keySet();
			for(String t:inc)
				if(expertToIncidentsR.get(s).get(t) == isTrain) //this means such pair belongs to train set
					trainPairs.add(s + "$" + t + "$1");
		}
		
		keys = expertToIncidentsT.keySet();
		for(String s:keys){
			Set<String> inc = expertToIncidentsT.get(s).keySet();
			for(String t:inc)
				if(expertToIncidentsT.get(s).get(t) == isTrain) //this means such pair belongs to train set
					trainPairs.add(s + "$" + t + "$0");
		}
		
		return trainPairs;
	}
	
	public static ArrayList<String> prepareTrainDevPairs(HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, boolean isTrain, String exp){
		//isTrain: true means train related data; false means development related data
		
		ArrayList<String> trainPairs = new ArrayList<>();
		
		Set<String> keys = expertToIncidentsR.keySet();
		for(String s:keys){
			if(!s.equals(exp))
				continue;
			
			Set<String> inc = expertToIncidentsR.get(s).keySet();
			for(String t:inc)
				if(expertToIncidentsR.get(s).get(t) == isTrain) //this means such pair belongs to train set
					trainPairs.add(s + "$" + t + "$1");
		}
		
		keys = expertToIncidentsT.keySet();
		for(String s:keys){
			if(!s.equals(exp))
				continue;
			
			Set<String> inc = expertToIncidentsT.get(s).keySet();
			for(String t:inc)
				if(expertToIncidentsT.get(s).get(t) == isTrain) //this means such pair belongs to train set
					trainPairs.add(s + "$" + t + "$0");
		}
		
		return trainPairs;
	}
	
	public static void semiJaccardSimilarity(ArrayList<String> pairs, 
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<Integer, Double>> expertise, boolean ResolveExpertise) throws IOException{
					
		int l_1 = 0;
		int l_0 = 0;
		
		double l_1_sum = 0, l_0_sum=0;
		BufferedWriter bw;
		if(ResolveExpertise)
				bw = new BufferedWriter(new FileWriter("ExpertiseByResolve_JaccardSimilaritySheet.csv"));
		else
			bw = new BufferedWriter(new FileWriter("ExpertiseByTransfer_JaccardSimilaritySheet.csv"));
		
		bw.write("Similarity,Res/Tran,GroupId,Incident\n");
		
		for(String pair:pairs){
			String[] parts = pair.split("\\$");
			HashMap<Integer, Double> exp = expertise.get(parts[0]);
			HashMap<Integer, Double> inc = incidents.get(parts[1]);
			double label = Double.parseDouble(parts[2]);
			
			if(exp==null || inc==null)
				continue;
			
			Set<Integer> features = inc.keySet();
			int cmn = 0;
			for(int f:features)
				if(exp.containsKey(f))
					cmn++;
			double ratio = cmn/(double)features.size();
			String TorR = "";
			
			if(label == 1){
				l_1++;
				l_1_sum += ratio;
				TorR = "R";
			}
			else{
				l_0++;
				l_0_sum += ratio;
				TorR = "T";
			}
			
			bw.write(ratio + "," + TorR + "," + parts[0] + "," + parts[1] + "\n");
		}
		
		bw.close();		
		System.out.println("Common Features for Resolved Incidents: " + l_1_sum/l_1);
		System.out.println("Common Features for Transferred Incidents: " + l_0_sum/l_0);
	}
	
	public static void countCommonElements_Cosine(ArrayList<String> pairs, 
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<Integer, Double>> expertise, boolean ResolveExpertise) throws IOException{
					
		int count_L1 = 0;
		int count_L0 = 0;
		
		double L1_cosine_sum = 0;
		double L0_cosine_sum = 0;
		
		BufferedWriter bw;
		if(ResolveExpertise)
				bw = new BufferedWriter(new FileWriter("ExpertiseByResolve_CosineSimilaritySheet.csv"));
		else
			bw = new BufferedWriter(new FileWriter("ExpertiseByTransfer_CosineSimilaritySheet.csv"));
		
		bw.write("Similarity,Res/Tran,GroupId,Incident\n");
		
		for(String pair:pairs){
			String[] parts = pair.split("\\$");
			HashMap<Integer, Double> exp = expertise.get(parts[0]);
			HashMap<Integer, Double> inc = incidents.get(parts[1]);
			double label = Double.parseDouble(parts[2]);
			
			if(exp==null || inc==null)
				continue;
						
//			double nominator = 0;
//			double exp_size = 0, inc_size =0;
//			Set<Integer> features = exp.keySet();
//			for(int f:features){
//				if(inc.containsKey(f))
//					nominator += exp.get(f)*inc.get(f);
//				exp_size += Math.pow(exp.get(f), 2);
//			}
//			features = inc.keySet();
//			for(int f:features)
//				inc_size += Math.pow(inc.get(f), 2);
			
			double nominator = 0;
			double exp_size = 0, inc_size =0;
			Set<Integer> features = inc.keySet();
			for(int f:features){
				if(exp.containsKey(f)){
					nominator += exp.get(f)*inc.get(f);
					exp_size += Math.pow(exp.get(f), 2);
				}
				inc_size += Math.pow(inc.get(f), 2);
			}
			
			double cosine = nominator;
			if(exp_size > 0)
				cosine /= (Math.sqrt(exp_size)*Math.sqrt(inc_size));
			else 
				cosine = 0;
			
			String TorR = "";
			
			if(label == 0){
				count_L0++;
				L0_cosine_sum += cosine;
				TorR = "T";
			}
			else{
				count_L1++;
				L1_cosine_sum += cosine;
				TorR = "R";
			}
			
			bw.write(cosine + "," + TorR + "," + parts[0] + "," + parts[1] + "\n");
		}		
		
		bw.close();
		System.out.println("Common Features for Resolved Incidents: " + L1_cosine_sum/count_L1);
		System.out.println("Common Features for Transferred Incidents: " + L0_cosine_sum/count_L0);		
	}
	
	public static void writeFeatureVectorsForWeka(HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertise, HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, String exp, int N) throws IOException{
	
		ArrayList<String> trainPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, true, exp);
		ArrayList<String> devPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, false, exp);
		
		ArrayList<String> allPairs = new ArrayList<>();
		allPairs.addAll(trainPairs);
		allPairs.addAll(devPairs);
		Collections.shuffle(allPairs);
		
		TreeSet<Integer> ids = new TreeSet<>();
		for(String pair:allPairs){
			String[] parts = pair.split("\\$");
			HashMap<Integer, Double> e = expertise.get(parts[0]);
			HashMap<Integer, Double> t = incidents.get(parts[1]);
			if(t==null)
				continue;
			for(int i=1;i<=N;i++)
				if(e.containsKey(i) || t.containsKey(i))
					ids.add(i);
		}
		
		N = ids.size();
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/sobhan/Desktop/Kayhan's Project/data/For Weka/" + exp + ".arff"));
		bw.write("@relation '" + exp + "'\n");		
//		for(int i=1;i<=2*N;i++)
		for(int i=1;i<=N;i++)
			bw.write("@attribute Feat-" + i + " real\n");
		bw.write("@attribute class {0,1}\n");
		bw.write("@data\n");
		
		for(String pair:allPairs){
			String[] parts = pair.split("\\$");
			HashMap<Integer, Double> e = expertise.get(parts[0]);
			HashMap<Integer, Double> t = incidents.get(parts[1]);
			if(t == null)
				continue;
			
			StringBuilder sb = new StringBuilder();
//			for(int i:ids){				
//				if(e.containsKey(i))
//					sb.append("," + e.get(i));
//				else
//					sb.append(",0");
//			}
//			for(int i:ids){
//				if(t.containsKey(i))
//					sb.append("," + t.get(i));
//				else
//					sb.append(",0");
//			}
			
			for(int i:ids){				
				if(e.containsKey(i) && t.containsKey(i))
					sb.append("," + e.get(i)*t.get(i));
				else
					sb.append(",0");
			}
			bw.write(sb.toString().substring(1) + "," + parts[2] + "\n");			
		}
		
		bw.close();
	}
	
	public static void writeFeatureVectorsForScikit(HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, String exp, int N) throws IOException{
	
		ArrayList<String> trainPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, true, exp);
		ArrayList<String> devPairs = prepareTrainDevPairs(expertToIncidentsR, expertToIncidentsT, false, exp);
		
		ArrayList<String> allPairs = new ArrayList<>();
		allPairs.addAll(trainPairs);
		allPairs.addAll(devPairs);
		Collections.shuffle(allPairs);
		
		TreeSet<Integer> ids = new TreeSet<>();
		for(String pair:allPairs){
			String[] parts = pair.split("\\$");
//			HashMap<Integer, Double> e = expertise.get(parts[0]);
			HashMap<Integer, Double> t = incidents.get(parts[1]);
			if(t==null)
				continue;
			for(int i=1;i<=N;i++)
//				if(e.containsKey(i) || t.containsKey(i))
				if(t.containsKey(i))
					ids.add(i);
		}
	
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/sobhan/Desktop/Kayhan's Project/data/For Scikit/BiGram/" + exp + ".txt"));	
		for(String pair:allPairs){
			String[] parts = pair.split("\\$");
//			HashMap<Integer, Double> e = expertise.get(parts[0]);
			HashMap<Integer, Double> t = incidents.get(parts[1]);
			if(t == null)
				continue;
			
			StringBuilder sb = new StringBuilder();
			
//			for(int i:ids){				
//				if(e.containsKey(i))
//					sb.append("," + e.get(i));
//				else
//					sb.append(",0");
//			}
			for(int i:ids){
				if(t.containsKey(i))
					sb.append("," + t.get(i));
				else
					sb.append(",0");
			}
			
//			for(int i:ids){				
//				if(e.containsKey(i) && t.containsKey(i))
//					sb.append("," + e.get(i)*t.get(i));
//				else
//					sb.append(",0");
//			}
			bw.write(sb.toString().substring(1) + "\t" + parts[2] + "\n");			
		}
		
		bw.close();
	}
}