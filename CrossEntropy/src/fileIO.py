'''
Modified on Jul 2, 2015

@author: mohark1
'''



import csv
import re


class readwrite:
    
    def remove_non_ascii(self, text):
        return re.sub(r'[^\x00-\x7F]+',' ', text)
        
    def readtraincsv(self, targetPath):
        cr = csv.reader(open(targetPath,"rb"))
        FullData = []
        counter = 0
        for row in cr:
            if counter == 0:
                counter +=1
                continue
            
            ticketID = row[0]
            ticketDesc = self.remove_non_ascii(row[1])
            #path = row[13]
            path = row[7]
            
            
            
            
            dataRow = [ticketID, ticketDesc, path]
            FullData.append(dataRow)
            
        return FullData
    
    def readtestcsv(self, targetPath):
        cr = csv.reader(open(targetPath,"rb"))
        FullData = []
        counter = 0
        for row in cr:
            if counter == 0:
                counter +=1
                continue
            
            ticketID = row[0]
            ticketDesc = self.remove_non_ascii(row[1])
            #path = row[16]
            path = row[12]
            
            
            
            
            dataRow = [ticketID, ticketDesc, path]
            FullData.append(dataRow)
            
        return FullData
            
        
        