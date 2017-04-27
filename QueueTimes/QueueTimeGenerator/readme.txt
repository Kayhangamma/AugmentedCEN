# Tool Engineer: Kayhan Moharreri / Email: kayhan.moharreri@gmail.com

# Tool Support in NW: Travis Lenocker / Email: lenokt@nationwide.com

# Data Support in NW: Corey Luczak / Email: corey.luczak@nationwide.com



1)Download and Install Python 2.7 from:

https://www.python.org/downloads/



2) Add python to PATH as an Environment Variables:

http://stackoverflow.com/questions/3701646/how-to-add-to-the-pythonpath-in-windows-7




3) Make sure to have read access to the data directory:

For example here it is a network path by default:
\\ohlewnas0260\Reporting & Analytics\LUCZAKC\3b. Content\EZ Path\processedPageFiles


4) Edit the first line of launch.bat according to your purpose. This format should be preserved: 
    python ./Tool/MainModule.py [DIRECTORY_PATH_TO_THE_CSV_FILES_IN_DOUBLE_QUOTES] [START_YEAR_MONTH] [END_YEAR_MONTH]

  - Dates and the path to the input csv files are flexible and are up to the user
  - [START_YEAR_MONTH] has to follow YYYY-MM Format.
  - [END_YEAR_MONTH] also has to follow YYYY-MM Format.
  - Example: python ./Tool/MainModule.py "\\ohlewnas0260\Reporting & Analytics\LUCZAKC\3b. Content\EZ Path\processedPageFiles" 2015-03 2016-02
  - The script runs on the time interval specified by the user inclusive of the bounds

5) Save your edits and then run the launch.bat file.

6) That's it. Still need help? Contact the tool Engineers noted above.