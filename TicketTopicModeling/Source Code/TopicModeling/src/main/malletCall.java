package main;
//Code Author: Kayhan Moharreri

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.jfree.ui.RefineryUtilities;

import charts.ChartForTopics;

import fileHandler.CopyFile;
import fileHandler.ExcelEditor;
import fileHandler.xmlHandler;

public class malletCall {
	
	
	public static void train(String inputPath, File F) throws IOException
	{
		 
		 	 
		 // CD to Mallet/bin, Write into the file using this pattern: mallet train-topics --input outputfrommallet.mallet --num-topics 1 0 --output-state topic-state.gz --output-topic-keys outputfrommalletkeys.txt --output-doc-topics outputfrommallet_composition.csv
		 String directoryChange = "cd %MALLET_HOME%\\bin"+ "\n";
		 
		 //String malletTrainer = "mallet import-dir --input C:\\Users\\mohark1\\Desktop\\inputs --output outputfrommallet.mallet --keep-sequence --remove-stopwords --extra-stopwords blacklisted.txt";
		 
		 String malletTrainer = "mallet import-dir --input "+inputPath+ " --output outputfrommallet.mallet --keep-sequence --remove-stopwords";
		 if (OptinalArguments.hasBlackList) 
		 {
			 malletTrainer = malletTrainer + " --extra-stopwords " + F.getParent()+ "\\"+ OptinalArguments.blacklistFile;
		 }
		 
		 String command = "mallet train-topics --input outputfrommallet.mallet --num-topics ";
		 String followed = " --optimize-interval ";
		 String afterOptimize=" --output-state topic-state.gz --output-topic-keys outputfrommalletkeys" ;
		 String txtfileNumbered =".txt --output-doc-topics outputfrommallet_composition" ;
		 String csvfileNumbered =".txt";
		 String xmlSwitch = " --xml-topic-phrase-report topicCounts";
		 String xmlEnd = ".xml";
		 String exitString = " && " + "exit \n";
		 String line;
		 
		 spliterWriter("batch.bat", directoryChange + malletTrainer+ exitString);
		 Process p = Runtime.getRuntime().exec("cmd /c start batch.bat");	
		 BufferedReader in = new BufferedReader(
                new InputStreamReader(p.getInputStream()) );
        while ((line = in.readLine()) != null) {
          System.out.println(line);
        }
        in.close();
        
		 for (int i=OptinalArguments.minimum;i<OptinalArguments.maximum+1; i++ )
		 {
			
			 spliterWriter("batch.bat", directoryChange + command+ Integer.toString(i)+followed+Integer.toString(i)+afterOptimize+ Integer.toString(i)+ txtfileNumbered+Integer.toString(i)+csvfileNumbered+xmlSwitch+Integer.toString(i)+xmlEnd+ exitString);
			 
			 p = Runtime.getRuntime().exec("cmd /c start batch.bat");		 
			 
			in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()) );
            while ((line = in.readLine()) != null) {
              System.out.println(line);
            }
            in.close();
		 }
		 
		 
		
		 String inputFile = "C:/Mallet/bin/outputfrommalletkeys";
		 String inputCompositionFile = "C:/Mallet/bin/outputfrommallet_composition";
		 String inputXml = "C:/Mallet/bin/topicCounts"; 
		 
		 boolean success = (new File(F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Topics")).mkdirs();
		 boolean success2 = (new File(F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\TopicsCompositions")).mkdirs();
		 boolean success3 = (new File(F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Results")).mkdirs();
		 boolean success4 = (new File(F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Charts")).mkdirs();
		 boolean success5 = (new File(F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\TopicDetails")).mkdirs();
		 
		 if (!success || !success2 || !success3 || !success4 || !success5) {
		     //System.err.println("Error: Folder was not created!");
		     //System.exit(0);
		 }
		 
		 
		 String outputFile = F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Topics\\outputfrommalletkeys"; //"C:/Mallet/bin/CSVs/outputfrommalletkeys";
		 String outputCompositionFile = F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\TopicsCompositions\\outputfrommallet_composition"; 
		 String outputXml = F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\TopicDetails\\topicCounts";
		 
		 
		 
		 
		 for (int i=OptinalArguments.minimum;i<OptinalArguments.maximum+1; i++ ){
			 //Read the file line by line:
			 File file = new File(inputFile + Integer.toString(i)+".txt" );
			BufferedReader br = new BufferedReader(new FileReader(file));
			 String AllLines = "";
			 String thisLine = "";
			 while ((thisLine = br.readLine()) != null) {
				 //System.out.println(thisLine);
				 thisLine = thisLine.replaceAll("\t", ",");
				 thisLine = thisLine.replaceAll(" ",",");
				 thisLine += "\n"; 
				 AllLines +=thisLine;
				 
			 }
			 br.close();
			 
			spliterWriter(outputFile+Integer.toString(i) +".csv", AllLines);
			
			//For Composition file
			 File file2 = new File(inputCompositionFile + Integer.toString(i)+".txt" );
			BufferedReader br2 = new BufferedReader(new FileReader(file2));
			 String AllLines2 = "";
			 String thisLine2 = "";
			 while ((thisLine2 = br2.readLine()) != null) {
				 
				 thisLine2 = thisLine2.replaceAll("\t", ",");
				 thisLine2 = thisLine2.replaceAll(" ",",");
				 thisLine2 += "\n"; 
				 // NOT SCALABLE 
				 AllLines2 +=thisLine2;
				 
			 }
			 br2.close();
			 
			spliterWriter(outputCompositionFile+Integer.toString(i) +".csv", AllLines2);	
			TopicCompositions Comp = new TopicCompositions(i);
			Comp.loadToMap(outputCompositionFile+Integer.toString(i) +".csv");
			
	        
			
			
			//Replicate an excel file as Data.xls
			CopyFile.copy(F.getParent()+"\\Data.xls", F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Results", "DataProcessed"+ Integer.toString(i)+ ".xls");
			ExcelEditor.AddInfo (F.getParent()+"\\Autogenerated--"+OptinalArguments.currentTime+ "\\Results\\DataProcessed"+Integer.toString(i)+".xls", Comp);
			
			//Plotting the chart
			ChartForTopics BarChart = new ChartForTopics("Distribution of "+Integer.toString(i) +" Topics",Comp.topicFreqCount(), i, F);
	        BarChart.pack();
	        RefineryUtilities.centerFrameOnScreen(BarChart);
	        BarChart.setVisible(false);
	        

	      //Creating the XML conversions
	       xmlHandler XMLHand = new xmlHandler();
	       XMLHand.topWords(inputXml+Integer.toString(i)+".xml", outputXml+Integer.toString(i)+".xlsx");
	        
	        System.out.println("Done for "+ i);   
		 } 
		 
		 deleteFile ("batch.bat", F);
		 System.exit(0);
	 }
	
	
	
	
	
	













	private static void deleteFile(String myFile, File F) {
		 try{
			 
	    		File file = new File(F.getParent()+ "\\"+myFile);
	 
	    		if(file.delete()){
	    			//System.out.println(file.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	 
	    	}catch(Exception e){
	 
	    		e.printStackTrace();
	 
	    	}
	
	 }
		 
		 public static void spliterWriter (String Filename, String line){
				
				try {
					 
					//String content = "This is the content to write into file";
		 
					File file = new File(Filename);
		 
					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}
		 
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(line);
					bw.close();
		 
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }

		
		
		
		
	}


