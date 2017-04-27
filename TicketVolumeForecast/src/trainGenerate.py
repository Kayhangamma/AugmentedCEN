'''
Created on Dec 15, 2015

@author: mohark1
'''

import numpy as np

from sklearn import linear_model
from collections import deque

import os
import csv


def printToFile(listOfTuples, fileName, outputPath):
    
    if not os.path.exists(outputPath):
        os.makedirs(outputPath)
    
    c = csv.writer(open(outputPath+fileName,"wb"))
    for el in listOfTuples:
        c.writerow(list(el))





def convertToFloatList(mytuple):
    x = [float(el) for el in mytuple]
    return x

class Train:

    models = []
    
    def __init__(self, matrixSet, modelConfig, modelOutput_Path):
        self.matrixSet = matrixSet
        self.modelConfig = modelConfig
        self.modelOutput_Path = modelOutput_Path
        
    
    
    def learn(self, features, target):
        
        # Create linear regression object
        regr = linear_model.LinearRegression()
        # Train the model using the training sets
        regr.fit(features, target)
        R2 = 100*regr.score(features, target)
        
        
        weights = [regr.intercept_]
        coeficients = regr.coef_.tolist()
        
        weights.extend(coeficients)
        
        weights_queue = deque(weights)
        
        return (R2, weights_queue, regr)
    
    def runTraining(self):
        
        listOfTuples = [('Model ID', 'Window size', 'DNS?', 'Prov', 'Last Year?', 'COD(R^2)', 'W6','W5','W4','W3','W2','W1','W0')]
                
        
        
        
        #For one model
        index = 0
        for matrix in self.matrixSet:
            index +=1
            cur_row = []
            cur_row.append("model "+str(index))
            cur_row.extend(list(self.modelConfig[index]))
            
            featureArray = np.asarray ( [ convertToFloatList(row[2:]) for row in matrix[1:]] )
            targetArray = np.asarray([float(row[1]) for row in matrix[1:]])
            
            (R2, weights_queue, regr) = self.learn (featureArray, targetArray)
            self.models.append(regr)
            
            cur_row.append("%.2f" % R2)
            
            padded_weights = []
            for k in range(self.modelConfig[index][0]+1):
                padded_weights.append(weights_queue.popleft())

            if len(padded_weights) == 2:
                padded_weights.extend([0,0])
            elif len(padded_weights) == 3:
                padded_weights.append(0)
            else:
                pass
            
            if (self.modelConfig[index][3] == 0):
                padded_weights.append(0)
            else:
                padded_weights.append(weights_queue.popleft())
                
            if (self.modelConfig[index][1] == 0):
                padded_weights.append(0)
            else:
                padded_weights.append(weights_queue.popleft())
                
            if (self.modelConfig[index][2] == 0):
                padded_weights.append(0)
            else:
                padded_weights.append(weights_queue.popleft()) 
                
            
            rpw = padded_weights[::-1]
            
            cur_row.extend(rpw)
            cur_row_tup = tuple(cur_row)
            
            listOfTuples.append(cur_row_tup)
        
        printToFile(listOfTuples, "modelsDetails.csv", self.modelOutput_Path)
        print "Step 3: Models are constructed and summarized in file. Model fitted on the data."
    
    def predictNext(self, vectorSet, month_to_predict):
        index = 0
        listOfTuples = [('Model ID', 'Window size', 'DNS?', 'Prov', 'Last Year?','MonthToPredict' ,'Predicted_Value')]
        sum = 0.000
        for vec_el in vectorSet:
            predictable = np.asarray ([convertToFloatList(vec_el)])
            
            predictedValue = self.models[index].predict(predictable).tolist()[0]
            sum+= predictedValue
            
            
            cur_row = []
            cur_row.append("model "+str(index+1))
            cur_row.extend(list(self.modelConfig[index+1]))
            cur_row.append(month_to_predict)
            cur_row.append(predictedValue)
            listOfTuples.append(tuple(cur_row))
            index +=1
            
        
        printToFile(listOfTuples, "predictions.csv", self.modelOutput_Path)
        print "Step 4: All models predicted the next month and predictions are stored in 'predictions.csv' . "
        print "Predicting...\nEnsembled prediction value based on "+ str(len(listOfTuples)-1)+" models for " +month_to_predict+ " is: "+"%.2f" % round(sum/float(index),2)
    
