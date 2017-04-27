'''
Created on Oct 9, 2016

@author: US
'''

import re
from nltk.corpus import stopwords

from fileIO import readwrite

import math


def textredactor(_input):
    
    result = ''.join([i for i in _input if not i.isdigit()])
    result = re.sub('[!@#$?\[\]./;:,~\\_()`*%&^=+|{}"]', '', result)
    return result 
    
def documentPrep (doc):
    doc = textredactor(doc)
    doc =doc.lower()
    doc = doc.replace('-',' ')
    words = doc.split()
    
    filtered_words = [word for word in words if word not in stopwords.words('english')]
    
    return filtered_words
    
def probMaker(documentSet):
    freq = {}
    prob = {}
    
    wordCount = 0.00
    
    for doc in documentSet:
        words = documentPrep(doc)

        
        
        for word in words:
            wordCount+=1.00
            if word not in freq:
                freq[word] = 1.00
            else:
                freq[word]+=1.00
    
    vocab_size = len(freq)
    for el in freq:
        prob[el] = (float(freq[el])+1.00)/(wordCount+vocab_size)
    prob['UNK$$'] = 1.00/(wordCount+vocab_size)
        
        
    
    return prob
    

def train(trainFilePath):
    rw = readwrite()
    trainDataset = rw.readtraincsv(trainFilePath)
      
    pathdict = {}
    for inc, des, path in trainDataset:
        if path not in pathdict:
            pathdict[path] = []
        pathdict[path].append(des)
        
   
    
    
    
    '''
    blu = probMaker(pathdict['NF-NW-BANK-LEVEL2'])
    import operator
    sorted_x = sorted(blu.items(), key=operator.itemgetter(1), reverse=True)
    for el in sorted_x:
        print el
    '''
    
    Grand_LM = {}
    
    index = 0
    for path in pathdict:
        index+=1
        print index
        Grand_LM[path] = probMaker(pathdict[path])
        
        
    
    
    
    return Grand_LM


def test(testFilePath,Grand_LM):
    crossEnt = {}
    
    rw = readwrite()
    testDataset = rw.readtestcsv(testFilePath)
    
    for inc, des, path in testDataset:
        if path not in Grand_LM:
            crossEnt[inc] = "#NA"
            continue
        words = documentPrep(des)
        
        crossEnt[inc] = 0.00
        _sum = 0.00
        for word in words:
            if word in Grand_LM[path]:
                coef = Grand_LM[path][word]
            else:
                coef = Grand_LM[path]['UNK$$']
            
                
            _sum += math.log(coef,2)
        crossEnt[inc]= str((-1.00/len(words))*_sum)
    
    
    return crossEnt

def writeToCSV(OSpath, crossEnt):
    import csv
    c = csv.writer(open(OSpath, "wb"))    
    c.writerow(["Incident","CrossEntropy"])
    
    for inc in crossEnt:
        c.writerow([inc,crossEnt[inc]]) 
    
    return

def main():
    trainFilePath = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\RoutineTrain_short.csv"
    testFilePath = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\RoutineTest_short.csv"
    
    CrossEntropyPath = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\scikitModels\\Second\\ce\\CrossEntropy.csv"
    
    Grand_LM = train(trainFilePath)
    
    
    crossEnt = test(testFilePath,Grand_LM)
    
    writeToCSV(CrossEntropyPath, crossEnt)
    
    

    


    
    
        
    
        
    

if __name__ == '__main__':
    main()