package ExpertiseVectorExtraction;

public class Node {
	
	public static class Hidden{
		double v;
		double y;
		double weight;
		double oldWeight;
		double lastWeightUpdate;		
	}
	
	public static class Input{
		double value;
		double[] weights;
		double[] oldWeights;
		double[] lastWeightChanges;
	}
}
