'''
Created on Feb 16, 2014

@author: Kayhan
'''

import os
import math
import operator
import viterbi

class hiddenMarkov:
    words = list(list())
    tags = list(list())
    featureTables = list()
    tagCounts = {}
    wordCounts = {}
    TotalTagCounts = 0
    tagLocationfinder = {}
    

    tagSeq= {}
    probTagSeq = {}

    def __init__(self):
        filename = os.path.join(os.path.dirname(__file__), 'files/wsj-train.txt')
        fob = open (filename,'r')
        allLines = fob.readlines()
        fob.close()
        
        i=0
        while i< len(allLines):
            allLines[i] = allLines[i].replace('\n','') 
            tokens = allLines[i].split();
            
            j=0
            wordsLineList = list()
            tagsLineList = list()
            while j<len(tokens):
                if j%2 ==0 :
                    wordsLineList.append(tokens[j])   
                    
                    wordlowered = tokens[j].lower()
                    if wordlowered in self.wordCounts:
                        self.wordCounts[wordlowered] += 1
                    
                    else:
                        self.wordCounts[wordlowered] = 1
                    
                else:
                    tagsLineList.append(tokens[j])
                    
                    
                    '''Sequence Tags with respect to previous tag'''
                    current = tokens[j]
                    if j>1:
                        previous = tokens[j-2]
                    else:
                        previous = "<S>"
                    
                    if previous in self.tagSeq:
                        if current in self.tagSeq[previous]:
                            self.tagSeq[previous][current]+=1
                        else:
                            self.tagSeq[previous][current]=1
                    else:
                        self.tagSeq[previous] =  {current:1}
                    
                    '''Sequence Tags done'''
                    
                    
                    if tokens[j] in self.tagCounts:
                        self.tagCounts[tokens[j]] += 1
                    
                    else:
                        self.tagCounts[tokens[j]] = 1
                    
                j+=1
                
            self.words.append(wordsLineList)
            self.tags.append(tagsLineList)
            i+=1 
            
        self.tagCounts["<S>"] = len(allLines)
        self.probTagSeqCalc()
            
            
    def probTagSeqCalc (self):
        
        for entry in self.tagSeq:
            Sum = 0
            for tag in self.tagSeq[entry]:
                Sum += self.tagSeq[entry][tag]
            
            for tag in self.tagSeq[entry]:
                prob = float(self.tagSeq[entry][tag]+1) / float(Sum+len(self.tagSeq)) 
                if entry in self.probTagSeq:
                    self.probTagSeq[entry][tag] = math.log(prob)
                else:
                    self.probTagSeq[entry] = {tag:math.log(prob)}
        
        '''smoothing''' 
        smoothed = 0            
        for tag in self.tagSeq:
            for tag2 in self.tagSeq:
                if (tag not in self.probTagSeq):
                    smoothed = math.log(float(1)/float((len(self.tagSeq)+self.tagCounts[tag] )) )
                    self.probTagSeq[tag] = {[tag2]:smoothed}
                else:
                    if (tag2 not in self.probTagSeq[tag]):
                        smoothed = math.log(float(1)/float((len(self.tagSeq)+self.tagCounts[tag] )) )
                        self.probTagSeq[tag][tag2] = smoothed
  
                
                    
                    
            
                
            
    def wordSelf(self, token):
        return token.lower()
    
    def isNumeric(self,token):
        return any([ch.isdigit() for ch in token])
    
    def isUppercase(self, token):
        return token.isupper()
    
    def isInitialUpper (self, token):
        return token.istitle()
    
    def isMixed(self, token):
        return (any([ch.isalnum() for ch in token]) and any([not ch.isalnum() for ch in token]))
    
    
  
    
            
    def tagTableConstructor (self):
        i=0
        tagWordDic={}
        tagNumDic={}
        tagUpDic = {}
        tagInitDic={}
        tagMixedDic={}
        
        while i< len(self.tags):
            j=0
            while j<len(self.tags[i]):
                self.TotalTagCounts+=1
                tokentag = self.tags[i][j]
                token = self.words[i][j]
                
                wordtok = self.wordSelf(token)
                numtok = str(self.isNumeric(token))
                uptok = str(self.isUppercase(token))
                initok = str(self.isInitialUpper(token))
                mixtok = str(self.isMixed(token))
                
                ''' First Feature'''
                if tokentag in tagWordDic:
                    if wordtok in tagWordDic[tokentag]:
                        tagWordDic[tokentag][wordtok] +=1
                    else:
                        tagWordDic[tokentag][wordtok] =1
                        
                else  :
                    tagWordDic[tokentag] = {wordtok:1}    
                
                ''' Second Feature'''
                    
                if tokentag in tagNumDic:
                    if numtok in tagNumDic[tokentag]:
                        tagNumDic[tokentag][numtok] +=1
                    else:
                        tagNumDic[tokentag][numtok] =1
                        
                else  :
                    tagNumDic[tokentag] = {numtok:1}
                      
                ''' Third Feature'''
                
                if tokentag in tagUpDic:
                    if uptok in tagUpDic[tokentag]:
                        tagUpDic[tokentag][uptok] +=1
                    else:
                        tagUpDic[tokentag][uptok] =1
                        
                else  :
                    tagUpDic[tokentag] = {uptok:1}
                
                ''' Forth Feature'''
                    
                if tokentag in tagInitDic:
                    if initok in tagInitDic[tokentag]:
                        tagInitDic[tokentag][initok] +=1
                    else:
                        tagInitDic[tokentag][initok] =1
                        
                else  :
                    tagInitDic[tokentag] = {initok:1}
                
                ''' Fifth Feature'''
                    
                if tokentag in tagMixedDic:
                    if mixtok in tagMixedDic[tokentag]:
                        tagMixedDic[tokentag][mixtok] +=1
                    else:
                        tagMixedDic[tokentag][mixtok] =1
                        
                else  :
                    tagMixedDic[tokentag] = {mixtok:1}
                    
             
                
                

                j+=1
            i+=1
            

        self.featureTables.append(tagWordDic)
        self.featureTables.append(tagNumDic)
        self.featureTables.append(tagUpDic)
        self.featureTables.append(tagInitDic)
        self.featureTables.append(tagMixedDic)
   
   
        
    def LogProbCalc(self,  tokentag, fIndex, fValue):
        
        currentFeatureTable = self.featureTables[fIndex]
        
        total = 0
        
        '''smothing'''
        if fIndex!=0:
            total = self.tagCounts[tokentag] + 2
        else:
            total = self.tagCounts[tokentag] + len(self.wordCounts)
        
        '''Counts'''
        fCount =0
        if tokentag in currentFeatureTable:
            if fValue in currentFeatureTable[tokentag]:
                fCount = currentFeatureTable[tokentag][fValue] + 1
            
            else:
                fCount = 1
        else:
            fCount = 1     
        return math.log(float(fCount)/float(total))
    
    def locationFinder(self):
        sorted_x = sorted(self.tagCounts.iteritems(), key=operator.itemgetter(1), reverse = True) 
        loc = 0
        for i in sorted_x:
            self.tagLocationfinder[i[0]] = loc
            loc+=1

    def testing (self):
        
        filename = os.path.join(os.path.dirname(__file__), 'files/wsj-test.txt')
        fob = open (filename,'r')
        allLines = fob.readlines()
        fob.close()
        
        words = list(list())
        tags = list(list())
        i=0
        while i< len(allLines):
            allLines[i] = allLines[i].replace('\n','') 
            tokens = allLines[i].split();
            
            j=0
            wordsLineList = list()
            tagsLineList = list()
            while j<len(tokens):
                if j%2 ==0 :
                    wordsLineList.append(tokens[j])   
                    
                else:
                    tagsLineList.append(tokens[j])
                j+=1
                
            words.append(wordsLineList)
            tags.append(tagsLineList)
            i+=1
            
        
        '''  Words and Tags are extracted from the Test file   '''    
        self.locationFinder()  
        i=0
        total = 0
        correctCount =0
        unk = 0
        unkcorr = 0
        matrixSize= len(self.tagLocationfinder)
        confusionMatrix = [[0 for x in range(matrixSize)] for x in range(matrixSize)] 
    
        maxMissClassifiedprint = 0
        misClassfiedSofar = 0
        while i< len(words):
            ''' p(W|T) fpr all tags and words in this sentence'''
            tagWordprob = {}
            wordline = []
            
            j=0
            while j<len(words[i]):
                
                
                token = words[i][j]
                
                wordtok = self.wordSelf(token)
                numtok = str(self.isNumeric(token))
                uptok = str(self.isUppercase(token))
                initok = str(self.isInitialUpper(token))
                mixtok = str(self.isMixed(token))
                
                currentWord = token.lower() 
                
                for tokentag in self.tagCounts:
                    
                    #tagLogProb = math.log(float(self.tagCounts[tokentag])/float(self.TotalTagCounts))
        
                    ''' Lookup the tag and word features''' 
                    logf1 = self.LogProbCalc(tokentag,0, wordtok)
                    logf2 = self.LogProbCalc(tokentag, 1, numtok)
                    logf3 = self.LogProbCalc(tokentag, 2, uptok)
                    logf4 = self.LogProbCalc(tokentag, 3, initok)
                    logf5 = self.LogProbCalc(tokentag, 4, mixtok)
                    
                    estimator = (logf1+logf2+logf3+logf4+logf5)
                    
                    
                    if tokentag in tagWordprob:
                        if currentWord in tagWordprob[tokentag]:
                            tagWordprob[tokentag][currentWord] = estimator
                        else:
                            tagWordprob[tokentag][currentWord] = estimator
                    else:
                        tagWordprob[tokentag] = {currentWord:estimator}
                            
                
            
                    
                wordline.append(currentWord)  
                j+=1
                
            v = viterbi.viterbi(self.probTagSeq, tagWordprob, wordline)
            tagSet = v.return_max()
            #print tagSet
            
            j=0

            while j<len(words[i]):
                actualtokentag = tags[i][j]                
                predictedtag = tagSet[j]
                wordtok = self.wordSelf(words[i][j]) 
                
                confusionMatrix[self.tagLocationfinder[actualtokentag]][self.tagLocationfinder[predictedtag]]+=1
                
                if (actualtokentag == predictedtag):
                    correctCount+=1
                    if wordtok not in self.wordCounts:
                        unkcorr +=1
                        
                else:
                
                    if misClassfiedSofar<maxMissClassifiedprint:
                        print str(misClassfiedSofar+1)+". "+wordtok+" Proposed: "+predictedtag +" Actual: "+actualtokentag
                        misClassfiedSofar+=1
                        
                if wordtok not in self.wordCounts:
                    unk+=1
                    
                total+=1    
                j+=1
                
            i+=1
        
        accuracy = correctCount*100/float(total)
        unkAccur = unkcorr*100/float(unk)
        self.printConfusionMatrix (confusionMatrix) 
        print "\t Total "+str(total)+ " right "+ str(correctCount)+" accuracy is %0.2f" %accuracy+"%"
        print "\t Unk total "+str(unk)+" right "+ str(unkcorr)+ " accuracy is %0.2f" %unkAccur+"%"
        print "\t confusionMatrixHMM.csv file generated\n"
    
    def printConfusionMatrix (self, M):
        sorted_tags = sorted(self.tagCounts.iteritems(), key=operator.itemgetter(1), reverse = True) 
        f = open('confusionMatrixHMM.csv','w')
        f.write('Actual\Proposed,') 
        for tag in sorted_tags:
            if (tag[0]==','):
                f.write('",",')
            else:
                f.write(tag[0]+',')
        f.write('\n')
        
        i=0
        for actag in sorted_tags:
            if (actag[0]==','):
                f.write('",",')
            else:
                f.write(actag[0]+',')
            j=0
            for postag in sorted_tags:
                f.write(str(M[i][j])+',')
                j+=1
            f.write('\n')
            i+=1
        f.close()