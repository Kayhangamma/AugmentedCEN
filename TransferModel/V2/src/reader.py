'''
Created on Apr 21, 2014

@author: Kayhan
'''

import csv
import re

class reader:
    '''
    classdocs
    '''


    def __init__(self):
        
        #Constructor
        pass
      
    def removeStopWords (self, myString, stopDictionary):
        myString = myString.lower()
        redacted = ' '.join(w for w in myString.split() if w not in stopDictionary)
        #redacted = ' '.join(w for w in re.split('\s|,|.|\n|\t',myString) if w not in stopDictionary)
        return redacted
        
        
    def readContent (self, path, stop):
        content = {}
        stopwords = {}
        
        cr = csv.reader(open(stop,"rb"))
        for row in cr:
            stopwords[row[0]] = 1
        
        
        
        cr = csv.reader(open(path,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    content [row[0]] = self.removeStopWords(row[2], stopwords)
                else:
                    print "Error"
        
            rowNum += 1
        
        return content
    
    def readTransfers (self, path):
        content = {}
        cr = csv.reader(open(path,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    content [row[0]] = ["INIT", row[1]]
                else:
                    content [row[0]].append(row[1])
        
            rowNum += 1
        
        return content
    
    def readAssignmentGroup(self, incidentTestPath):
        content = {}    
        
        
        cr = csv.reader(open(incidentTestPath,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    content [row[0]] = row[10]
                else:
                    print "Error"
        
            rowNum += 1
        
        return content    
        