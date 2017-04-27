'''
Modified on Jul 2, 2015

@author: mohark1
'''

from fileIO import readwrite
from preprocessing import prep
from classifier import topLevelClassifier, secondLevelClassifier





#CSV file path on disk

#This should be replaced by the database connection later.
#Need auto-construct the path field from the dataset 

trainFilePath = "C:\\Kayhan\NWWork\\Work-In-Progress\\Data\\transGroup\\research\\weekly-2015\\incident-details\\AllInOne.csv" 
DirToDumpModelsTop = "C:\\Kayhan\NWWork\\Work-In-Progress\\Data\\transGroup\\research\\weekly-2015\\incident-details\\scikitModels\\Top"
DirToDumpModelsSecond = "C:\\Kayhan\NWWork\\Work-In-Progress\\Data\\transGroup\\research\\weekly-2015\\incident-details\\scikitModels\\Second"
#Top k paths are assigned routine label.
Routine_k = 30
#Bottom s% for testing
#First level is for Routine/Non-Routine classification
test_first_level_split = 0.2
#Second level is for routine path classification
test_second_level_split = 0.2 

def main ():
    rw = readwrite()
    trainDataset = rw.readcsv(trainFilePath)
    prepros_object = prep(test_first_level_split,test_second_level_split,trainDataset)
    (first_train, first_test, second_train, second_test) = prepros_object.labelPaths(Routine_k)
    #each of the above four are comprised of a triple: (data, path, routine/non-routine)
    

    
    
    #Build the model and store it on disk
    topLevelClassifier(first_train, first_test, DirToDumpModelsTop)
    secondLevelClassifier(second_train, second_test, DirToDumpModelsSecond)
    
    
    
    
    

if __name__ == '__main__':
    main()