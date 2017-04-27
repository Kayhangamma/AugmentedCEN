'''
Created on Aug 18, 2014

@author: US
'''
import nltk

class classifier:
    '''
    classdocs
    '''
    trainSet = {}

    def __init__(self, supervisedSet):
        
        (incident_Description_purified, routinenessPathDic, PathThreshold) = supervisedSet
        for inc in routinenessPathDic:
            if routinenessPathDic[inc] >= PathThreshold :
                self.trainSet[inc] = (incident_Description_purified [inc], "Routine")
            else:
                self.trainSet[inc] = (incident_Description_purified [inc], "Nonroutine") 
            
        