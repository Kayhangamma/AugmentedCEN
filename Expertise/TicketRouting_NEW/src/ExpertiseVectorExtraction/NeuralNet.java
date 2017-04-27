package ExpertiseVectorExtraction;

import java.util.Random;

public class NeuralNet {
	public double[] I_weight;
	public double[] E_weight;
	
	public double[] I_weight_lastUpdate;
	public double[] E_weight_lastUpdate;
	
	public double b;
	
	public NeuralNet(int N){
		I_weight = new double[N];
		E_weight = new double[N];
		
		I_weight_lastUpdate = new double[N];
		E_weight_lastUpdate = new double[N];
		
//		double r = 4.0 * Math.sqrt(3.0); //Given from http://stats.stackexchange.com/questions/47590/what-are-good-initial-weights-in-a-neural-network
		double r = 1.0;
		for(int i=0;i<N;i++){
			I_weight[i] = (2*r)*(new Random()).nextDouble() - r;
			E_weight[i] = (2*r)*(new Random()).nextDouble() - r;
			
			I_weight_lastUpdate[i] = 0;
			E_weight_lastUpdate[i] = 0;
		}
		
		b = 1; //bias value
	}
}
