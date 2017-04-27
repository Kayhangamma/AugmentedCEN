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
    
    def readIncPath(self, incidentTestPath):
        inc_path = {}
        
        cr = csv.reader(open(incidentTestPath,"rb"))
        rowNum = 0
        for row in cr:
            if rowNum!=0:
                if row[0] not in inc_path:
                    inc_path [row[0]] = row[2].split(',')
                else:
                    print ("Error")
        
            rowNum += 1
        
        return inc_path    
    
    def writeTransferDist(self, transferDic, Inc_TransferDist_Path):
        c = csv.writer(open(Inc_TransferDist_Path, "wb"))
        c.writerow(["Incident ID","Avg. Transfer Distance"])
        
        for inc in transferDic:
            c.writerow([inc,transferDic[inc]])
        
        
