package fileHandler;
//Code Author: Vijayalakshmi Dhamodharan

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class xmlHandler {
	
	 XSSFWorkbook workbook = new XSSFWorkbook();
     XSSFSheet sheet = workbook.createSheet("Sample sheet");
    
	 int i3 = 0, i4 = 0, rownum = 0, rownum1 = 1, rownum2 = 1,colnum1 = 0, rownumPhrase = 0,rownumPhrase1 = 0, maxWordlistsize = 0, maxPhraselistsize =0, rownum3 = 0,maxWordlistsize1 = 0;
	 Row firstRow = sheet.createRow(rownum);
	 Row row1,row2;

    	public void topWords(String originalFilepath, String targetFilepath){
    	
        ArrayList<String> word = new ArrayList<String>();
        ArrayList<String> wordCount = new ArrayList<String>();
        ArrayList<String> wordWeight = new ArrayList<String>();
        
        ArrayList<String> phraseCount = new ArrayList<String>();
        ArrayList<String> phraseWeight = new ArrayList<String>();
        
        ArrayList<String> phrase = new ArrayList<String>();
        
        ArrayList<Integer> wordlistSize = new ArrayList<Integer>();
        ArrayList<Integer> phraselistSize = new ArrayList<Integer>();

        
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(originalFilepath));
            
            doc.getDocumentElement().normalize();
           
            NodeList listOfTopics = doc.getElementsByTagName("topic");
            
            
            for (int s = 0; s < listOfTopics.getLength(); s++){
            	
            	Node topicNode = listOfTopics.item(s);            	

        		if (topicNode.hasAttributes()) {
                   Attr attr = (Attr) topicNode.getAttributes().getNamedItem("id");
                   if (attr != null) {
                       String attribute= attr.getValue();                      
                       
                       writetopicExcel(attribute,s);             }
            	
            	Element topicElement = (Element) topicNode;
            	NodeList wordList = topicElement.getElementsByTagName("word");
            	int wordlist = wordList.getLength();
            	
            	wordlistSize.add(wordlist);
            	Collections.sort(wordlistSize);
            	int maxWordlist = wordlistSize.get(wordlistSize.size() - 1);
            	 maxWordlistsize = maxWordlist+1;
            	 maxWordlistsize1 = maxWordlistsize;
            	
            	 
            	 NodeList phraseList = topicElement.getElementsByTagName("phrase");
             	
             	int phraselist = phraseList.getLength();
             	phraselistSize.add(phraselist);
             	Collections.sort(phraselistSize);
             	int maxPhraselist = phraselistSize.get(phraselistSize.size() - 1);
             	 maxPhraselistsize = maxPhraselist + 3;
              	//System.out.println("largest phrase" + maxPhraselistsize);
            	 
            }
            
            }
            
            for (int s1 = 0; s1 < listOfTopics.getLength(); s1++){
            	
            	Node topicNode = listOfTopics.item(s1);            	

            		if (topicNode.hasAttributes()) {
                       Attr attr = (Attr) topicNode.getAttributes().getNamedItem("id");
                       if (attr != null) {
                           String attribute= attr.getValue();                      
                           
                           writetopicExcel(attribute,s1);

                       }
                	
                	Element topicElement = (Element) topicNode;
                	
                	
                	
                	NodeList wordList = topicElement.getElementsByTagName("word");
                	//int wordlist = wordList.getLength();
	                for(int x=0,size= wordList.getLength(); x<size; x++) {
	                    	
	                    	Node node = wordList.item(x);
	                    	if (node.getNodeType() == Node.ELEMENT_NODE) {
	                    		
	                    		NodeList wordChild = node.getChildNodes();
	                    		//String wordName = wordChild.item(0).getNodeValue();
	                    		String wordAttr = node.getAttributes().getNamedItem("count").getNodeValue();
	                    		String wordLocalWeight = node.getAttributes().getNamedItem("weight").getNodeValue();
	                    		
	                    		word.add(((Node) wordChild.item(0)).getNodeValue().trim());
	                    		wordCount.add(wordAttr);
	                    		wordWeight.add(wordLocalWeight);
	                    		
	                
	                    	 
	                       }
	                    	
	                  }  
	                 
	               
	                 writewordExcel(word,wordCount, wordWeight,s1);
                	
                	NodeList phraseList = topicElement.getElementsByTagName("phrase");
                	
                	//int phraselist = phraseList.getLength();
	                for(int x=0,size= phraseList.getLength(); x<size; x++) {
	                    	Node node = phraseList.item(x);
	                    	if (node.getNodeType() == Node.ELEMENT_NODE) {
	                    		NodeList phraseChild = node.getChildNodes();
	                    		//String phraseName = phraseChild.item(0).getNodeValue();
	                    		String phraseAttr = node.getAttributes().getNamedItem("count").getNodeValue();
	                    		String phraseWeightAttr = node.getAttributes().getNamedItem("weight").getNodeValue();
	                    	
	                    		phrase.add(((Node) phraseChild.item(0)).getNodeValue().trim());
	                    		
	                    		phraseWeight.add(phraseWeightAttr);
	                    		phraseCount.add(phraseAttr);
	                    		
	                    	
	                       }
	                  }  
	               
	                          writephraseExcel(phrase,phraseCount,phraseWeight,s1);
            		}
            		}
            
             closeExcel(targetFilepath);
            
        }

        catch (SAXParseException err) 
        {
            System.out.println("** Parsing error" + ", line "+ err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } 
        catch (SAXException e) 
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } 
        catch (Throwable t) 
        {
            t.printStackTrace();
        }

    	
 }

	public void writetopicExcel(String attribute,int topicnumber){
		
		int column2 = (colnum1+(3*topicnumber));
		
		Cell cell = firstRow.createCell(column2);
		Cell cell1 = firstRow.createCell(column2+1);
		Cell cell2 = firstRow.createCell(column2+2);
		cell1.setCellValue("Count " + attribute);
		cell2.setCellValue("Weight " + attribute );
		cell.setCellValue("TOPIC " + attribute);
	}

	public void writewordExcel(ArrayList<String> word,ArrayList<String> wordCount, ArrayList<String> wordWeight, int topicnumber) {
			
		if (sheet.getRow(rownum1) == null)
		{ 
			for (int i1 = 0; i1<maxWordlistsize; i1++)
			
			   {
				 row1 = sheet.createRow(rownum1++); }
				 
			rownum1 = 1;
			   
			     for(int i=i3;i<word.size();i++)
			        {
						Row row2 = sheet.getRow(rownum1); 
						String words = word.get(i);
			        	String wordCounts = wordCount.get(i);
			        	String WordWeights = wordWeight.get(i);
			        	
			        	Cell cell = row2.createCell(colnum1);
						cell.setCellValue(words);
			        	
			        	Cell cell1 = row2.createCell(colnum1+1);
			        	cell1.setCellValue(wordCounts);
			        	
			        	Cell cell2 = row2.createCell(colnum1+2);
			        	cell2.setCellValue(WordWeights);
			        	
				        
				        i3 = word.size();
				       rownum1++;
			        	
			        }
			  
			   rownum1 = 1;
			   
			   
			}

		else {
			
			for(int i=i3;i<word.size();i++) {
				
				Row row2 = sheet.getRow(rownum1); 
				String words = word.get(i);
				String wordCounts = wordCount.get(i);
				String WordWeights = wordWeight.get(i);
				

				int column2 = (colnum1+(3*topicnumber));
				
				 Cell cell = row2.createCell(column2);
				 
				 cell = sheet.getRow(rownum1).getCell(column2);

				 cell.setCellValue(words);
				
				Cell cell1 = row2.createCell(column2+1);
				cell1 = sheet.getRow(rownum1).getCell(column2+1);
	        	cell1.setCellValue(wordCounts);
	        	
	        	Cell cell2 = row2.createCell(column2+2);
				 cell2 = sheet.getRow(rownum1).getCell(column2+2);
	        	cell2.setCellValue(WordWeights);
	        	i3 = word.size();
	        	rownum1++;
			
			}
			rownumPhrase = i3+1;
			rownum1 = 1;
	}
		
	}
		
		
	private void writephraseExcel(ArrayList<String> phrase,ArrayList<String> phraseCount, ArrayList<String> phraseWeight,int topicnumber) {
     	
		maxWordlistsize = maxWordlistsize+1;
        if (sheet.getRow(maxWordlistsize) == null)
		{ 
			 
        	for (int i1 = i4; i1<maxPhraselistsize; i1++)
    			
			   {
				 row2 = sheet.createRow(maxWordlistsize); 
				 maxWordlistsize++;
			   }
        	
        	maxWordlistsize = maxWordlistsize1+1;
        	
        	

        	for(int i=i4;i<phrase.size();i++)
			        {
			    	 		     
			    	    Row row4 = sheet.getRow(maxWordlistsize);
			    	 	String phrases = phrase.get(i);
			        	String phraseCounts = phraseCount.get(i);
			        	String phraseWeights = phraseWeight.get(i);
			        	
			        	Cell cell = row4.createCell(colnum1);
			        	cell.setCellValue(phrases);
			        	
			        	Cell cell1 = row4.createCell(colnum1+1);
			        	cell1.setCellValue(phraseCounts);
			        	
			        	Cell cell2 = row4.createCell(colnum1+2);
			        	cell2.setCellValue(phraseWeights);
			        	
				        
				        i4 = phrase.size();
				        maxWordlistsize++;
			        	
			        }
			    
			   
			}

		else {
			
			
			for(int i=i4;i<phrase.size();i++) {
				
				 Row row3 = sheet.getRow(maxWordlistsize);
				 String phrases = phrase.get(i);
		         String phraseCounts = phraseCount.get(i);
		         String phraseWeights = phraseWeight.get(i);
				
				int column2 = (colnum1+(3*topicnumber));			
				 
				
				 Cell cell = row3.createCell(column2);
				 cell = sheet.getRow(maxWordlistsize).getCell(column2);
				cell.setCellValue(phrases);
				
				
				Cell cell1 = row3.createCell(column2+1);
				 cell1 = sheet.getRow(maxWordlistsize).getCell(column2+1);
	        	cell1.setCellValue(phraseCounts);
	        	
	        	Cell cell2 = row3.createCell(column2+2);
				 cell2 = sheet.getRow(maxWordlistsize).getCell(column2+2);
	        	cell2.setCellValue(phraseWeights);
	        	i4 = phrase.size();
	        	maxWordlistsize++;
				}
				
			}
         maxWordlistsize = maxWordlistsize1;
	
	}
	
		
    public void closeExcel(String targetFilepath){
   
	    try { 
	    	 
	    	FileOutputStream out = new FileOutputStream(new File(targetFilepath));
	        workbook.write(out);
	        out.close();
	    
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }      
		


		
}
