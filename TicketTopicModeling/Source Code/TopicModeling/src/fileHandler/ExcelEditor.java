package fileHandler;
//Code Author: Naman Mody

import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


import main.ResolutionGenerator;
import main.TopicCompositions;



public class ExcelEditor {

	
	 public static void AddInfo(String xlsFilePath, TopicCompositions comp) {
			
		 
		 try{
				
				//System.out.println(xlsFilePath);
				FileInputStream myfile = new FileInputStream(new File(xlsFilePath));			
				
				HSSFWorkbook wb = new HSSFWorkbook(myfile);		
								
				wb.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);
				HSSFSheet sheet = wb.getSheetAt(0);
				
				
			/*** Set the Topic heading ***/
				
				HSSFRow first_row = sheet.getRow(0);
				int index = first_row.getPhysicalNumberOfCells();
				
				HSSFCell topic_cell = first_row.createCell(index);
				topic_cell.setCellType(Cell.CELL_TYPE_STRING);
				topic_cell.setCellValue("Topic");
				
				HSSFCell prob_cell = first_row.createCell(index+1);
				prob_cell.setCellType(Cell.CELL_TYPE_STRING);
				prob_cell.setCellValue("Probability");
				
			/********/	
						
				int total_rows = sheet.getPhysicalNumberOfRows();		
				//System.out.println("total rows " + total_rows + " and index is " + index);
				
				int offset =0;
				HSSFRow curr_row = first_row;
				@SuppressWarnings("unused")
				HSSFCell curr_cell, curr_cell2, l_cell;
				
				
				for (int i=1; i<total_rows; i++)
					
				{
					
					if (ResolutionGenerator.badRows.contains(i)) offset++;
					
					else {
						
						curr_row = sheet.getRow(i);
					
					
					//l_cell = curr_row.getCell(index-1);
					//if (l_cell==null) { offset++; continue;}
					//String resol = r_cell.getStringCellValue();
				
					curr_cell = curr_row.createCell(index);
					curr_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					curr_cell.setCellValue(comp.searchItem(i-offset).getfirst());
					
					curr_cell2 = curr_row.createCell(index+1);
					curr_cell2.setCellType(Cell.CELL_TYPE_NUMERIC);
					curr_cell2.setCellValue(comp.searchItem(i-offset).getsecond());
					
					}
				
				}
				
				
				FileOutputStream fileOut = new FileOutputStream(new File(xlsFilePath));
				wb.write(fileOut);
				fileOut.close();


			}
			
			catch (FileNotFoundException e) {
			    e.printStackTrace();
				}
			catch (IOException e) {
			    e.printStackTrace();
				}
			
			catch (NullPointerException e) {}

		}
		 
		 
}
