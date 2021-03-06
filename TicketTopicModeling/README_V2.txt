
Automated Topic Modeling, Version 2.6
Authors team: Kayhan Moharreri, Naman Mody, Vijayalakshmi Dhamodharan, Nishita Yalamanchili
---------------------------------
This package includes three sub folders. "Example" is just a sample Topic modeling project for starters to see how the system works. In case users want to add more functionality to this package, we have provided the "Source Code" folder. The tool itself resides in the "Tool" folder.    

Here are instruction to Automated Topic Modeling Tool:

This tool is very helpful application in finding latent topics in a large corpus of unstructured text. Right now the tool is developed for "Windows" operating systems only.

1- To run this tool you need to have MALLET installed on your PC. Here are the steps to download and install mallet:

	I) Go to the MALLET project page, and download MALLET. (As of this writing, we are working with version 2.0.7.)
	
	II) You will also need the Java developer�s kit (JDK 6 or higher)� that is, not the regular Java that�s on every computer, but the one that lets you program things. Install this on your computer.
	
	III) Unzip MALLET into your C: directory . This is important: it cannot be anywhere else. You will then have a directory called C:\mallet-2.0.7 or similar. For simplicity�s sake, rename this directory just mallet.
	
	IV) MALLET uses an environment variable to tell the computer where to find all the various components of its processes when it is running. It�s rather like a shortcut for the program. A programmer cannot know exactly where every user will install a program, so the programmer creates a variable in the code that will always stand in for that location. We tell the computer, once, where that location is by setting the environment variable. If you moved the program to a new location, you�d have to change the variable.
	** To create an environment variable in Windows 7, click on your Start Menu -> Control Panel -> System -> Advanced System Settings. Click new and type MALLET_HOME in the variable name box. It must be like this � all caps, with an underscore � since that is the shortcut that the programmer built into the program and all of its subroutines. Then type the exact path (location) of where you unzipped MALLET in the variable value, e.g., c:\mallet

2- Now you are all set to run the tool on you dataset. Within this package, there are two other files, 1) Launch.bat and 2) topicModeling.jar

3- Your data file should be an excel 2003 file and should be named exactly as "Data.xls". Please make sure that there is a freeform field in your data file that you want to perform topic modeling on. This file should be placed on the same path as launch.bat and topicModeling.jar

4- Now right click on launch.bat and click edit.  You can use different optional switch variables based on your purpose. 
	-Max [NUM]           : Identify the maximum number of topics. By default maximum is pre-assigned to 8. 
	-Min [NUM]	     : Identify the minimum number of topics. By default maximum is pre-assigned to 2. 
	-Blacklist [FILE.txt]: Give explicitly the words that are to be ignored in your dataset. By default English language stop words (a, the , an, and, ...) are used. To add more to the stop words list, this switch should be followed by a text file which includes all those "BLACK" words in a space separated fashion. 
	-Fields		     : The fields that you plan to perform topic modeling on. By default the tool is sensitive to "Resolution" field. but it can be specified using this switch. Please make sure that your field name does not include any space character. example of wrong input: "-Fields Test Resolution"
	-Groups	[FILE.txt]   : provide term and groups of words that are very likely to follow the same order most of the times. Note that each line corresponds to one group of words.	    
	-Eqlist [FILE.txt]   : provide equal terms and phrases. All the right hand side terms will be replaced by the reference term. Each line of the txt file should follow this pattern: Reference = term1, term2, term3

EXAMPLE: Java -jar topicMoling.jar -Max 6 -Min 3 -Blacklist black.txt -Fields Description -Groups myGroup.txt -Eqlist eq.txt

5- After forming your query by editing the launch file, save the launch.bat file. Double click on "launch.bat" to run your query. Now you should see windows command prompt running some automated iterative OS jobs. After that the command prompt windows are closed, open "Autogenerated\Topics" folder. open the existing ".csv" files to see the topics generated from you dataset. Also you can open the "Results" folder to find out the dominating topic for each free form text.


Should you have any questions please contact the authors.      
	
