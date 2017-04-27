'''
Created on Oct 28, 2016

@author: moosas1
'''
from datetime import *
from time import *

def logicalToActualConversion():
    fw = open('convertedActualToLogicalTime.txt', 'w')
    
    #Read the input file 
    with open('dateToLogicalDate.txt') as f:
        content = f.readlines()
                
    #Parse the file and fill the dictionary
    for line in content:
        parts = line.split()
        oldTime = parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[5] 
        newTime = datetime(*strptime(oldTime, "%a %b %d %H:%M:%S %Y")[0:6]).strftime('%m/%d/%y %H:%M')
        fw.write(newTime + "\t" + parts[6] + "\n")
        
    fw.close()
    
logicalToActualConversion()        