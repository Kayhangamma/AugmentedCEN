'''
Modified on Jul 2, 2015

@author: mohark1
'''

from fileIO import readwrite
from preprocessing import prep
from classifier import secondLevelClassifier





#CSV file path on disk

#This should be replaced by the database connection later.
#Need auto-construct the path field from the dataset 

trainFilePath = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\AllInOne_short.csv"

DirToDumpModelsSecond = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\scikitModels\\Second"

prediction_csvdump = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\scikitModels\\Second\\csv\\"

#Top k paths are assigned routine label.
Routine_k = 30
#Bottom s% for testing
#First level is for Routine/Non-Routine classification

#Second level is for routine path classification

test_second_level_split = 0.20

def main ():
    rw = readwrite()
    
    trainDataset = rw.readcsv(trainFilePath)
    
    prepros_object = prep(test_second_level_split,trainDataset)
    (second_train, second_test) = prepros_object.labelPaths(Routine_k)
    #each of the above four are comprised of a triple: (data, path, routine/non-routine)
    

    
    
    #Build the model and store it on disk
    secondLevelClassifier(second_train, second_test, DirToDumpModelsSecond, prediction_csvdump)
    
    
    
    
    

if __name__ == '__main__':
    main()