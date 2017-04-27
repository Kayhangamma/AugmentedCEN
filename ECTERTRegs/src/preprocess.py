'''
Created on Oct 28, 2016

@author: US
'''

import csv

class preprocess:

    inc_transactions = {}
    inc_priority = {}

    def __init__(self, transcPath, fullPath):
    
        self.trsancationOSPath = transcPath
        self.comprehensivePath = fullPath
    
    def buildPriorities(self):
        cr = csv.reader(open(self.comprehensivePath,"rb"))
        for row in cr:
            self.inc_priority[row[0]] = row[6]
        
                    
    
    
    def buildTransactionSet (self):
        self.buildPriorities()
        
        cr = csv.reader(open(self.trsancationOSPath,"rb"))
        for row in cr:    
            temp =[]
            temp.extend(row[1:])
            if (row[0] in self.inc_priority) and (self.inc_priority[row[0]]!="0"):
                temp.append(self.inc_priority[row[0]])
                if row[0] not in self.inc_transactions:
                    self.inc_transactions[row[0]]= []
                self.inc_transactions[row[0]].append(temp)
                
        #print self.inc_transactions["IM02362584"]
    
        '''
        Constructor
        '''
        