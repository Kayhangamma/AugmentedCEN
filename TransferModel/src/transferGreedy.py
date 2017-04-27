'''
Created on Apr 22, 2014

@author: Kayhan
'''

import math
import operator
from pydoc import resolve

class greedy:
    '''
    classdocs
    '''
    

    def transferProb (self, trans):
        
        transCounts = {}
        groupTransfersProb = {} # Given Gi, calculate Gj
        for inc in trans:
            i = 0 
            while i< len(trans[inc])-1:
                if trans[inc][i] not in transCounts:
                    transCounts[trans[inc][i]] = {trans[inc][i+1]:1}
                else:
                    if trans[inc][i+1] not in transCounts[trans[inc][i]]:
                        transCounts[trans[inc][i]][trans[inc][i+1]] = 1
                    else:
                        transCounts[trans[inc][i]][trans[inc][i+1]] +=1
                  
                i+=1
                
        #Calculate group probabilities
        
        for x in transCounts:
            totalTrans = 0.00
            for y in transCounts[x]:
                totalTrans += transCounts[x][y]
            
            for y in transCounts[x]:
                if x not in groupTransfersProb:
                    groupTransfersProb[x] = {y:transCounts[x][y]/totalTrans}
                else:
                    groupTransfersProb[x][y] = transCounts[x][y]/totalTrans
            
            #groupTransfersProb[x]= {"OUTARANGE":1/totalTrans}
        
        comprehensiveGroups = {}
        for inc in trans:
            for y in trans[inc]:
                if y not in comprehensiveGroups:
                    comprehensiveGroups[y] = 1
        return (groupTransfersProb, transCounts, comprehensiveGroups.keys())        
                
                
    def edgeProb(self, transfer, content):
        
        edgeContent = {}
        for inc in transfer:
            i = 0 
            while i< len(transfer[inc])-1:
                edge = (transfer[inc][i],transfer[inc][i+1])
                if edge in edgeContent:
                    edgeContent[edge].append(content[inc])
                else:
                    edgeContent[edge] = []
                    edgeContent[edge].append(content[inc])
                
                i+=1
        
        edgeContentProb = {}
        edgeContentFreq = {}
        edgeTotalTokens = {}
        
        for edge in edgeContent:
            totalTokens = 0.00
            for ticket in edgeContent[edge]:
                totalTokens+= len(ticket.split())
            edgeTotalTokens [edge] = totalTokens
            
                           
        for edge in edgeContent:
            edgeContentProb[edge] = {}
            edgeContentFreq[edge] = {}
            for ticket in edgeContent[edge]:
                words = ticket.split()
                for term in words:
                    
                    if term in edgeContentFreq[edge]:
                        edgeContentFreq[edge][term] += 1.00 
                    else:
                        edgeContentFreq[edge][term] = 1.00
                        
            for term in edgeContentFreq[edge]:
                edgeContentProb [edge][term] = edgeContentFreq[edge][term]/edgeTotalTokens[edge]
            edgeContentProb [edge]["UNK"] = 1/edgeTotalTokens[edge]
                
        return edgeContentProb   
        
        
    def greedyTransfer(self, groupProb, edgeWordProb, test_text, comprehensiveGroups, resolver):
        
        
        includedSet = ["INIT"]
        words= test_text.split()
        
        while len(includedSet)<=11:
            
            leftOverGroups = self.delta(comprehensiveGroups,includedSet)
            s_tProbs = {}
            for source in includedSet:
                for target in leftOverGroups:
                    #prob calc P(target|t,source)
                    if source in groupProb:
                        if target in groupProb[source]:
                            numerator = math.log (groupProb[source][target])
                        #else:
                            #numerator = math.log (groupProb[source]["OUTARANGE"])
                
                        if (source, target) in edgeWordProb:
                            for term in words:
                                if term in edgeWordProb[(source, target)]:
                                    numerator += math.log (edgeWordProb[(source, target)][term])
                                else:
                                    numerator += math.log (edgeWordProb[(source, target)]["UNK"])
                                    
                            s_tProbs[(source, target)] = numerator
            
            maxPair = max(s_tProbs.iteritems(), key=operator.itemgetter(1))[0]
            if maxPair[1] ==  resolver:
                break
            else:
                includedSet.append(maxPair[1])                    
                                    
                                    
        #return len(includedSet)
        return includedSet
        
        
    def delta(self, comprehensiveGroups,includedSet):   
        newList = []
        for x in comprehensiveGroups:
            if x not in includedSet:
                newList.append(x)
        
        return newList
        
        
        
        