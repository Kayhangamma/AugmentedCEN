'''
Created on Jul 2, 2015

@author: mohark1
'''

from sklearn.externals import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np
import sys
from datetime import datetime

DirOfTopModel = "C:\\Kayhan\NWWork\\Work-In-Progress\\Data\\transGroup\\research\\weekly-2015\\incident-details\\scikitModels\\Top\\NBmodel_first.pkl"
DirOfSecondModel = "C:\\Kayhan\NWWork\\Work-In-Progress\\Data\\transGroup\\research\\weekly-2015\\incident-details\\scikitModels\\Second\\NBmodel_second.pkl"

def main():
    
    print "\n*************************************"
    print "Welcome to E-ZPath version 1.0.1\nPlease follow the steps:\n1.Record the text of your ticket in a 'txt' file. \n2. Provide the directory path of your ticket:\n"

    dir_path = raw_input('When ready, provide the full path of your txt file here\n (Exp: C:\\test\\myticket.txt):\n\n')
    
    a = datetime.now()
    
    #dir_path = "C:\\Kayhan\\ticket2.txt"
    
    try:
        ticket_file = open(dir_path, 'r')
    except:
        sys.stderr.write("Oops! Wrong file or directory. Please try again.\n")
        exit(0)
    
     
    ticket_content = ticket_file.read()
    t_content = np.array([ticket_content], dtype=unicode)

    NB_pipeline_top = joblib.load(DirOfTopModel)
    if NB_pipeline_top.predict(t_content)[0] == 'NR':
        print "The ticket content provided is non-routine. Unfortunately no statistical evidence was found for prediction."
    else:
        print "The ticket content is routine. The system is generating the path recommendation ..."
        NB_pipeline_second = joblib.load(DirOfSecondModel)
        print "The predicted path is: "+str(NB_pipeline_second.predict(t_content)[0])
        print "Recommendation confidence:  %0.2f " %(100.00* max(NB_pipeline_second.predict_proba(t_content)[0])) +"%"
        b = datetime.now()
        c = b-a
        print "Total prediction execution time was " +str(c.seconds) + " seconds and %0.0f" %(c.microseconds/1000) + " milliseconds"
        
     
    
    
if __name__ == '__main__':
    main()
