'''
Created on Feb 17, 2014

@author: Kayhan
'''

import os
import operator

class CRF:
    words = list(list())
    tags = list(list())
    tagCounts = {}
    wordCounts = {}
    testTags = list()
    testWords = list()
    tagLocationfinder = {}
    

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
                    
                    if tokens[j] in self.tagCounts:
                        self.tagCounts[tokens[j]] += 1
                    
                    else:
                        self.tagCounts[tokens[j]] = 1
                    
                j+=1
                
            self.words.append(wordsLineList)
            self.tags.append(tagsLineList)
            i+=1 
            
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
    
    def trainTransformationFile(self, NumberOfFeatures):
        
        f = open('train'+str(NumberOfFeatures)+'.txt','w')
        i=0
        while i< len(self.tags):
            j=0
            while j<len(self.tags[i]): 
                
                tokentag = self.tags[i][j]
                token = self.words[i][j]
                
                wordtok = self.wordSelf(token)
                numtok = str(self.isNumeric(token))
                uptok = str(self.isUppercase(token))
                initok = str(self.isInitialUpper(token))
                mixtok = str(self.isMixed(token))
                
                if NumberOfFeatures ==1:
                    self.writetoFile(tokentag+" "+ wordtok,f)
                else:   
                    self.writetoFile(tokentag+" "+ wordtok+" "+ numtok+" "+uptok+" "+initok+" "+mixtok,f)
                
                j+=1
            self.emptyLineInFile (f);
            i+=1 
                
        f.close()
    
    def emptyLineInFile (self, f):
        f.write("\n")
        
    def writetoFile (self, string , f):
        features = string.split() 
        i=0
        LookUp = {2:"is_numeric",3:"is_upercase", 4:"is_title", 5:"is_mixed"}
        
        L = list()
        while i<len(features):
            
            if i == 0:
                if features[i] == "#":
                    features[i] = "HASHTAG"    
                L.append(features[i])
            elif i == 1:
                L.append("word_is_"+features[i])
            else:
                if features[i] == "True":
                    L.append(LookUp[i])
            i+=1
        
        j=0
        while j<len(L)-1 :
            f.write(L[j]+'\t')
            j+=1
        f.write(L[j])
        f.write("\n")
        
        
    def train (self, NumberOfFeatures):
        command = "./crfsuite.prog learn -a pa -m crf.cls train"+ str(NumberOfFeatures)+".txt"
        os.system(command)
        
    def testTransformationFile(self, NumberOfFeatures):
        
        filename = os.path.join(os.path.dirname(__file__), 'files/wsj-test.txt')
        fob = open (filename,'r')
        allLines = fob.readlines()
        fob.close()
        
        i=0
        words = list(list())
        tags = list(list())
        while i< len(allLines):
            allLines[i] = allLines[i].replace('\n','') 
            tokens = allLines[i].split();
            
            j=0
            wordsLineList = list()
            tagsLineList = list()
            while j<len(tokens):
                if j%2 ==0 :
                    wordsLineList.append(tokens[j])   
                    self.testWords.append(tokens[j])
                else:
                    tagsLineList.append(tokens[j])
                    if tokens[j] == "#":
                        tokens[j] = "HASHTAG"
                    self.testTags.append(tokens[j])
                    
                j+=1
            words.append(wordsLineList)
            tags.append(tagsLineList)
            i+=1
                
                
                        
        
        f = open('test'+str(NumberOfFeatures)+'.txt','w')
        i=0
        while i< len(tags):
            j=0
            while j<len(tags[i]): 
                
                tokentag = tags[i][j]
                token = words[i][j]
                
                wordtok = self.wordSelf(token)
                numtok = str(self.isNumeric(token))
                uptok = str(self.isUppercase(token))
                initok = str(self.isInitialUpper(token))
                mixtok = str(self.isMixed(token))
                
                if NumberOfFeatures ==1:
                    self.writetoFile(tokentag+" "+ wordtok, f)
                else:   
                    self.writetoFile(tokentag+" "+ wordtok+" "+ numtok+" "+uptok+" "+initok+" "+mixtok,f)
                
                j+=1
            self.emptyLineInFile (f);
            i+=1 
                
        f.close()
    
    def locationFinder(self):
        sorted_x = sorted(self.tagCounts.iteritems(), key=operator.itemgetter(1), reverse = True) 
        loc = 0
        for i in sorted_x:
            if i[0] == "#":
                self.tagLocationfinder["HASHTAG"] = loc
            else:
                self.tagLocationfinder[i[0]] = loc
            loc+=1
            
    def generatePrediction (self, NumberOfFeatures):
        command = "./crfsuite.prog tag -m crf.cls test"+str(NumberOfFeatures)+".txt > predictions"+str(NumberOfFeatures)+".txt"
        os.system(command)
            
    def test (self, NumberOfFeatures):

        predictedTags = list()
        filename = os.path.join(os.path.dirname(__file__), "predictions"+str(NumberOfFeatures)+".txt")
        with open(filename) as f:
            for line in f:
                if line!="\n":
                    predictedTags.append(line.split()[0])
        
        self.locationFinder()  
        i=0      
        total = 0
        correctCount =0  
        unk =0
        unkcorr =0
        maxMissClassifiedprint = 0
        misClassfiedSofar = 0
        matrixSize= len(self.tagLocationfinder)
        confusionMatrix = [[0 for x in range(matrixSize)] for x in range(matrixSize)] 
        for tag in predictedTags:
            wordtok = self.testWords[i].lower()
            
            confusionMatrix[self.tagLocationfinder[self.testTags[i]]][self.tagLocationfinder[tag]]+=1
            if self.testTags[i] == tag:
                correctCount+=1
                if wordtok not in self.wordCounts:
                    unkcorr +=1
            else:
                if misClassfiedSofar<maxMissClassifiedprint and NumberOfFeatures==5:
                    print str(misClassfiedSofar+1)+". "+self.testWords[i]+" Proposed: "+tag +" Actual: "+self.testTags[i]
                    misClassfiedSofar+=1
                
            if wordtok not in self.wordCounts:
                    unk+=1
                    

            total+= 1
            i+=1
        accuracy = correctCount*100/float(total)
        unkAccur = unkcorr*100/float(unk)
        if NumberOfFeatures==5:
            self.printConfusionMatrix (confusionMatrix) 
        print "\t Total "+str(total)+ " right "+ str(correctCount)+" accuracy is %0.2f" %accuracy+"%"  
        print "\t Unk total "+str(unk)+" right "+ str(unkcorr)+ " accuracy is %0.2f" %unkAccur+"%" 
        print ""      
        
        
    def printConfusionMatrix (self, M):
        sorted_tags = sorted(self.tagCounts.iteritems(), key=operator.itemgetter(1), reverse = True) 
        f = open('confusionMatrixCRF.csv','w')
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
        
    
            