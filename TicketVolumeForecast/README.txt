
Autoregressive Firewall Forecast tool, Version 2.0
Predicting the volume of firewall requests per month through ensemble learning (Aggregated autoregressive forecasting)
Authors team: Kayhan Moharreri (mohark1@nationwide.com), Travis Lenocker (Lenockt@nationwide.com)
------------------------------------------------------------------------------

Prerequisites:

This package includes a source folder containing the source code in Python. To be able to run the tool on Windows here are the requirements:
1. Download Python 2.7.9 or higher from here: https://www.python.org/download/releases/2.7/
2. Install Python in a convenient directory of your choice.
3. Add python.exe to your path through Environment Variables:
	- Go to Computer -> Properties -> Advanced System settings -> Environment Variables
	- In this dialog box, you can add or modify User and System variables. To change System variables, you need non-restricted access to your machine (i.e. Administrator rights).
	- Under system variables find the variable "Path". 
	- Edit "Path", and append the path to your "python.exe" file to the end of the variable value. Then click OK, OK and OK.
	- Just to check that Python path is working, open command prompt and type in: Python. Should be able to see the version as it initiates Python interactive shell (>>>)
4. Make sure that you have "pip" working. 
	- open command prompt and type in "pip -V". You should be able to see the version of your pip.
	- If pip was undefined, add "Python27\Scripts\pip.exe" to the path through Environment Variables.
5. Download and install appropriate versions of SciPy, and NumPy from here: http://www.lfd.uci.edu/~gohlke/pythonlibs/
	- You should have two ".whl" files corresponding to SciPy and NumPy
	- Install each of the packages by running this command "pip install your-package.whl"
6. Download and install Scikit-learn:
	- Make sure you have good internet connection with no download restrictions.
	- Simply run: "pip install -U scikit-learn"

------------------------------------------------------------------------------
Running the tool:

1. Go to the src\files folder and make sure to edit the input.csv file appropriately to insert/delete/update your numbers.
 	- Use a text editor to edit ".csv" file. Using excel is discouraged. 
2- Open the command prompt and switch to the directory that the src folder resides.
	- type in: "MainModule.py"
	- If the input file is corrupt (wrong format) a self-descriptive error should pop up. 
	- Otherwise, it should initiate the autoregressive Forecast on your input file.
 
3- After a successful run you should be able to see "src\files\Model_Summary_Predictions" folder in which:
	- ModelsDetails.csv provides a summary of the forecasting models in use (How good they fit the data, Weight coefficients, etc).
	- Predictions.csv basically provides value predictions that was generated independently for each model. 
	- A quick way to aggregate the results is to get an average of forecasted values and set that as your forecasted result for the next month.


 
  

