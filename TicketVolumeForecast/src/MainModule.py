'''
Created on Dec 14, 2015

@author: mohark1
'''

from readProcess import Reader
from transformInput import Transform
from trainGenerate import Train
import shutil
import os

import numpy as np
from sklearn import linear_model
from sklearn import datasets




def main():
    inputPath = ".\\files\\input.csv"
    
    transformedInput_Path = ".\\files\\Transformed_Input\\"    
    modelOutput_Path = ".\\files\\Model_Summary_Predictions\\"
    
    #Delete the folders if exists.
    if os.path.exists(transformedInput_Path):
        shutil.rmtree(transformedInput_Path)
        
    if os.path.exists(modelOutput_Path):
        shutil.rmtree(modelOutput_Path)
    
    
    
    
    # Reader deals with reading the input file
    R = Reader(inputPath)
    # errorHanding() reads the file and checks if the input file is correct. Also loads the raw_data matrix.
    raw_data, month_to_predict = R.errorHanding()  

    #Best models and their config:
    #1: Window 3 DNS 1 Prov 1 LastYear 1
    #2: Window 2 DNS 1 Prov 1 LastYear 1
    #3: Window 3 DNS 1 Prov 1 LastYear 0
    #4: Window 3 DNS 0 Prov 1 LastYear 1
    #5: Window 2 DNS 0 Prov 1 LastYear 1
    #6: Window 3 DNS 0 Prov 1 LastYear 0
    #7: Window 2 DNS 1 Prov 1 LastYear 0
    #8: Window 2 DNS 0 Prov 1 LastYear 0
    #9: Window 3 DNS 0 Prov 0 LastYear 0
    
    modelConfig = {1: (3,1,1,1), 2:(2,1,1,1), 3:(3,1,1,0), 4:(3,0,1,1), 5:(2,0,1,1), 6:(3,0,1,0), 7:(2,1,1,0), 8:(2,0,1,0), 9: (3,0,0,0)}
    modelheadings = {1: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-3", "FW_t-12","DNS_t-1","Serv_Prov"),
                     2: ("Month","FW_Now","FW_t-1","FW_t-2", "FW_t-12","DNS_t-1","Serv_Prov"),
                     3: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-3","DNS_t-1","Serv_Prov"),
                     4: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-3", "FW_t-12","Serv_Prov"),
                     5: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-12","Serv_Prov"),
                     6: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-3","Serv_Prov"),
                     7: ("Month","FW_Now","FW_t-1","FW_t-2","DNS_t-1","Serv_Prov"),
                     8: ("Month","FW_Now","FW_t-1","FW_t-2","Serv_Prov"),
                     9: ("Month","FW_Now","FW_t-1","FW_t-2","FW_t-3")
                    }
    
    T = Transform(raw_data, modelConfig,modelheadings,transformedInput_Path)
    #Construct the regression data matrix for each model and place it in a matrix set.
    matrixSet, vectorSet = T.runForModels()

    #Train the models and generate the model outputs in the modelOutput_Path 
    TG = Train(matrixSet, modelConfig, modelOutput_Path)
    TG.runTraining()
    TG.predictNext(vectorSet, month_to_predict)    
    os.system("pause")
    

if __name__ == '__main__':
    main()
