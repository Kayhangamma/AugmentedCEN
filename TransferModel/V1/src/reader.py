'''
Created on Apr 21, 2014

@author: Kayhan
'''

import csv
import re
from string import lower

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
        
        # ordFrequency = {}
        #SinverseDocFrequency = {}
        
        cr = csv.reader(open(stop,"rt"))
        for row in cr:
            stopwords[row[0]] = 1
        
        puncFile = open(puncs, "r")
        puncs = puncFile.read()        
        for punc in puncs.split('\n'):            
            puncWords[punc] = "1"
              
        cr = csv.reader(open(path,"rt"))
        rowNum = 0
        for row in cr:
            #thisDocUniqueWords = {}
            if rowNum!=0:
                if row[0] not in content:
                    #print lower(row[2])
                    tmpStr = self.removeStopWords(lower(row[2]), stopwords)
                    tmpStr = self.removePuncs(tmpStr, puncWords)
                    content [row[0]] = tmpStr
                    
                    """
                    words = tmpStr.split()
                    for wrd in words:
                        if wrd in wordFrequency:
                            wordFrequency[wrd] += 1
                        else:
                            wordFrequency[wrd] = 1
                        if wrd not in thisDocUniqueWords:
                            thisDocUniqueWords[wrd] = 1                            
                    
                    for wrd in thisDocUniqueWords:
                        if wrd in inverseDocFrequency:
                            inverseDocFrequency[wrd] += 1
                        else:
                            inverseDocFrequency[wrd] = 1
                    """
                                                            
                else:
                    print ("Error")
        
            rowNum += 1
        """
        fw = open("E:/uniqueWords.txt", "w")
        fw.write("Word\tTF\tIDF\n")
        for word in wordFrequency:
            fw.write(word + "\t" + str(wordFrequency[word]) + "\t" + str(inverseDocFrequency[word]) + "\n")
        fw.close()
        
        
        print "Length of unique words: "
        print len(wordFrequency
        """
        
        return content
    
    def readTransfers (self, path):
        content = {}
        cr = csv.reader(open(path,"rt"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    #content [row[0]] = ["INIT", row[1]]
                    content [row[0]] = [row[1]]
                else:
                    content [row[0]].append(row[1])
        
            rowNum += 1
        
        return content
    
    def readAssignmentGroup(self, incidentTestPath):
        content = {}
        
        cr = csv.reader(open(incidentTestPath,"rt"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in content:
                    content [row[0]] = row[10]
                else:
                    print ("Error")
        
            rowNum += 1
        
        return content
    
    
    def readSolvedByMatlab(self, path):
        content = {}
        
        cr = csv.reader(open(path,"U"))        
        for row in cr:    
            if row[0] not in content:
                content [row[0]] = row[0]
                
        return content