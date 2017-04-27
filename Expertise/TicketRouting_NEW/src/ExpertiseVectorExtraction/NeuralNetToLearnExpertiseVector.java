package ExpertiseVectorExtraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

public class NeuralNetToLearnExpertiseVector {
	
	public static void main(String[] args) throws IOException {
		// UniGram or BiGram
		boolean useUniGram = true;
		
		// Set Train Ratio for to Create Train and Dev Sets 
		double trainRation = 0.9;
		int M = 20; //this value will be used in time of creating Dev and Train sets, where experts with less than M resolved/transferred incident won't be used for dev set. 
		
		// Load incident vectors
		HashMap<String, HashMap<Integer, Double>> incidents = loadVectors("I", useUniGram, "");
		
		// Load Initial Expertise Vector
		HashMap<String, HashMap<Integer, Double>> expertiseR = loadVectors("E", useUniGram, "R");
		HashMap<String, HashMap<Integer, Double>> expertiseT = loadVectors("E", useUniGram, "T");
		
		// Load Expert to Resolved Incidents
		HashMap<String, HashMap<String, Boolean>> expertToIncidentsR = loadExpertToIncident(true);
		
		// Load Expert to Transferred Incidents
		HashMap<String, HashMap<String, Boolean>> expertToIncidentsT = loadExpertToIncident(false);
		
		// Specify Train and Dev Sets
		expertToIncidentsR = createTrainAndDevSets(expertToIncidentsR, trainRation, M);
		expertToIncidentsT = createTrainAndDevSets(expertToIncidentsT, trainRation, M);
		
		// Normalize Feature Values: It seems that normalization is not a good idea!
//		List<HashMap<String, HashMap<Integer, Double>>> tbr = normalizeFeatures(incidents, expertiseR, expertiseT);
//		incidents = tbr.get(0); expertiseR = tbr.get(1); expertiseT = tbr.get(2);
		
		// Get the Number of features
		int N = getFeatureFrequency(useUniGram);
		
		// Network initial setup
		NeuralNet NN = new NeuralNet(N);

		// Learning parameters setup (heuristically) 
		double learningRate = 0.4;
		double momentum = 0.9;
		int maxEpochs = 10;
		int maxAlternates = 1;
		double maxError = 0.1;
		
		for(int i=0;i<maxAlternates;i++){
			System.out.println("\nIteration: " + (i+1) + "\n");
			
			//Network Train for Maximization
			NN = Maximization_NetworkTrain(NN, incidents, expertiseR, expertiseT, expertToIncidentsR, expertToIncidentsT, N, learningRate, 
					momentum, maxEpochs, maxError);
			System.out.println();
			
			//Network Train for Expectation
			ArrayList<HashMap<String, HashMap<Integer, Double>>> tbr_exp = Expectation_NetworkTrain(NN, incidents, expertiseR, expertiseT, expertToIncidentsR, expertToIncidentsT, N, 
					learningRate, momentum, maxEpochs, maxError);
			expertiseR = tbr_exp.get(0);
			expertiseT = tbr_exp.get(1);
			System.out.println();
		}
				
	}

	public static HashMap<String, HashMap<Integer, Double>> loadVectors(String type, boolean uniOrBi, String R_or_T) throws IOException{
		// type: E stands for Expert, and I stands for Incidents
		// uniOrBi as true means the type of vector should be Unigram. 
		// R_or_T: 'R' stands for loading expertise vectors based on resolved incidents. 'T', stands for loading expertise based on transferred incidents. 
		// The last parameter just work for experts
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
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
		
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
		List<String> lines = null;
		if(resolved)
			lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "ExpertToTranferred.txt"));
		
		HashMap<String, HashMap<String, Boolean>> expertToIncidents = new HashMap<>();
		for(int i=1;i<lines.size();i++){
			HashMap<String, Boolean> incidents = new HashMap<>();
			String[] tickets = lines.get(i).split("\t")[1].split(",");			
			for(int j=0;j<tickets.length;j++)
				incidents.put(tickets[j], true); //Here, 'true' indicates all sample are part of Train set by default. 
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
	
	public static List<HashMap<String, HashMap<Integer, Double>>> normalizeFeatures(HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertiseR, HashMap<String, HashMap<Integer, Double>> expertiseT){
		
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
		it = expertiseR.entrySet().iterator();
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
		
		//examine expertise vectors based on transferred incidents
		it = expertiseT.entrySet().iterator();
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
		HashMap<String, HashMap<Integer, Double>> expertiseRNormalized = new HashMap<>();
		HashMap<String, HashMap<Integer, Double>> expertiseTNormalized = new HashMap<>();
		
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
		
		it = expertiseR.entrySet().iterator();
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
			expertiseRNormalized.put(exp.getKey(), exp_n);
		}
		
		it = expertiseT.entrySet().iterator();
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
			expertiseTNormalized.put(exp.getKey(), exp_n);
		}
		
		//Return statement
		List<HashMap<String, HashMap<Integer, Double>>> tbr = new ArrayList<>();
		tbr.add(incidentsNormalized);
		tbr.add(expertiseRNormalized);
		tbr.add(expertiseTNormalized);
		return tbr; //tbr: To Be Returned!
	}
	
	private static int getFeatureFrequency(boolean uniOrBi) throws IOException {
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
		List<String> lines = null;
		if(uniOrBi)
			lines = Files.readAllLines(Paths.get(path + "usefulWords.txt"));
		else
			lines = Files.readAllLines(Paths.get(path + "usefulBiGrams.txt"));
		return (lines.size()-1);
	}

	public static double logisticRegression(NeuralNet NN, HashMap<Integer, Double> E, HashMap<Integer, Double> I){
		double r = 0;
		
		Iterator it = E.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> e = (Entry<Integer, Double>) it.next();
			r += e.getValue() * NN.E_weight[e.getKey()-1];			
		}
		
		it = I.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> i = (Entry<Integer, Double>) it.next();
			r += i.getValue() * NN.I_weight[i.getKey()-1];
		}
		
		r += NN.b;
		r = 1.0/(1.0 + Math.exp(-r));
				
		return r;
	}

	public static NeuralNet updateWeightVectorGradientDescent(NeuralNet NN, HashMap<Integer, Double> E, HashMap<Integer, Double> I, 
			double rho, double momentum, double error){

		Iterator it = E.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> e = (Entry<Integer, Double>) it.next();
			double updateValue = rho*error*e.getValue() + momentum*NN.E_weight_lastUpdate[e.getKey()-1];			
			NN.E_weight[e.getKey()-1] = NN.E_weight[e.getKey()-1] + updateValue;
			NN.E_weight_lastUpdate[e.getKey()-1] = updateValue;
		}
		
		it = I.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> i = (Entry<Integer, Double>) it.next();
			double updateValue = rho*error*i.getValue() + momentum*NN.I_weight_lastUpdate[i.getKey()-1];
			NN.I_weight[i.getKey()-1] = NN.I_weight[i.getKey()-1] + updateValue;
			NN.I_weight_lastUpdate[i.getKey()-1] = updateValue;
		}

		return NN;
	}
	
	public static double reportErrorOnDevSet(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertiseR, HashMap<String, HashMap<Integer, Double>> expertiseT, 
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsR, HashMap<String, HashMap<String, Boolean>> expertToIncidentsT){
		
		//initialize Mean Squared Error (MSR)
		double errorSum = 0;
		double numOfPatterns = 0;		
		
		//Test based on Resolved_Expertise and Resolved Incidents
		double[] outs = mainDevEvaluationProcess(NN, incidents, expertiseR, expertToIncidentsR, 1.0);
		errorSum += outs[0]; numOfPatterns += outs[1];
		
		//Test based on Resolved_Expertise and Transferred Incidents
		outs = mainDevEvaluationProcess(NN, incidents, expertiseR, expertToIncidentsT, 0.0);
		errorSum += outs[0]; numOfPatterns += outs[1];
		
		//Test based on Transferred_Expertise and Resolved Incidents		
		outs = mainDevEvaluationProcess(NN, incidents, expertiseT, expertToIncidentsR, 0.0);
		errorSum += outs[0]; numOfPatterns += outs[1];
		
		//Test based on Transferred_Expertise and Transferred Incidents
		outs = mainDevEvaluationProcess(NN, incidents, expertiseT, expertToIncidentsT, 1.0);
		errorSum += outs[0]; numOfPatterns += outs[1];
		
		double mse = Math.round((errorSum/numOfPatterns)*1000000)/1000000.000000;		
		return mse;
	}
	
	public static double[] mainDevEvaluationProcess(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertise, HashMap<String, HashMap<String, Boolean>> expertToIncidents, double trueLabel){
		
		//initialize Mean Squared Error (MSR)
		double errorSum = 0;
		double numOfPatterns = 0;	
				
		Iterator it = expertToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> e = (Entry<String, HashMap<String, Boolean>>) it.next();
			HashMap<Integer, Double> exp = expertise.get(e.getKey());
			if(exp == null)
				continue;
			
			Iterator it2 = e.getValue().entrySet().iterator(); 
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				if(t.getValue()) //this means t is a Train pattern, not a Dev one.
					continue;
				HashMap<Integer, Double> inc = incidents.get(t.getKey());
				if(inc == null) //this happens if the content of incident be useless
					continue;
				
				//calculate the net output					
				double o = logisticRegression(NN, exp, inc);
				double error = trueLabel - o; //this is like [d - y], which means the actual value of error, signed version
				errorSum += Math.pow(error, 2); numOfPatterns++;				
			}
		}
		
		double[] outs = {errorSum, numOfPatterns};
		return outs;
	}
	
	public static NeuralNet Maximization_NetworkTrain(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertiseR, HashMap<String, HashMap<Integer, Double>> expertiseT, 
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsR, HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, 
			int N, double learningRate, double momentum, int maxEpochs, double maxError){

		double previousDevMSE = 0;
		int sameOrLargerErrorCount = 0;
		
		for(int ep=0;ep<maxEpochs;ep++){
			
			//initialize Mean Squared Error (MSR)
			double errorSum = 0;
			double numOfPatterns = 0;
			
			int num_of_false_prediction = 0;
			
			//if we find any input pattern which network returns large error for that, then this flag flips! 
			boolean error_flag = false;

			// I. Train based on pair of expert (R_expertise) and resolved incident --> Predict resolve!
			ArrayList<Object> toBeReturned = mainTrainProcess(NN, expertiseR, incidents, expertToIncidentsR, maxError, error_flag, learningRate, momentum, 1.0);
			NN = (NeuralNet) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// II. Train based on pair of expert (R_expertise) and transferred incident --> Predict Non-resolve!
			toBeReturned = mainTrainProcess(NN, expertiseR, incidents, expertToIncidentsT, maxError, error_flag, learningRate, momentum, 0.0);
			NN = (NeuralNet) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// III. Train based on pair of expert (T_expertise) and resolved incident --> Predict Non-Transfer!
			toBeReturned = mainTrainProcess(NN, expertiseT, incidents, expertToIncidentsR, maxError, error_flag, learningRate, momentum, 0.0);
			NN = (NeuralNet) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// IV. Train based on pair of expert (T_expertise) and transferred incident --> Predict Transfer!
			toBeReturned = mainTrainProcess(NN, expertiseT, incidents, expertToIncidentsT, maxError, error_flag, learningRate, momentum, 1.0);
			NN = (NeuralNet) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
				
			if(!error_flag){
				//this is end of learning process
				System.out.println("Coverage is obtained in epoch: " + ep);
				break;
			}
			
			double devMSE = reportErrorOnDevSet(NN, incidents, expertiseR, expertiseT, expertToIncidentsR, expertToIncidentsT);
			
			if(devMSE >= previousDevMSE)
				sameOrLargerErrorCount++;
			else
				sameOrLargerErrorCount = 0;
			previousDevMSE = devMSE;
			
			System.out.println("Maximization ==> Epoch: " + ep + " with MSE: " + Math.round((errorSum/numOfPatterns)*1000000)/1000000.000000 + 
					" Errors: " + num_of_false_prediction + " #Patterns: " + numOfPatterns + " DevMSE: " + devMSE);

			if(sameOrLargerErrorCount == 400)
				return NN;
		}
		
		return NN;
	}

	public static ArrayList<Object>  mainTrainProcess(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> expertise,
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<String, Boolean>> expertToIncidents,
			double maxError, boolean error_flag, double learningRate, double momentum, double trueValue){
		
		ArrayList<Object> tbr = new ArrayList<>();
		double errorSum = 0;
		double numOfPatterns = 0;
		int num_of_false_prediction = 0;
				
		Iterator it = expertToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> e = (Entry<String, HashMap<String, Boolean>>) it.next();
			HashMap<Integer, Double> exp = expertise.get(e.getKey());
			if(exp == null)
				continue;
			
			Iterator it2 = e.getValue().entrySet().iterator(); 
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				if(!t.getValue()) //this means t is a Dev pattern, not a training one
					continue;
				HashMap<Integer, Double> inc = incidents.get(t.getKey());
				if(inc == null) //this happens if the content of incident be useless
					continue;
				
				//calculate the net output					
				double o = logisticRegression(NN, exp, inc);
				double error = trueValue - o; //this is like [d - y], which means the actual value of error, signed version
				errorSum += Math.pow(error, 2); numOfPatterns++;
				if(Math.abs(error) > maxError){
					num_of_false_prediction++;
					error_flag = true;
					NN = updateWeightVectorGradientDescent(NN, exp, inc, learningRate, momentum, error);
				}
			}
		}
		
		tbr.add(NN);
		tbr.add(errorSum);
		tbr.add(numOfPatterns);
		tbr.add(num_of_false_prediction);
		tbr.add(error_flag);
		
		return tbr;
	}
	
	public static HashMap<Integer, HashMap<Boolean, ArrayList<HashMap<Integer, Double>>>> CreatePatternSets(
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<Integer, Double>> expertise, 
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsR, HashMap<String, HashMap<String, Boolean>> expertToIncidentsT){
		//Create Patterns
		HashMap<Integer, HashMap<Boolean, ArrayList<HashMap<Integer, Double>>>> patterns = new HashMap<>();
		int id = 0;
		
		Iterator it = expertToIncidentsR.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> e = (Entry<String, HashMap<String, Boolean>>) it.next();
			HashMap<Integer, Double> exp = expertise.get(e.getKey());
			if(exp == null)
				continue;
			
			Iterator it2 = e.getValue().entrySet().iterator(); 
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				if(!t.getValue()) //this means t is a Dev pattern, not a training one
					continue;
				HashMap<Integer, Double> inc = incidents.get(t.getKey());
				if(inc == null) //this happens if the content of incident be useless
					continue;
				
				HashMap<Boolean, ArrayList<HashMap<Integer, Double>>> pt = new HashMap<>();
				ArrayList<HashMap<Integer, Double>> vectors = new ArrayList<>();
				vectors.add(exp);
				vectors.add(inc);
				pt.put(true, vectors);
				patterns.put(id++, pt);
			}
		}
		
		it = expertToIncidentsT.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> e = (Entry<String, HashMap<String, Boolean>>) it.next();
			HashMap<Integer, Double> exp = expertise.get(e.getKey());
			if(exp == null)
				continue;
			
			Iterator it2 = e.getValue().entrySet().iterator(); 
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				if(!t.getValue()) //this means t is a Dev pattern, not a training one
					continue;
				HashMap<Integer, Double> inc = incidents.get(t.getKey());
				if(inc == null) //this happens if the content of incident be useless
					continue;
				
				HashMap<Boolean, ArrayList<HashMap<Integer, Double>>> pt = new HashMap<>();
				ArrayList<HashMap<Integer, Double>> vectors = new ArrayList<>();
				vectors.add(exp);
				vectors.add(inc);
				pt.put(false, vectors);
				patterns.put(id++, pt);
			}
		}
		
		return patterns;
	}
	
	public static NeuralNet Maximization_NetworkTrain_WithShuffle(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertise, HashMap<String, HashMap<String, Boolean>> expertToIncidentsR,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, int N, double learningRate, double momentum, 
			int maxEpochs, double maxError){

		//Create Patterns
		HashMap<Integer, HashMap<Boolean, ArrayList<HashMap<Integer, Double>>>> patterns = CreatePatternSets(incidents, expertise, expertToIncidentsR, expertToIncidentsT);
		int id = patterns.size();
		
		//Create an ArrayList of ids to be shuffled at the beginning of each epoch
		ArrayList<Integer> ids = new ArrayList<>();
		for(int i=0;i<id;i++)
			ids.add(i);		
		
		for(int ep=0;ep<maxEpochs;ep++){
			
			//initialize Mean Squared Error (MSR)
			double errorSum = 0;
			double numOfPatterns = 0;
			
			int num_of_false_prediction = 0;
			
			//if we find any input pattern which network returns large error for that, then this flag flips! 
			boolean error_flag = false;
		
			//shuffle should happen for each epoch
			Collections.shuffle(ids);		
		
			//Now, train based on shuffled input set.
			//HashMap<Integer, HashMap<Boolean, ArrayList<HashMap<Integer, Double>>>>
			for(int p=0;p<ids.size();p++){
				HashMap<Boolean, ArrayList<HashMap<Integer, Double>>> ptrn = patterns.get(p);
				double output = 0;
				ArrayList<HashMap<Integer, Double>> exp_inc = null;
				if(ptrn.containsKey(true)){
					output = 1.0;
					exp_inc = ptrn.get(true);
				}
				else
					exp_inc = ptrn.get(false);
				
				HashMap<Integer, Double> exp = exp_inc.get(0);
				HashMap<Integer, Double> inc = exp_inc.get(1);
	
				//calculate the net output					
				double o = logisticRegression(NN, exp, inc);
				double error = output - o; //this is like [d - y], which means the actual value of error, signed version
				errorSum += Math.pow(error, 2); numOfPatterns++;
				if(Math.abs(error) > maxError){
					num_of_false_prediction++;
					error_flag = true;
					NN = updateWeightVectorGradientDescent(NN, exp, inc, learningRate, momentum, error);
				}				
			}
			
			if(!error_flag){
				//this is end of learning process
				System.out.println("Coverage is obtained in epoch: " + ep);
				break;
			}
			
			double devMSE = reportErrorOnDevSet(NN, incidents, expertise, expertise, expertToIncidentsR, expertToIncidentsT);
			
			System.out.println("Epoch: " + ep + " with MSE: " + Math.round((errorSum/numOfPatterns)*1000000)/1000000.000000 + 
					" Errors: " + num_of_false_prediction + " #Patterns: " + numOfPatterns + " DevMSE: " + devMSE);
		}
		
		return NN;
	}

	public static HashMap<Integer, Double> updateExpertiseVectorGradientDescent(NeuralNet NN, HashMap<Integer, Double> E, double rho, double error){

		for(int i=0;i<NN.E_weight.length;i++){
			double updateValue = rho*error*NN.E_weight[i];
			if(E.containsKey(i+1))
				E.put(i+1, E.get(i+1)+updateValue);
			else
				E.put(i+1, updateValue);			
		}
		
		return E;
	}
	
	public static ArrayList<HashMap<String, HashMap<Integer, Double>>> Expectation_NetworkTrain(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> incidents, 
			HashMap<String, HashMap<Integer, Double>> expertiseR, HashMap<String, HashMap<Integer, Double>> expertiseT,
			HashMap<String, HashMap<String, Boolean>> expertToIncidentsR, HashMap<String, HashMap<String, Boolean>> expertToIncidentsT, 
			int N, double learningRate, double momentum, int maxEpochs, double maxError){

		double previousDevMSE = 0;
		int sameOrLargerErrorCount = 0;
		ArrayList<HashMap<String, HashMap<Integer, Double>>> tbr = new ArrayList<>();
		
		for(int ep=0;ep<maxEpochs;ep++){
			
			//initialize Mean Squared Error (MSR)
			double errorSum = 0;
			double numOfPatterns = 0;			
			
			int num_of_false_prediction = 0;
			
			//if we find any input pattern which network returns large error for that, then this flag flips! 
			boolean error_flag = false;
		
			// I. Train based on pair of expert (R_expertise) and resolved incident --> Predict resolve!
			ArrayList<Object> toBeReturned = mainExpectationTrainProcess(NN, expertiseR, incidents, expertToIncidentsR, maxError, error_flag, learningRate, momentum, 1.0);
			expertiseR = (HashMap<String, HashMap<Integer, Double>>) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// II. Train based on pair of expert (R_expertise) and transferred incident --> Predict Non-resolve!
			toBeReturned = mainExpectationTrainProcess(NN, expertiseR, incidents, expertToIncidentsT, maxError, error_flag, learningRate, momentum, 0.0);
			expertiseR = (HashMap<String, HashMap<Integer, Double>>) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// III. Train based on pair of expert (T_expertise) and resolved incident --> Predict Non-Transfer!
			toBeReturned = mainExpectationTrainProcess(NN, expertiseT, incidents, expertToIncidentsR, maxError, error_flag, learningRate, momentum, 0.0);
			expertiseT = (HashMap<String, HashMap<Integer, Double>>) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			// IV. Train based on pair of expert (T_expertise) and transferred incident --> Predict Transfer!
			toBeReturned = mainExpectationTrainProcess(NN, expertiseT, incidents, expertToIncidentsT, maxError, error_flag, learningRate, momentum, 1.0);
			expertiseT = (HashMap<String, HashMap<Integer, Double>>) toBeReturned.get(0);
			errorSum += (double) toBeReturned.get(1);
			numOfPatterns += (double) toBeReturned.get(2);
			num_of_false_prediction += (int) toBeReturned.get(3);
			error_flag = (boolean) toBeReturned.get(4);
			
			if(!error_flag){
				//this is end of learning process
				System.out.println("Coverage is obtained in epoch: " + ep);
				break;
			}
			
			double devMSE = reportErrorOnDevSet(NN, incidents, expertiseR, expertiseT, expertToIncidentsR, expertToIncidentsT);
			
			if(devMSE >= previousDevMSE)
				sameOrLargerErrorCount++;
			else
				sameOrLargerErrorCount = 0;
			previousDevMSE = devMSE;
			
			System.out.println("Expectation ==> Epoch: " + ep + " with MSE: " + Math.round((errorSum/numOfPatterns)*1000000)/1000000.000000 + 
					" Errors: " + num_of_false_prediction + " #Patterns: " + numOfPatterns + " DevMSE: " + devMSE);

			if(sameOrLargerErrorCount == 4){
				tbr.add(expertiseR);
				tbr.add(expertiseT);
				return tbr;
			}
		}
		
		tbr.add(expertiseR);
		tbr.add(expertiseT);
		return tbr;
	}
	
	public static ArrayList<Object>  mainExpectationTrainProcess(NeuralNet NN, HashMap<String, HashMap<Integer, Double>> expertise,
			HashMap<String, HashMap<Integer, Double>> incidents, HashMap<String, HashMap<String, Boolean>> expertToIncidents,
			double maxError, boolean error_flag, double learningRate, double momentum, double trueValue){
		
		ArrayList<Object> tbr = new ArrayList<>();
		double errorSum = 0;
		double numOfPatterns = 0;
		int num_of_false_prediction = 0;
		
		Iterator it = expertToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> e = (Entry<String, HashMap<String, Boolean>>) it.next();
			HashMap<Integer, Double> exp = expertise.get(e.getKey());
			if(exp == null)
				continue;
			
			Iterator it2 = e.getValue().entrySet().iterator(); 
			while(it2.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it2.next();
				if(!t.getValue()) //this means t is a Dev pattern, not a training one
					continue;
				HashMap<Integer, Double> inc = incidents.get(t.getKey());
				if(inc == null) //this happens if the content of incident be useless
					continue;
				
				//calculate the net output					
				double o = logisticRegression(NN, exp, inc);
				double error = trueValue - o; //this is like [d - y], which means the actual value of error, signed version
				errorSum += Math.pow(error, 2); numOfPatterns++;
				if(Math.abs(error) > maxError){
					num_of_false_prediction++;
					error_flag = true;
					exp = updateExpertiseVectorGradientDescent(NN, exp, learningRate, error);
				}
			}
			//replace the updated version
			expertise.put(e.getKey(), exp);
		}
		
		tbr.add(expertise);
		tbr.add(errorSum);
		tbr.add(numOfPatterns);
		tbr.add(num_of_false_prediction);
		tbr.add(error_flag);
		
		return tbr;
	}
}
