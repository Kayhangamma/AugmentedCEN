'''
Modified on Jul 6, 2015

@author: mohark1
'''

import operator
import numpy as np

class prep:
    positions_toID = {}
    workingSet = []
    first_testper = 0
    
    def __init__(self, tf, trainDataset):
        
        self.first_testper = tf
        self.workingSet = trainDataset
        for i in range(len(self.workingSet)):
            self.positions_toID[i] = self.workingSet[i][0]
        
        #Remove ticketID from the working set
        for triple in self.workingSet:
            del triple[0]
        
            
    
    def labelPaths(self, Routine_k):
        pathfreq = {}
        for triple in self.workingSet:
            if triple[1] not in pathfreq:
                pathfreq[triple[1]] = 1
            else:
                pathfreq[triple[1]] += 1
        
        sorted_pathfreq = sorted(pathfreq.items(), key=operator.itemgetter(1), reverse=True)
        pathRNRLabels = {}
        
        #Labeling top 'Routine_k' frequent paths as R and others as NR  
        for i in range(len(sorted_pathfreq)):
            if i<Routine_k:
                pathRNRLabels[sorted_pathfreq[i][0]] = 'R'
            else:
                pathRNRLabels[sorted_pathfreq[i][0]] = 'NR'
        
        countOfRoutines = 0
        for triple in self.workingSet:
            triple.append(pathRNRLabels[triple[1]])
            if triple[2] == "R":
                countOfRoutines +=1
            
        routinePercent = 100.00 *float(countOfRoutines)/len(self.workingSet)
        print '%0.2f' % routinePercent + "% of the tickets are considered routine" 
        
        count_trainable_First = int((1.00-self.first_testper)*len(self.workingSet))
               
        text_data_first_train_list = []
        text_data_first_test_list = []
        path_target_first_train_list = []
        path_target_first_test_list = []
        rtype_target_first_train_list = []
        rtype_target_first_test_list = []
        
        for i in range(len(self.workingSet)):
            if i<count_trainable_First:
                text_data_first_train_list.append(self.workingSet[i][0])
                path_target_first_train_list.append(self.workingSet[i][1])
                rtype_target_first_train_list.append(self.workingSet[i][2])
                
            else:
                text_data_first_test_list.append(self.workingSet[i][0])
                path_target_first_test_list.append(self.workingSet[i][1])
                rtype_target_first_test_list.append(self.workingSet[i][2])
        
        
        
        text_data_first_train = np.array(text_data_first_train_list, dtype=unicode)
    
        text_data_first_test = np.array(text_data_first_test_list, dtype=unicode)
        
        path_target_first_train = np.array(path_target_first_train_list, dtype=unicode) 
        path_target_first_test = np.array(path_target_first_test_list, dtype=unicode)
        
         
        rtype_target_first_train = np.array(rtype_target_first_train_list, dtype=unicode) 
        rtype_target_first_test = np.array(rtype_target_first_test_list, dtype=unicode)
        
        
        #Generating the train and test set of the top level classifier
        
        first_train = (text_data_first_train, path_target_first_train, rtype_target_first_train)
        first_test = (text_data_first_test, path_target_first_test, rtype_target_first_test)
        
        
        
        
        
        
        

        
        return (first_train, first_test)

            
        
        