'''
Modified on Jul 2, 2015

@author: mohark1
'''

import csv

class readwrite:
    
    
    def readcsv(self, targetPath):
        cr = csv.reader(open(targetPath,"rb"))
        FullData = []
        counter = 0
        for row in cr:
            if counter == 0:
                counter +=1
                continue
            
            ticketID = row[0]
            ticketDesc = row[2]
            path = row[23]
            
            dataRow = [ticketID, ticketDesc, path]
            FullData.append(dataRow)
        return FullData
            
        
        