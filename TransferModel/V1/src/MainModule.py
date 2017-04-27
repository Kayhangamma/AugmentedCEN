'''
Created on Apr 20, 2014

@author: Kayhan
'''
from reader import reader 
from transferGreedy import greedy
from hierarchy import taxonomy
from AdjacencyMatrix import adjMatrix
import csv

def main ():
    # read tickets form content
    incidentPath = "files\\TrainIncidents.csv"
    transferPath = "files\\TrainTransfers.csv"
    stopPath = "files\\stop.csv"
    puncsPath = "files\\puncs.txt"
    matlabSolved = "files\\SolvedByMatlab.csv"
    
    
    '''Adjacency Matrix code'''
    adjInputFilePath = "files\\transferHistory.csv"
    adjOutputFilePath = "AdjacencyTransferMatrix.csv"
    
    adjMatrixObj = adjMatrix(adjInputFilePath)
    adjMatrixObj.buildAdjMatrix(adjOutputFilePath)
    '''Adjacency ends here'''
    
    '''Taxonomy Code'''
    hierarchyPath = "files\\UniqueGroups.csv"
    
    organTax = taxonomy (hierarchyPath) 
    organTax.structureConstruction("root")
    
    '''Taxonomy Ends here'''
    
    

    readIncident = reader()
    inc_content = readIncident.readContent(incidentPath, stopPath, puncsPath)
    inc_transfer =  readIncident.readTransfers(transferPath)
    
    matlabSolvedTickets = readIncident.readSolvedByMatlab(matlabSolved)
  
    '''for x in inc_content:
        if x =="IM01690839" or x =="IM01646400":
            print (x), inc_content[x]'''
        
    #print len(inc_transfer)
    #print len(inc_content)
    
    TG = greedy()
    (groupProb, groupCount, comprehensiveGroups) = TG.transferProb(inc_transfer)    
    '''print groupProb ['INIT']['NSC-PCT-INCOMING']
    print groupCount ['INIT']['NSC-PCT-INCOMING']
    print groupCount ['INIT'] '''
    
    #group trans
    (edgeWordProb, totalNumberOfUniqueWords) = TG.edgeProb(inc_transfer, inc_content)
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['error']
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['pct']
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['the']    
    
     
    #Testing The model:
    incidentTestPath = "files\\TestIncidents.csv"
    transferTestPath = "files\\TestTransfer.csv"

    inc_test_content = readIncident.readContent(incidentTestPath, stopPath, puncsPath)
    inc_test_transfer =  readIncident.readTransfers(transferTestPath)
    inc_test_assign = readIncident.readAssignmentGroup(incidentTestPath)
    #inc_test_initial = readIncident.readInitialGroup(incidentTestPath)

    failedSequences = 0
    c = csv.writer(open("results.csv", "wb"))
    index = 0
    singleSolver = 0
    solved = 0
    zero = 0
    
    for  x in inc_test_assign:
        """
        if x in matlabSolvedTickets:
            print x
        else:
            continue
    
        if x != "IM01712936":
            continue         
            """       
        if index == 1000:
            break
        print  x
        index +=1
        
        if len(inc_test_transfer[x]) == 1:
            singleSolver +=1;
            continue;
                
        initial = inc_test_transfer[x][0]
        resolver = inc_test_transfer[x][len(inc_test_transfer[x])-1]
     
        actualGroup = inc_test_transfer[x]
        #print (index+1)
        probabilityMatrix = TG.calculateProbabilityMatrix(groupProb, edgeWordProb, inc_test_content[x], totalNumberOfUniqueWords)
        predictedGroup = TG.greedyTransfer(comprehensiveGroups,resolver,initial, probabilityMatrix)
        #c.writerow([x,actualGroup, predictedGroup])
        
        """Exactly what that happening in MATLAB code"""
        if initial == resolver:
            c.writerow([x,actualGroup, initial,"resolved"])
            solved +=1
        
        elif len(predictedGroup) > 11:
            c.writerow([x,actualGroup, predictedGroup,"not-resolved"])
            failedSequences +=1
            
        elif len(predictedGroup) ==0:
            c.writerow([x,actualGroup, predictedGroup,"not-resolved"])
            zero +=1
        else:
            c.writerow([x,actualGroup, predictedGroup,"resolved"])
            solved +=1    
        

    print "Finished!"
    print ("Number of failed incidents:" + str(failedSequences) + "\n")
    print ("Number of single solver: " + str(singleSolver))
    print ("Number of solved: " + str(solved))
    print ("Number of zero: " + str(zero))
if __name__ == '__main__':
    main()