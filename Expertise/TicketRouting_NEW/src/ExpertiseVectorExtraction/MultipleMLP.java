package ExpertiseVectorExtraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MultipleMLP {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		findSkewFeatureOfDataPerExperts();
	}
	
	public static void findSkewFeatureOfDataPerExperts() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Improved Data/";
		HashSet<String> resolvers = new HashSet<>();
		resolvers.addAll(Files.readAllLines(Paths.get(path + "frequentResolvers.txt")));
		HashSet<String> transferers = new HashSet<>();
		transferers.addAll(Files.readAllLines(Paths.get(path + "frequentTransferers.txt")));
		
		HashSet<String> selectedExperts = new HashSet<>();
		for(String exp:resolvers)
			if(transferers.contains(exp))
				selectedExperts.add(exp);
		
		//load count for Expert --> transfer and Expert -> resolve
		HashMap<String, Integer> expertResolve = new HashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(path + "ExpertToResolved.txt"));
		for(String ln:lines){
			String[] parts = ln.split("\t");
			expertResolve.put(parts[0], parts[1].split(",").length);
		}
		
		HashMap<String, Integer> expertTransfer = new HashMap<>();
		lines = Files.readAllLines(Paths.get(path + "ExpertToTransferred.txt"));
		for(String ln:lines){
			String[] parts = ln.split("\t");
			expertTransfer.put(parts[0], parts[1].split(",").length);
		}
		
		for(String exp:selectedExperts)
			System.out.println(exp + "\t" + expertResolve.get(exp) + "\t" + expertTransfer.get(exp));
		
	}

}
