'''
Created on Nov 2, 2014

@author: Kayhan
'''

import csv

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
        return redacted
        
    def removePuncs (self, myString, puncsDictionary):        
        myString= myString.lower()
        redacted = ""
        for w in myString.split():
            chars = list(w)
            v = ""
            for c in chars:
                if c not in puncsDictionary:
                    v +=c
                else:
                    v += " "  
            redacted += " " + v.strip()    
        return redacted.strip()
        
    def readContent (self, path, stop, puncs):
        content = {}
        stopwords = {}
        puncWords = {}
        
        cr = csv.reader(open(stop,"rb"))
        for row in cr:            
            stopwords[row[0]] = 1
        
        puncFile = open(puncs, "r")
        puncs = puncFile.read()        
        for punc in puncs.split('\n'):            
            puncWords[punc] = "1"
              
        cr = csv.reader(open(path,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    tmpStr = self.removePuncs(row[2], puncWords)
                    tmpStr = self.removeStopWords(tmpStr, stopwords)                    
                    content [row[0]] = tmpStr               
                else:
                    print ("Error")
        
            rowNum += 1
    
        return content
    
    def readTransfers (self, path):
        content = {}
        cr = csv.reader(open(path,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:              
                if row[0] not in content:
                    content [row[0]] = [row[1]]                    
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
                    print ("Error")
        
            rowNum += 1
        
        return content    
