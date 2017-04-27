package main;
//Code Author: Kayhan Moharreri

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class OptinalArguments {
	
	public static int maximum=8;
	public static int minimum=2;
	public static String currentTime = DateDemo();
	public static boolean hasBlackList = false;
	public static boolean hasGroups = false;
	public static boolean hasEqlist = false;
	public static String groupsFile = "";
	public static String blacklistFile = "";
	public static String EqFile = "";
	public static String [] Fields;
	

	public static void argParse (String args[]) throws IOException {
		
			String[] defaultField = new String [1];
			defaultField[0] = "Resolution"; //Setting the default 
			Fields = defaultField ;
			for (int i=0;i<args.length;i++)
			 {
				 //Add the try catch
				 switch (args[i]){
				 
				 
				 	case "-Eqlist":
				 		EqFile = args[i+1];
				 		hasEqlist = true;
				 		i++;
				 		break;
				 		
				 	case "-Groups":
				 		groupsFile = args[i+1];
				 		hasGroups = true;
				 		i++;
				 		break;
				 
				 	case "-Fields":
				 		String fields = "";
				 		while (i+1<args.length &&  (args[i+1].charAt(0) != '-'  &&  args[i+1].charAt(0) != '\n') ){
				 			fields  = fields + args[i+1]+ "`";
				 			i++;
				 				
				 		}
				 		Fields = fields.split("`");
				 		break;
				 		
				 
				 	case "-Blacklist":
				 		hasBlackList = true;
				 		blacklistFile = args[i+1];
				 		i++;
				 		// Try catch needed
				 		break;
				 		
					 case "-Max":
						 try{
							 maximum = Integer.parseInt(args[i+1]);
							 i++;
						 } catch (NumberFormatException e) {
								System.err.println("Not a number passed around " + args[i]);
								System.exit(0);
						 }
						 break;
					 case "-Min":
						 try{
							 minimum = Integer.parseInt(args[i+1]);
							 i++;
						 } catch (NumberFormatException e) {
								System.err.println("Not a number passed around " + args[i]);
								System.exit(0);
						 }
						 break;
					 default: 
						 System.err.println("Error "+"at around "+args[i]);
						 System.exit(0);
						 break;
				 }
			
			 }
	}
	
	public static String DateDemo () 
	{
		      Date dNow = new Date( );
		      SimpleDateFormat ft = 
		      new SimpleDateFormat ("MM.dd.yyyy'@'hh.mma");
		      return ft.format(dNow);
	}
	
	
}
