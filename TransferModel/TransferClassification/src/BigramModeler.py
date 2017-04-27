'''
Created on Aug 15, 2014

@author: US
'''

#import operator
import math
class languageModel:
    '''
    classdocs
    '''

    incidentDescriptions = {}
    universalDic = {}
    bigramDic = {}
    def __init__(self, inc_description):
        '''
        Constructor
        '''
        
        self.incidentDescriptions = inc_description
        self.dictionaryBuild()
        
    def dictionaryBuild(self):
        
        
        #Words dictionary
        for inc in self.incidentDescriptions:
            text = self.incidentDescriptions[inc]
            textList = text.split()
            for word in textList:
                if word in self.universalDic:
                    self.universalDic[word]+= 1.00
                else:
                    self.universalDic[word] = 1.00
        
        #sorted_Universal_Dic = sorted(universalDic.iteritems(), key=operator.itemgetter(1), reverse=True)
        #return sorted_Universal_Dic
        
        #Bigram Dictionary
        for inc in self.incidentDescriptions:
            text = self.incidentDescriptions[inc]
            textList = text.split()
            index = 0
            
            while index<len(textList):
                current_word = textList[index]
                if index+1 < len(textList):
                    word_ahead = textList[index+1]
                else: 
                    word_ahead = "#</s>"
                if current_word in self.bigramDic:
                    if word_ahead in self.bigramDic[current_word]:
                        self.bigramDic[current_word][word_ahead] +=1.00
                    else:
                        self.bigramDic[current_word][word_ahead] = 1.00
                        
            
                else:
                    self.bigramDic[current_word] = {word_ahead:1.00}    
                
                index+=1
        
    def RoutinenessIndexCalc(self):
        routinenessOfContent = {}
        for inc in self.incidentDescriptions:
            text = self.incidentDescriptions[inc]
            textList = text.split()
            index = 0
            SumOfLogs = 0.00 
            
            while index<len(textList):
                current_word = textList[index]
                if index+1 < len(textList):
                    word_ahead = textList[index+1]
                else: 
                    word_ahead = "#</s>"
                    
                SumOfLogs+=self.logProbability(word_ahead, current_word)
                
                
                index +=1
            
            if index !=0:  
                routinenessOfContent[inc] = SumOfLogs/float(index)
            
            
        return routinenessOfContent    
                
    def logProbability(self, word_ahead, current_word):
        Lambda = 1.00
        numerator = self.bigramDic[current_word][word_ahead] + Lambda
        denominator = self.universalDic[current_word] + Lambda*float(len(self.universalDic))    
        return math.log(numerator/denominator)
        
            