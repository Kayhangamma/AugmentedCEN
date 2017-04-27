'''
Created on Apr 22, 2014

@author: Kayhan
'''

import math
import operator
import sys
from pydoc import resolve
from __builtin__ import str

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
        uniqueWords = {}
                           
        for edge in edgeContent:
            edgeContentProb[edge] = {}
            edgeContentFreq[edge] = {}
            for ticket in edgeContent[edge]:
                words = ticket.split()
                for term in words:                    
                    if term not in edgeContentFreq[edge]:
                        edgeContentFreq[edge][term] = 1.00
                    """Ignoring the frequency of all words""" 
                    #else:
                        #edgeContentFreq[edge][term] += 1.00
                    if term not in uniqueWords:
                        uniqueWords[term] = 1
            
            for term in edgeContentFreq[edge]:
                edgeContentProb[edge][term] = 1.0/len(edgeContentFreq[edge])
                #edgeContentProb[edge][term] = edgeContentFreq[edge][term]/(len(edgeContentFreq[edge]))                            
            """I made the next line as comment in order to leverage the exact formula which used in original implementation"""
            #edgeContentProb [edge]["UNK"] = 1/edgeTotalTokens[edge]
                
        return (edgeContentProb, len(uniqueWords))   

    
    def calculateProbabilityMatrix(self, groupProb, edgeWordProb, test_text, totalNumberOfUniqueWords):
        """Based on equation 15 in KDD'10"""
        lamb = 0.9        
        words = test_text.split();
                
        """1. Extract edgeProb values for words in given ticket"""
        edgeWordProbReduced = {}
        for edge in edgeWordProb:
            edgeWordProbReduced[edge] = {}
            for word in words:
                if word in edgeWordProb[edge]:
                    edgeWordProbReduced[edge][word] = (edgeWordProb[edge][word]*lamb)
                else:
                    edgeWordProbReduced[edge][word] = 0.0
        
        """2. Calculating Sig-Row-Sum matrix"""
        sigRowSum = {}
        for edge in edgeWordProbReduced:
            sigRowSum[edge] = 0.0
            for word in words:
                sigRowSum[edge] += edgeWordProbReduced[edge][word]
            sigRowSum[edge] = (abs(sigRowSum[edge] - 0.01) + 1.0)/2.0
        
        """3. Add smoothing value (1- lambda) to each probability of word per edge"""
        """4. Multiply SigRowSum(e) into each P(w|e)"""
        for edge in edgeWordProbReduced:            
            for word in words:
                edgeWordProbReduced[edge][word] += ((1.0-lamb)/totalNumberOfUniqueWords)
                edgeWordProbReduced[edge][word] *= sigRowSum[edge]
        
        """5. Computing the production over words, as part of equation 15 based on given test ticket"""
        """6. Multiplying group probability into result of above production"""
        probOfTicketPerEdge = {}
        for edge in edgeWordProbReduced:            
            tmpValue = 1.0            
            for word in words:
                tmpValue *= edgeWordProbReduced[edge][word]
                
            (src,trgt) = edge
            probOfTicketPerEdge[edge] = tmpValue * groupProb[src][trgt]            
        
        """Calculate the relation 15 for each pair of group i (i is initial) and j and given test ticket t"""
        """7. Calculate denumerator based on all possible initial groups"""
        denumerator = {}
        for edge in probOfTicketPerEdge:
            (init, target) = edge
            if init in denumerator:
                denumerator[init] += probOfTicketPerEdge[edge]
            else:
                denumerator[init] = probOfTicketPerEdge[edge]
        """8. Apply numerator per each edge"""        
        finalProbabilityMatrix = {}
        for edge in probOfTicketPerEdge:
            (init, target) = edge
            if denumerator[init] > 0: #Funny runtime error! there is difference between this value and 0 here, but not in devision time!!
                #print (str(denumerator[init]) )                
                finalProbabilityMatrix[edge] =  probOfTicketPerEdge[edge]/denumerator[init]
        
        return finalProbabilityMatrix
                             
    
    def greedyTransfer(self, comprehensiveGroups, resolver, initial, probabilityMatrix):
        
        includedSet = [initial]
        
        while len(includedSet) <= 11:
            
            leftOverGroups = self.delta(comprehensiveGroups,includedSet)
            s_tProbs = {}
            for source in includedSet:
                for target in leftOverGroups:
                    edge = (source, target)
                    if edge in probabilityMatrix:
                        s_tProbs[edge] = probabilityMatrix[edge]
                        print ("current edge: " + str(edge))
            
            if len(s_tProbs) == 0:
                includedSet = {}
                return includedSet
            
            maxPair = max(s_tProbs.iteritems(), key=operator.itemgetter(1))[0]
            if maxPair[1] ==  resolver:         
                break
            else:
                includedSet.append(maxPair[1])
                             
        return includedSet
        
        
    def delta(self, comprehensiveGroups,includedSet):   
        newList = []
        for x in comprehensiveGroups:
            if x not in includedSet:
                newList.append(x)
        
        return newList
    
    
    def greedyTransferOriginalModel(self, groupProb, edgeWordProb, test_text, comprehensiveGroups, resolver, initial):
                
        lamb = 0.9;
        betta = 0.1;
        
        includedSet = [initial]        
        words= test_text.split()
        
        """1. Finding the maximum value of numerator: b (regarding video to deal with underflow problem)"""
        max_log_sum_exp = {}
        for source in comprehensiveGroups:
            max_value = -100000 #someone may change this default value
            for target in comprehensiveGroups:                
                if source in groupProb:
                    if target in groupProb[source]:
                        value = math.log (groupProb[source][target])
                        
                        if (source, target) in edgeWordProb:
                            for term in words:
                                if term in edgeWordProb[(source, target)]:                                   
                                    value += math.log (edgeWordProb[(source, target)][term])
                                else:
                                    value += math.log (edgeWordProb[(source, target)]["UNK"])
                                    
                        if value > max_value:
                            max_value = value
            max_log_sum_exp[source] = max_value
            #sys.stdout.write(source + "\tmax: " + str(max_value) +"\n")
        
        
        """2. Taking any group as target, then compute the denumerator using the log_sum_exp approach"""
        denumerators = {}
        for source in comprehensiveGroups:
            denumerator = 0            
            for target in comprehensiveGroups:                
                if source in groupProb:
                    if target in groupProb[source]:
                        value = math.log (groupProb[source][target])
                        
                        if (source, target) in edgeWordProb:
                            for term in words:
                                if term in edgeWordProb[(source, target)]:                                   
                                    value += math.log (edgeWordProb[(source, target)][term])
                                else:
                                    value += math.log (edgeWordProb[(source, target)]["UNK"])
                                    
                        value -= max_log_sum_exp[source];
                        expValue = math.exp(value)
                        denumerator += expValue
            
            if denumerator > 0:
                denumerators[source] = math.log(denumerator) + max_log_sum_exp[source]
                #sys.stdout.write(source + "\tRawValue: " + str(math.log(denumerator)) +"\n")
            else:
                denumerators[source] = max_log_sum_exp[source]
                
            #sys.stdout.write(source + "\tmax:" + str(max_log_sum_exp[source]) + "\tdenum:" + str(denumerators[source]) +"\n")
            #sys.stdout.write(source + "\tMax:" + str(denumerators[source]) +"\n")
        
        
        """3. Finding the most optimized transfer sequence"""
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
                            """One change here: I shifted (by tab) following if condition to the right in order to be part of previous if condition!
                            I think logically we are not supposed to see any difference! But conceptually it is more likely to have this news code organization"""
                            if (source, target) in edgeWordProb:
                                for term in words:
                                    if term in edgeWordProb[(source, target)]:                                   
                                        numerator += math.log (edgeWordProb[(source, target)][term])
                                    else:
                                        numerator += math.log (edgeWordProb[(source, target)]["UNK"])
                                    
                            s_tProbs[(source, target)] = numerator - denumerators[source]
                            if denumerators[source] == -100000:
                                print "error! for -100000"
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
        