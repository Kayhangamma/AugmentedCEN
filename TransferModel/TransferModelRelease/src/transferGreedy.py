'''
Created on Nov 3, 2014

@author: Kayhan
'''

import operator
import csv
import copy



class greedy:
    '''
    classdocs
    '''
    #Smoothing Related function
    def transferProbSmoothed(self, trans, uniqueGroupOverall, orgTree, gamma, fileOrgOut ,fileSmoOut):
        
        (groupProb,transCounts,comprehensiveGroupkeys)  = self.transferProb(trans)
        
        print "Please wait ... Smoothing" 
        smoothedGroupProb =copy.deepcopy(groupProb)
        indexw = 0
        for sourceGroup in uniqueGroupOverall:
            indexw+=1
            
            for targetGroup in uniqueGroupOverall:
                #if targetGroup == "CA-BPS":
                #   print "BULANG"
                if targetGroup != sourceGroup:
                    smoothie = self.parentSmoother (groupProb, sourceGroup, targetGroup, orgTree)

                    if smoothie != 0.00: 
                        first =(1.00-gamma)*(self.validDictionaryPair(groupProb, sourceGroup,targetGroup) )
                        second = gamma * smoothie
                        smoothedGroupProb = self.addIfNotInDic(smoothedGroupProb, sourceGroup, targetGroup, (first+second) )
                        #smoothedGroupProb[sourceGroup][targetGroup]= (first+second)
 
 
        #Normalization of SmoothedGroupProb:
        for src in smoothedGroupProb:
            rowSum = 0.00
            for trg in smoothedGroupProb[src]:
                rowSum += smoothedGroupProb[src][trg]
            for trg in  smoothedGroupProb[src]:
                smoothedGroupProb[src][trg] = (smoothedGroupProb[src][trg]/rowSum)
        #Return values:
        
        self.createCSV (groupProb, fileOrgOut)
        self.createCSV(smoothedGroupProb, fileSmoOut)
        return (smoothedGroupProb, transCounts, comprehensiveGroupkeys)  
        
    def createCSV (self, GroupProb,fileOut):
        c = csv.writer(open(fileOut, "wb"))
        c.writerow(["Sender", "Receiver", "Prob"])
        for x in GroupProb:
            for y in GroupProb[x]:
                currentRow = [x, y, GroupProb[x][y] ]
                c.writerow(currentRow) 
        

    def addIfNotInDic (self, dic, src, trg, value):
        if src in dic:
                dic[src][trg] = value
        else:
            dic[src] = {trg:value}
        return dic
          
    def validDictionaryPair (self, dic, src, trg):
        if src in dic:
            if trg in dic[src]:
                return dic[src][trg]
        return 0.00  
                    
    #Smoothing Related function    
    def parentSmoother (self, groupProb, sourceGroup, targetGroup, orgTree):
        sourceList = self.findSiblings (sourceGroup,orgTree)
        targetList = self.findSiblings (targetGroup,orgTree)
        avg= 0.00
        
        #Source to TargetSiblings
        countCase1 = 0
        sumCase1 = 0.00
        
        for TargetSibl in targetList:
            if sourceGroup in groupProb:
                if TargetSibl in groupProb[sourceGroup]:
                    sumCase1 += groupProb[sourceGroup][TargetSibl]
                    countCase1+=1
        
        #SourceSiblings to Target
        countCase2 = 0
        sumCase2 = 0.00
  
        for SourceSibl in sourceList:
            if SourceSibl in groupProb:
                if targetGroup in groupProb[SourceSibl]:
                    sumCase2 +=groupProb[SourceSibl][targetGroup]
                    countCase2+=1
        
        #SourceSiblings to TargetSiblings is ignored.
                    
        if (countCase1+countCase2) != 0:
            avg = (sumCase1+sumCase2)/(countCase1+countCase2)
        return avg
        
    #Smoothing Related function    
    def findSiblings (self, sampleGroup,orgTree):
        groupSequence = sampleGroup.split('-')
        current = orgTree.root
        for seq in groupSequence:
            if seq in current.children:
                current = current.children[seq]
        #current is leaf needed
        probingNode = current
        #if bool(probingNode.children) == True:
        #Has kids already
        
        probingNode = probingNode.parent
        while len(probingNode.children) ==1:
            if probingNode.ligitTerminal==True:
                break
            probingNode = probingNode.parent
        
        siblings = self.allPossibleChildren (probingNode)
        
        #remove self
        siblings.remove(sampleGroup)
        
        return siblings
            
    #Smoothing Related function
    def allPossibleChildren (self, probingNode):
        siblings = []
        if probingNode.ligitTerminal==True:
            siblings.append(probingNode.fullName)
        for kid in probingNode.children:
            siblings = siblings + self.allPossibleChildren(probingNode.children[kid]) 
        
        return siblings
        

    
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
                thisTicketUniques = {}
                for term in words:                    
                    if term not in thisTicketUniques:
                        thisTicketUniques[term] = 1.0
                    if term not in uniqueWords:
                        uniqueWords[term] = 1
                    
                for term in thisTicketUniques:
                    if term not in edgeContentFreq[edge]:
                        edgeContentFreq[edge][term] = 1.0
                    else:
                        edgeContentFreq[edge][term] += 1.0                      
                        
            totalFreqForThisEdge = 0
            for term in edgeContentFreq[edge]:
                totalFreqForThisEdge += edgeContentFreq[edge][term]
                
            for term in edgeContentFreq[edge]:                
                edgeContentProb[edge][term] = edgeContentFreq[edge][term]/totalFreqForThisEdge  
                  
        return (edgeContentProb, len(uniqueWords))   

    
    def calculateProbabilityMatrix(self, groupProb, edgeWordProb, test_text, totalNumberOfUniqueWords):       
        """Based on equation 15 in KDD'10"""        
        lamb = 0.9
        allWords = test_text.split()
        
        """0. Extract a list of unique words in given test ticket"""
        words = {}
        for term in allWords:
            if term not in words:
                words[term] = 1
                                
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
                
            signOfThisEdge = 0
            if (sigRowSum[edge] - 0.01) == 0:
                signOfThisEdge = 0
            elif (sigRowSum[edge] - 0.01) > 0:
                signOfThisEdge = 1
            else:
                signOfThisEdge = -1
                                
            sigRowSum[edge] = (signOfThisEdge + 1.0)/2.0
        
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
            if denumerator[init] > 0:             
                finalProbabilityMatrix[edge] =  probOfTicketPerEdge[edge]/denumerator[init]
        
        return finalProbabilityMatrix
                             
    
    def greedyTransfer(self, comprehensiveGroups, resolver, initial, probabilityMatrix):
        
        includedSet = [initial]
        
        """Since we are using actual group in our resolver, this limit is set to 11"""
        while len(includedSet) <= 11: 
            
            leftOverGroups = self.delta(comprehensiveGroups,includedSet)
            s_tProbs = {}
            for source in includedSet:
                for target in leftOverGroups:
                    edge = (source, target)
                    if edge in probabilityMatrix:
                        s_tProbs[edge] = probabilityMatrix[edge]
            
            if len(s_tProbs) == 0:
                includedSet = {}
                return includedSet
            
            maxPair = max(s_tProbs.iteritems(), key=operator.itemgetter(1))[0]
            includedSet.append(maxPair[1])
            
            if maxPair[1] ==  resolver:         
                break               
                             
        return includedSet
        
        
    def delta(self, comprehensiveGroups,includedSet):   
        newList = []
        for x in comprehensiveGroups:
            if x not in includedSet:
                newList.append(x)
        
        return newList
