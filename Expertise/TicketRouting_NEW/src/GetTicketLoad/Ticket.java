package GetTicketLoad;

public class Ticket {
	String id;
	int t1;
	int t2;
	int pr;
	String type;
	
	public Ticket(String input){
		String[] parts = input.split("\t");
		id = parts[0];
		t1 = Integer.parseInt(parts[2]);
		t2 = Integer.parseInt(parts[3]);
		pr = Integer.parseInt(parts[7]);
		type = parts[6];
	}
}
