'''
Created on Mar 27, 2017

@author: US
'''

import csv
from Tarjan import Graph
import operator 
from hierarchy import taxonomy

def fileToDic(fileName):
    cr = csv.reader(open(fileName,"rb"))
    
    dataDic = {}
    
    for row in cr:
        cur_line = row[1]
        dataDic [row[0]] = cur_line.split(",")
    return dataDic
    
def buildGraph (Data, Min_FEQ_Threshold):
    vertices = {}
    
    for inc in Data:
        for expert in Data[inc]:
            if expert not in vertices:
                vertices[expert] = 1
            else:
                vertices[expert] += 1
                
    
     
    
    g1 = Graph (len(vertices))
    
    adjacency_weight = {}
    for inc in Data:
        for i in range(len(Data[inc])):
            if i+1<len(Data[inc]):
                u = Data[inc][i]
                v = Data[inc][i+1]
                
                if (u, v) not in adjacency_weight:
                    adjacency_weight [(u, v)] = 1
                else:
                    adjacency_weight [(u, v)] += 1
    
    expert_indexer = {}
    inverse_expert_indexer = {}
    
    
    index = 0
    for (u,v) in adjacency_weight:
        if adjacency_weight[(u,v)]>Min_FEQ_Threshold:
            if u not in expert_indexer:
                expert_indexer[u] = index
                inverse_expert_indexer [index] = u
                index+=1
            if v not in expert_indexer:
                expert_indexer[v] = index
                inverse_expert_indexer [index] = v
                index+=1
                
                
    g1 = Graph (len(expert_indexer))
    
    for (u,v) in adjacency_weight:
        if adjacency_weight[(u,v)]>Min_FEQ_Threshold:    
            g1.addEdge(expert_indexer[u], expert_indexer[v])
    #print inverse_expert_indexer
  
    composition = g1.SCC()  
    SCC_list = invertIndexComposition(composition, inverse_expert_indexer)
    
    #print "\nSSC in print graph "
    #print composition
    
    Final_SCC_list = []
    for scc in SCC_list: 
        if len(scc)!=1:
            Final_SCC_list.append(scc)
            
            
    
    #Only test
    Final_SCC_list = []
    for (u,v) in adjacency_weight:
        if adjacency_weight[(u,v)]>Min_FEQ_Threshold:
            Final_SCC_list.append([u,v])
    
    
    return Final_SCC_list, adjacency_weight
        
        

def distance (u,v, orgTree):
    return orgTree.distanceOnTree(u, v)


def diamProducer (scc, adjacency_weight, orgTree, Min_FEQ_Threshold):
    scc_adjacency = {}
    for i in range(len(scc)):
        for j in range(len(scc)):
            if i<j:
            #if i!=j:
                if (scc[i],scc[j]) in adjacency_weight:
                    if adjacency_weight[(scc[i],scc[j])]>Min_FEQ_Threshold:
                        scc_adjacency[(scc[i],scc[j])] = adjacency_weight[(scc[i],scc[j])]
                      
    
    collectedSum = 0.00
    collectedWeights = 0.00
    for (u,v) in scc_adjacency:
        #collectedSum += scc_adjacency[(u,v)]*distance(u,v, orgTree)
        #collectedWeights += scc_adjacency[(u,v)]
        
        collectedSum += distance(u,v, orgTree)
        collectedWeights += 1.00
        
        
    return collectedSum/collectedWeights

    
def diameterCalculator(Final_SCC_list, adjacency_weight, orgTree, Min_FEQ_Threshold):
    diameterDistribution = []
    diam_sum = 0.00
    for scc in Final_SCC_list:
        diam = diamProducer(scc, adjacency_weight, orgTree, Min_FEQ_Threshold)
        diameterDistribution.append(diam)
        diam_sum += diam
    
    
    diam_average = (diam_sum/len(Final_SCC_list))
    
    return diam_average,diameterDistribution
            
def invertIndexComposition(composition, inverse_expert_indexer):
    indexMapped_composit = []
    for array in composition:
        indexMapped_array = []
        for el in array:
            indexMapped_array.append(inverse_expert_indexer[el])
        indexMapped_composit.append(indexMapped_array)
    return indexMapped_composit        
        
    

def main():
    
    hierarchyPath = "files\\UniqueGroups.csv"
    organTax = taxonomy (hierarchyPath) 
    orgTree = organTax.structureConstruction("root")

    
    Min_FEQ_Threshold = 0
    
    while (Min_FEQ_Threshold<=260):
    
    
        Data = fileToDic("files\\IncidentPath.csv")
        Final_SCC_list, adjacency_weight = buildGraph(Data,Min_FEQ_Threshold)    
        
        
        diam_average, diameterDistribution = diameterCalculator (Final_SCC_list, adjacency_weight, orgTree, Min_FEQ_Threshold)
        
        print str(Min_FEQ_Threshold)+","+str(diam_average)+","+str(len(Final_SCC_list))
        
        Min_FEQ_Threshold+=1
    

if __name__ == '__main__':
    main()