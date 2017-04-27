'''
Created on Nov 2, 2014

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
    
    '''Adjacency Matrix code
    adjInputFilePath = "files\\transferHistory.csv"
    adjOutputFilePath = "AdjacencyTransferMatrix.csv"
    
    adjMatrixObj = adjMatrix(adjInputFilePath)
    adjMatrixObj.buildAdjMatrix(adjOutputFilePath)
    '''
    
    '''Taxonomy Code'''
    hierarchyPath = "files\\UniqueGroups.csv"
    
    organTax = taxonomy (hierarchyPath) 
    uniqueGroupOverall = organTax.uniqueGroupOverall()
    orgTree = organTax.structureConstruction("root")
    
    '''Taxonomy Ends here'''

    readIncident = reader()

    inc_content = readIncident.readContent(incidentPath, stopPath, puncsPath)
    inc_transfer =  readIncident.readTransfers(transferPath)    
    
    TG = greedy()
    
    (groupProb, groupCount, comprehensiveGroups) = TG.transferProb(inc_transfer)    

    '''Smoothing begins'''
    print "Smoothing Begins"
    adjOrgutputFilePath = "TransferMatrixOriginal.csv"
    adjSMOutputFilePath = "TransferMatrixSmoothed.csv"
    gamma = 0.2
    (smoothedGroupProb, groupCount, comprehensiveGroups) = TG.transferProbSmoothed(inc_transfer,uniqueGroupOverall, orgTree, gamma, adjOrgutputFilePath, adjSMOutputFilePath)
    print "Smoothing Over"
    '''Smoothed ends'''
    
    #group trans
    (edgeWordProb, totalNumberOfUniqueWords) = TG.edgeProb(inc_transfer, inc_content)
     
    #Testing The model:
    incidentTestPath = "files\\TestIncidents.csv"
    transferTestPath = "files\\TestTransfer.csv"

    inc_test_content = readIncident.readContent(incidentTestPath, stopPath, puncsPath)
    inc_test_transfer =  readIncident.readTransfers(transferTestPath)
    inc_test_assign = readIncident.readAssignmentGroup(incidentTestPath)
   
    c = csv.writer(open("results.csv", "wb"))
    
    failedIncident = 0
    solvedIncident = 0
    singleSolver = 0
    index = 0
    
    print "Please wait ... Routing for test data"
    for  x in inc_test_assign:
        index +=1         
        if index%1000 == 0 :
            print index         
        
        #if index == 100:
        #    break
        
        if len(inc_test_transfer[x]) == 1:
            singleSolver +=1;
            continue;
        # Important Note - Initial group is taken from first hop decision by the experts.        
        initial = inc_test_transfer[x][0]
        resolver = inc_test_transfer[x][len(inc_test_transfer[x])-1]
     
        actualGroup = inc_test_transfer[x]
        probabilityMatrix = TG.calculateProbabilityMatrix(smoothedGroupProb, edgeWordProb, inc_test_content[x], totalNumberOfUniqueWords)
        predictedGroup = TG.greedyTransfer(comprehensiveGroups,resolver,initial, probabilityMatrix)
        c.writerow([x, actualGroup, predictedGroup, len(predictedGroup)])

        if len(predictedGroup) > 11:            
            failedIncident +=1
        else:
            solvedIncident +=1

    print "Finished!"
    print ("Number of failed incidents:" + str(failedIncident))
    print ("Number of solved incidents: " + str(solvedIncident))
    print ("Number of single solver: " + str(singleSolver))

if __name__ == '__main__':
    main()