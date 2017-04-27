'''
Created on Apr 20, 2014

@author: Kayhan
'''
from reader import reader 
from transferGreedy import greedy
import csv

def main ():
    # read tickets form content
    incidentPath = "files\\TrainIncidents.csv"
    transferPath = "files\\TrainTransfers.csv"
    stopPath = "files\\stop.csv"

    readIncident = reader()
    inc_content = readIncident.readContent(incidentPath, stopPath)
    inc_transfer =  readIncident.readTransfers(transferPath)
  
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
    edgeWordProb = TG.edgeProb(inc_transfer, inc_content)
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['error']
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['pct']
    #print edgeWordProb[('INIT','NSC-PCT-INCOMING')]['the']
    
     
    #Testing The model:
    incidentTestPath = "files\\TestIncidents.csv"
    transferTestPath = "files\\TestTransfer.csv"

    inc_test_content = readIncident.readContent(incidentTestPath, stopPath)
    inc_test_transfer =  readIncident.readTransfers(transferTestPath)
    inc_test_assign = readIncident.readAssignmentGroup(incidentTestPath)

    
    c = csv.writer(open("results.csv", "wb"))
    index = 0
    for  x in inc_test_assign:
        resolver = inc_test_assign[x]
        """actualGroupCount = len(inc_test_transfer[x]) - 1
        predictedGroupCount = TG.greedyTransfer(groupProb, edgeWordProb, inc_test_content[x], comprehensiveGroups,resolver)
        
        c.writerow([x,actualGroupCount, predictedGroupCount])
        """
        actualGroup = inc_test_transfer[x]
        print index+1
        predictedGroup = TG.greedyTransfer(groupProb, edgeWordProb, inc_test_content[x], comprehensiveGroups,resolver)
        c.writerow([x,actualGroup, predictedGroup])

        if index ==200:
            break
        index +=1

if __name__ == '__main__':
    main()