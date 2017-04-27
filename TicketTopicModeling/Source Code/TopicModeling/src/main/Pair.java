package main;
//Code Author: Kayhan Moharreri

public class Pair {

	private int first;
	private double second;
	
	public Pair(){
		first = 0;
		second = 0.00;
		
	}
	public Pair(int f, double s){
		first = f;
		second = s;
	}
	
	public void setFirst(int f)
	{
		this.first = f; 
		
	}
	public void setSecond (double s)
	{
		this.second = s; 
		
	}
	public int getfirst(){
		return first;
	}
	public double getsecond(){
		return second;
	}
	
}
