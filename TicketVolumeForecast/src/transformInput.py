'''
Created on Dec 15, 2015

@author: mohark1
'''

import csv
import os

def printToFile(listOfTuples, i, outputPath):
    fileName = "Model_"+str(i)+"_data.csv"
    
    if not os.path.exists(outputPath):
        os.makedirs(outputPath)
    
    c = csv.writer(open(outputPath+fileName,"wb"))
    for el in listOfTuples:
        c.writerow(list(el))


class Transform:
   

    def __init__(self, raw_data, modelConfig, modelheadings, outputP):
        self.data = raw_data
        self.modelConfig = modelConfig
        self.modelheadings = modelheadings
        self.outputPath = outputP

        
    
    def runForModels (self):
        
    
        #Creating the data matrix for each model above. Input data.
        matrix = []
        vector = []
        
        current_i = 0
        for key in self.modelConfig:
            matrix.append([self.modelheadings[key]])
            
            for i in range(len(self.data)):
                lookBack = []
                for k in range(self.modelConfig[key][0]):
                    if i-(k+1)>=0 and self.data[i-(k+1)][0]!='':
                        lookBack.append(self.data[i-(k+1)][0])
                
                if len(lookBack)!=self.modelConfig[key][0]:
                    continue
                
                elif (self.modelConfig[key][1] == 1) and (self.data[i-1][1]==''):
                    continue
                elif (self.modelConfig[key][2] == 1) and (self.data[i-1][2]==''):
                    continue
                elif (self.modelConfig[key][3] == 1) and (self.data[i-12][0]=='' or i<12):
                    continue
                
                
                cur_row_data = []
                cur_row_data.append(self.data[i][3])
                cur_row_data.append(self.data[i][0])
                cur_row_data.extend(lookBack)
                
                if (self.modelConfig[key][3] == 1):
                    cur_row_data.append(self.data[i-12][0])
                
                if (self.modelConfig[key][1] == 1):
                    cur_row_data.append(self.data[i-1][1])
                
                if (self.modelConfig[key][2] == 1):
                    cur_row_data.append(self.data[i-1][2])
                    
                tup_data = tuple(cur_row_data)    
                matrix[current_i].append(tup_data)
            
            printToFile(matrix[current_i], current_i+1, self.outputPath)    
            current_i+=1
            
            #Set what needs to be predicted as vector
            cur_vec = []
            for wi in range(self.modelConfig[key][0]):
                cur_vec.append(self.data[-(wi+1)][0])
            if (self.modelConfig[key][3] == 1):
                cur_vec.append(self.data[-12][0])
            
            if (self.modelConfig[key][1] == 1):
                cur_vec.append(self.data[-1][1])
            
            if (self.modelConfig[key][2] == 1):
                cur_vec.append(self.data[-1][2])
            vector.append(tuple(cur_vec))
        
        print "Step 2: Data transformations for the models are performed."
        
        return (matrix, vector)

              
            