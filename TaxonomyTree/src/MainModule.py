'''
Created on Nov 2, 2014

@author: Kayhan
'''
from reader import reader 
from hierarchy import taxonomy

def main ():
    # read tickets form content
    hierarchyPath = "files\\UniqueGroups.csv"
    Inc_PathPath = "C:\\Kayhan\\OS University\\Research\\Jay\TransferGroups\\Data\\2016\\weekly-2016\\paths\\Inc_paths_2015-03-01 to 2016-02-29.csv" 
    Inc_TransferDist_Path = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\TransferDistance\\tansDist_2015-03-01 to 2016-02-29.csv"
    
    '''Taxonomy Code'''
    
    organTax = taxonomy (hierarchyPath) 
    orgTree = organTax.structureConstruction("root")
    '''Taxonomy Ends here'''
    
    '''Sample Average Taxonomy Distance starts'''
    #Group1 = "NI-INTERNET-SALES-POWERSPORTS"
    #Group2 = "AES-NFEA-DBA-RR"
    #print orgTree.distanceOnTree(Group1, Group2)
    '''Sample Average Taxonomy Distance ends'''
    
    readwriteObj = reader()
    pathSet = readwriteObj.readIncPath(Inc_PathPath)
    
    avgTransferDist = {}
    index = 0
    for inc in pathSet:
        index+=1
        print index
        current = pathSet[inc]
        pathLength = len(current) 
        if pathLength>1:
            sumAll = 0.00
            for i in range(pathLength-1):
                sumAll += float(orgTree.distanceOnTree(current[i], current[i+1]))
            avgDis = sumAll/float(pathLength-1)
            avgTransferDist[inc] = avgDis
                
        else:
            avgTransferDist[inc] = 0.00 
    
    readwriteObj.writeTransferDist(avgTransferDist,Inc_TransferDist_Path)
    
    
        
    

if __name__ == '__main__':
    main()