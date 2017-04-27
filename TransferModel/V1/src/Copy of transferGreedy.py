'''
Created on Apr 22, 2014

@author: Kayhan
'''

import math
import operator
import sys
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
        
        
    def greedyTransfer(self, groupProb, edgeWordProb, test_text, comprehensiveGroups, resolver, initial):
        
        
        includedSet = [initial]
        
        words= test_text.split()
        
        """2. Taking any group as target, then compute the denumerator using the log_sum_exp approach"""
        zeroDenum = 0
        denumerators = {}
        for source in comprehensiveGroups:
            flag = False
            denumerator = 0            
            for target in comprehensiveGroups:                
                if source in groupProb:
                    if target in groupProb[source]:
                        value = groupProb[source][target]
                        denumerator += value
                        flag = True
             
            if denumerator > 0:            
                denumerators[source] = math.log(denumerator)
            else:
                zeroDenum += 1
                denumerators[source] = 0                
                
            #sys.stdout.write(source + "\tmax:" + str(max_log_sum_exp[source]) + "\tdenum:" + str(denumerators[source]) +"\n")
            #sys.stdout.write(source + "\tMax:" + str(denumerators[source]) +"\n")
        #print "Zero denum: " + str(zeroDenum)        
        
        """3. Finding the most optimized transfer sequence"""       
        while len(includedSet)<=10:
            
            leftOverGroups = self.delta(comprehensiveGroups,includedSet)
            s_tProbs = {}
            for source in includedSet:
                for target in leftOverGroups:
                    if source in groupProb:
                        if target in groupProb[source]:
                            numerator = math.log (groupProb[source][target])    
                            s_tProbs[(source, target)] = numerator - denumerators[source]
                            #if denumerators[source] == 0 and groupProb[source][target] != 0:
                             #   print "What is going on dude?!"                             
                            #s_tProbs[(source, target)] = numerator
                            #sys.stdout.write("Numerator: " + str(numerator) + "\tDenum: " + str(denumerators[source]) + "\tMax: " + str(max_log_sum_exp[source]) + "\tProb: " + str(s_tProbs[(source, target)]) + "\n")
                            #print(numerator)
            if len(s_tProbs) == 0:
                includedSet = {}
                return includedSet
            
            maxPair = max(s_tProbs.items(), key=operator.itemgetter(1))[0]            
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
    
    