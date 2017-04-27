'''
Created on Aug 18, 2014

@author: US
'''
import nltk
import random
import operator
from nltk.classify import apply_features

class classifier:
    '''
    classdocs
    '''
    dataSet = {}
    dataDic = []
    word_features = []
    inputSet= []
    dicTopWordsCutOff = 1300
    crossValidationFolds = 5
    GapFromTop = 50
    def __init__(self, supervisedSet):
        
        (incident_Description_purified, incident_paths, routinenessPathDic, PathThreshold) = supervisedSet
        
        for inc in incident_Description_purified:
           
            wordSequenceList = incident_Description_purified [inc].split()
            if routinenessPathDic[tuple(incident_paths[inc])] >= PathThreshold :
            #guess = randint(1,2)
            #if guess>1:
                self.dataSet[inc] = (wordSequenceList, "Routine")
            else:
                self.dataSet[inc] = (wordSequenceList, "Nonroutine") 
        
        
        self.buildDictionary ()
        
    def buildDictionary (self):
        Dic = {}
        for inc in self.dataSet:
            textList = self.dataSet[inc][0]
            for word in textList:
                if word in Dic:
                    Dic[word]+= 1
                else:
                    Dic[word] = 1
                    
        
        sorted_Dic = sorted(Dic.iteritems(), key=operator.itemgetter(1), reverse=True)
        self.dataDic = [ word for (word,freq) in sorted_Dic]
        #print sorted_Dic
        print self.dicTopWordsCutOff
        print self.dataDic[self.GapFromTop:self.dicTopWordsCutOff+self.GapFromTop]

        
    def feature_extraction_classification(self):
    
        self.word_features = self.dataDic[self.GapFromTop:self.dicTopWordsCutOff+self.GapFromTop]
            
        
        #featuresets = []
        counter = 1
        #for (d,c) in self.dataSet.values():
            #print counter
            #featuresets.append((self.document_features(d), c))
            #counter +=1
               
        #featuresets = [(self.document_features(d), c) for (d,c) in self.dataSet.values()]
        
        self.inputSet = self.dataSet.values()
        
        
        random.shuffle(self.inputSet)
        
        DataSet_Size = float(len(self.dataSet))
        #self.crossValidation(DataSet_Size, featuresets)
        
        self.crossValidation(DataSet_Size)
        
        #threshold = int(DataSet_Size*(1-self.trainRatio))
        
        
        
        #train_set, test_set = featuresets[threshold:], featuresets[:threshold]
        #train_set, test_set = featuresets[100:], featuresets[:100]
        #classifier = nltk.NaiveBayesClassifier.train(train_set)
        
        
        #print(nltk.classify.accuracy(classifier, test_set))
           
    
    
    def crossValidation (self,DataSet_Size):
        chunkSize = int(DataSet_Size/float(self.crossValidationFolds))
        print "Results of "+str(self.crossValidationFolds)+"-fold cross-validation:"
        for i in range(self.crossValidationFolds) :
            if i == 0:
                
                #test_set = featuresets[chunkSize*i:chunkSize*(i+1)]
                #train_set = featuresets[chunkSize:]
                test_set = apply_features(self.document_features, self.inputSet[chunkSize*i:chunkSize*(i+1)] )
                train_set = apply_features(self.document_features, self.inputSet[chunkSize:])
                
            elif i>=1 and i<=self.crossValidationFolds -2:
                #test_set = featuresets[chunkSize*i:chunkSize*(i+1)]
                #train_set = featuresets[:chunkSize*i] + featuresets[chunkSize*(i+1):]
                test_set = apply_features(self.document_features, self.inputSet[chunkSize*i:chunkSize*(i+1)] )
                train_set = apply_features(self.document_features, self.inputSet[:chunkSize*i] + self.inputSet[chunkSize*(i+1):] )
                
                
            elif i == self.crossValidationFolds -1:
                #test_set = apply_featuresfeaturesets[chunkSize*i:]
                #train_set = featuresets[:chunkSize*i]
                test_set = apply_features(self.document_features, self.inputSet[chunkSize*i:] )
                train_set = apply_features(self.document_features, self.inputSet[:chunkSize*i] )
                
            
            
            print str(i+1) + ".Test set between "+str(i*chunkSize) +" and " +str((i+1)*chunkSize) + " :"         
            classifier = nltk.NaiveBayesClassifier.train(train_set)    
            print(nltk.classify.accuracy(classifier, test_set))
            
        classifier.show_most_informative_features(20)  
         
    def document_features(self,document): 
        document_words = set(document) 
        features = {}
        for word in self.word_features:
            features['%s' % word] = (word in document_words)
        return features    