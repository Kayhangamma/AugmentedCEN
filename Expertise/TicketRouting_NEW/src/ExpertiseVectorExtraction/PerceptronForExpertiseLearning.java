package ExpertiseVectorExtraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class PerceptronForExpertiseLearning {

	public static void main(String[] args) throws IOException {
//		HashMap<String, HashMap<Integer, Double>> incidents = loadIncidentVectors();
		HashMap<String, HashMap<String, Boolean>> expertToIncident = loadTicketToExpert();
		
		Iterator it = expertToIncident.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, HashMap<String, Boolean>> exp_inc = (Entry<String, HashMap<String, Boolean>>) it.next();
			
		}
	}
	
	public static HashMap<String, HashMap<Integer, Double>> loadIncidentVectors() throws IOException{
		String path = "C:/Users/moovas1/Desktop/Kayhan Project/TimeToResolveData-Secure/";
		HashMap<String, HashMap<Integer, Double>> incidents = new HashMap<String, HashMap<Integer,Double>>();
		BufferedReader br = new BufferedReader(new FileReader(path + "incident_vector.txt"));
		String line = br.readLine();//First line is header
		while((line=br.readLine()) != null){
			String id = line.split("\t")[0];
			String[] vec = line.split("\t")[1].split(",");
			HashMap<Integer, Double> vector = new HashMap<Integer, Double>();
			for(int i=0;i<vec.length;i++)
				if(!vec[i].equals("0"))
					vector.put(i+1, Double.parseDouble(vec[i]));
			incidents.put(id, vector);
		}
		return incidents;
	}

	public static HashMap<String, HashMap<String, Boolean>> loadTicketToExpert() throws IOException{
		String path = "C:/Users/moovas1/Desktop/Kayhan Project/TimeToResolveData-Secure/";
		BufferedReader br = new BufferedReader(new FileReader(path + "Transfer Time Intervals Converted.txt"));
		HashMap<String, HashMap<String, Boolean>> expertToIncident = new HashMap<String, HashMap<String,Boolean>>();
		String line = br.readLine();//First line is header
		while((line=br.readLine()) != null){
			String[] parts = line.split("\t");
			HashMap<String, Boolean> incidents = new HashMap<String, Boolean>();
			if(expertToIncident.containsKey(parts[1]))
				incidents = expertToIncident.get(parts[1]);
			if(parts[6].equals("Resolution"))
				incidents.put(parts[0], true);
			else
				incidents.put(parts[0], false);
			
			expertToIncident.put(parts[1], incidents);
		}
		return expertToIncident; 
	}

	public static void learningExpertise(String exp, HashMap<String, Boolean> inc, 
			HashMap<String, HashMap<Integer, Double>> incidents, double minError, int M){
		//M: Total number of features in incident vector
		
		//Create initial expertise vector for expert exp
		double[] E = new double[M];
		Random rn = new Random();
		for(int i=0;i<M;i++)
			E[i] = Math.round(rn.nextDouble()*100.00)/100.00;
		
		//Set the initial error value
		double error = Double.MAX_VALUE;
		double b = 1.0;
		double nu = 0.1;
		int epoch = 1;
		
		while(error > minError){
			//An epoch
			System.out.println("Current Epoch: " + epoch);
			Iterator it = inc.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Boolean> t = (Entry<String, Boolean>) it.next();
				HashMap<Integer, Double> v = incidents.get(t.getKey());				
				Iterator it2 = v.entrySet().iterator();
				double w_e = 0;
				while(it2.hasNext()){
					Map.Entry<Integer, Double> f = (Entry<Integer, Double>) it2.next();
					w_e += E[f.getKey()-1]*f.getValue();
				}
				double out = activationFunction(w_e, b);
				double trueValue = 0;
				if(t.getValue())
					trueValue = 1.0;
				error = trueValue - out;
				for(int i=0;i<M;i++){
					double delta = nu*error*E[i];
					E[i] = E[i] + delta;
				}
			}
			epoch++;
		}
	}
	
	public static double activationFunction(double w_e, double b){
		double val = 1.0/(1 + Math.exp(-(w_e + b)));
		return val;
	}
}
