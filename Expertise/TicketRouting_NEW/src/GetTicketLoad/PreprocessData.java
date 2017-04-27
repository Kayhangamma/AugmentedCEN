package GetTicketLoad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PreprocessData {

	public static void main(String[] args) throws IOException, ParseException {
		//This process will return a list of experts with related incidents. Incidents are sorted based on their start time
//		HashMap<String, ArrayList<Ticket>> expertsToIncidents = createIndexOnTicketTransfers();
//		System.out.println("Data is loaded!");
//		System.out.println(countNumberOfProcessed_SimpleCase("NI-AGENCY-DESKTOP", 34463, 34860, 2, expertsToIncidents));
//		for(int i=0;i<10000;i++)
//			countNumberOfProcessed_SimpleCase("NI-AGENCY-DESKTOP", 34463, 34860, 2, expertsToIncidents);
		getPriorityForTickets();
	}
	
	public static void getPriorityForTickets() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/Entire Data/";
		BufferedReader br = new BufferedReader(new FileReader(path + "Full 2015-03-01 to 2016-02-29 EZ Path Report.csv"));
		String line = br.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "Incident Summarized Detail.csv"));
		bw.write("Incident ID,Priority,Owning Group,Assignment Group,Closed Group\n");
		HashMap<String, Integer> priorities = new HashMap<String, Integer>();
		StringBuilder incident = new StringBuilder();
		while((line= br.readLine()) != null){
			line = line.replace(",\",", "\",");
			if(line.startsWith("IM0") && incident.toString().length() > 0){
				String[] content = incident.toString().split(",\"");
				String filteredContent = "";
				for(int i=0;i<content.length;i++){
					if(content[i].contains("\"")){
						String[] ps = content[i].split("\"");
						if(ps.length > 1)
							filteredContent += ",NULL" + ps[1];
						else
							filteredContent += ",NULL";
					}
					else
						filteredContent += content[i];
				}
//				System.out.println(filteredContent);
				String[] parts = filteredContent.split(",");
				if(parts.length == 30){					
					bw.write(parts[0] + "," + parts[7] + "," + parts[9] + "," + parts[10] + "," + parts[11] + "\n");
					if(priorities.containsKey(parts[7]))
						priorities.put(parts[7], priorities.get(parts[7]) + 1);
					else 
						priorities.put(parts[7], 1);
				}
				incident = new StringBuilder();				
			}
			incident.append(line);
		}
		bw.close();
		
		System.out.println(priorities);
	}

	public static void convertTimeToLogicalUnits() throws IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ssa");
		HashMap<Date, Boolean> uniqueTimes = new HashMap<Date, Boolean>();
		String path = "C:/Users/moovas1/Desktop/Kayhan Project/TimeToResolveData-Secure/";
		BufferedReader br = new BufferedReader(new FileReader(path + "Transfer Time Intervals 2015-03-01 to 2016-02-29.csv"));
		String line = br.readLine();
		while((line = br.readLine()) != null){
			String[] parts = line.split(",");
			Date t1 = sdf.parse(parts[2]);
			Date t2 = sdf.parse(parts[3]);
			uniqueTimes.put(t1, true);
			uniqueTimes.put(t2, true);
		}		
		System.out.println("#Unique Time Stamps: " + uniqueTimes.size() + "\n");
		
		ArrayList<Date> dates = new ArrayList<Date>();
		Iterator it = uniqueTimes.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Date, Boolean> d = (Entry<Date, Boolean>) it.next();			
			dates.add(d.getKey());
		}
		
		//sorting the collection of dates
		Collections.sort(dates);
		
		//Assigning unique ID to dates
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "dateToLogicalDate.txt"));
		for(int i=0;i<dates.size();i++)
			bw.write(dates.get(i) + "\t" + (i+1) + "\n");
		bw.close();
	}

	public static void convertTransferTicketsFile() throws IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ssa");
		String path = "C:/Users/moovas1/Desktop/Kayhan Project/TimeToResolveData-Secure/";	 
		
		//load date to logical clock
		BufferedReader br = new BufferedReader(new FileReader(path + "dateToLogicalDate.txt"));
		HashMap<Date, Integer> dateToInt = new HashMap<Date, Integer>();
		String line = "";
		while((line = br.readLine()) != null){
			String[] parts = line.split("\t");
			dateToInt.put(new Date(parts[0]), Integer.parseInt(parts[1]));
		}
		
		//load priority values per tickets
		br = new BufferedReader(new FileReader(path + "Incident Summarized Detail.csv"));
		HashMap<String, Integer> priorities = new HashMap<String, Integer>();
		line = br.readLine();
		while((line=br.readLine()) != null){
			String[] parts = line.split(",");
			priorities.put(parts[0], Integer.parseInt(parts[1]));
		}
		
		//load ''transfer'' file for conversion
		br = new BufferedReader(new FileReader(path + "Transfer Time Intervals 2015-03-01 to 2016-02-29.csv"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "Transfer Time Intervals Converted.txt"));
		HashMap<String, Boolean> ticketsWithNoPriority = new HashMap<String, Boolean>();
		bw.write(br.readLine().replace(" ", "_").replace(",", "\t") + "\tPriority\n");
		while((line=br.readLine()) != null){
			String[] parts = line.split(",");
			int t1 = dateToInt.get(sdf.parse(parts[2]));
			int t2 = dateToInt.get(sdf.parse(parts[3]));
			bw.write(parts[0] + "\t" + parts[1] + "\t" + t1 + "\t" + t2 + "\t" + parts[4] + 
					"\t" + parts[5] + "\t" + parts[6] + "\t" + priorities.get(parts[0]) + "\n");
			if(!priorities.containsKey(parts[0]))
				ticketsWithNoPriority.put(parts[0], true);
		}
		bw.close();
	}

	public static HashMap<String, ArrayList<Ticket>> createIndexOnTicketTransfers() throws IOException{
		String path = "C:/Users/moovas1/Desktop/Kayhan Project/TimeToResolveData-Secure/";
		HashMap<String, ArrayList<Ticket>> expertsToIncidents = new HashMap<String,  ArrayList<Ticket>>();		
		BufferedReader br = new BufferedReader(new FileReader(path + "Transfer Time Intervals Converted.txt"));
		
		//load incidents per expert group
		String line = br.readLine();
		while((line = br.readLine()) != null){
			String[] parts = line.split("\t");
			if(parts[7].equals("null"))
				continue;
			ArrayList<Ticket> incidents = new ArrayList<Ticket>();
			if(expertsToIncidents.containsKey(parts[1]))
				incidents = expertsToIncidents.get(parts[1]);
			Ticket t = new Ticket(line);
			incidents.add(t);
			expertsToIncidents.put(parts[1], incidents);
		}
		
		//sort incidents for each expert group based on start time
		HashMap<String, ArrayList<Ticket>> expertsToIncidents_sorted = new HashMap<String,  ArrayList<Ticket>>();
		Iterator it = expertsToIncidents.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, ArrayList<Ticket>> g = (Entry<String, ArrayList<Ticket>>) it.next();
			ArrayList<Ticket> toBeSorted = g.getValue();
			Collections.sort(toBeSorted, new Comparator() {
				@Override
				public int compare(Object arg0, Object arg1) {
					Ticket f = (Ticket) arg0;
					Ticket s = (Ticket) arg1;
					if(f.t1 > s.t1)
						return 1;
					if(f.t1 < s.t1)
						return -1;
					else
						return 0;
				}
			});
			expertsToIncidents_sorted.put(g.getKey(), toBeSorted);
		}
		
		return expertsToIncidents_sorted;
	}
	
	public static double countNumberOfProcessed_SimpleCase(String g, int t1, int t2, int p, 
			HashMap<String, ArrayList<Ticket>> expertsToIncidents){
		ArrayList<Ticket> incidents = expertsToIncidents.get(g);
		double load = 0;
		for(int i=0;i<incidents.size();i++){
			Ticket t = incidents.get(i);
			if(t.pr != p)
				continue;
			if(t.t1 <= t1 && t.t2 > t1 && t.t2 <= t2)
				load += (t.t2-t1)/(double)(t2-t1);
			else if (t.t1 >= t1 && t.t2 <= t2)
				load += (t.t2-t.t1)/(double)(t2-t1);
			else if(t.t1 >= t1 && t.t1 < t2 && t.t2 >= t2)
				load += (t2-t.t1)/(double)(t2-t1);
			else if(t.t1 < t1 && t.t2 > t2)
				load += 1.0;
		}
		return load;
	}

	public static void buildANewTransferTimeIntervalFile() throws IOException{
		String path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/";
		
		List<String> existing_transfers = Files.readAllLines(Paths.get(path + "Archive/Transfer Time Intervals 2015-03-01 to 2016-02-29.csv"));
		List<String> all_train_incidents = Files.readAllLines(Paths.get(path + "TrainTestRandomSplit/Trains_AllIncidents.txt"));
		HashMap<String, Boolean> train = new HashMap<>();
		for(int i=0;i<all_train_incidents.size();i++)
			train.put(all_train_incidents.get(i), true);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "Transfer Time Intervals 2015-03-01 to 2016-02-29.csv"));
		bw.write(existing_transfers.get(0) + "\n");
		for(int i=1;i<existing_transfers.size();i++){
			String[] parts = existing_transfers.get(i).split(",");
			if(train.containsKey(parts[0]))
				bw.write(existing_transfers.get(i) + "\n");
		}
		bw.close();
		
	}
}
