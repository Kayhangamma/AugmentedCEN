'''
Created on Jun 30, 2014

@author: mohark1
'''
import csv

class fileWriter:
    
    def csvWrite (self, headings, data, filePath):
        c = csv.writer(open(filePath, "wb"))
        c.writerow(headings)
        for li in data:
            c.writerow(li)
                          
            
        
        
        