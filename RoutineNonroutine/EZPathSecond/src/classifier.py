'''
Created on Jul 2, 2015

@author: mohark1
'''

import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import PassiveAggressiveClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report
from sklearn.metrics import confusion_matrix
from sklearn.externals import joblib
from helperFunctions import sizeOf

def topLevelClassifier (first_train, first_test, DirToDumpModels):

    
    #Printing the size of the sets in MB.
    #sizeOf (first_train[0],first_test[0])
    #sizeOf (second_train[0],second_test[0])
    
    
    n_samples= first_train[2].size
    print "number of training samples: "+ str(n_samples)
    #print n_features
   
    '''
    #feature extraction:
    TfidfVectorizer()
    vectorizer = TfidfVectorizer(min_df=1)
    X_train = vectorizer.fit_transform(first_train[0])
    
    y_train = first_train[2]
    #y_train = np.array([int(el=="R") for el in first_train[2]])
    #training
   
    clf = MultinomialNB(alpha=0.1)
    clf.fit(X_train, y_train)
    
    X_test = vectorizer.transform(first_test[0])
    y_test = first_test[2]
    
    print("NB Test score:")
    print clf.score(X_test, y_test)

    '''

    NBpipeline = Pipeline((
    ('vec', TfidfVectorizer(min_df=1)),
    ('clf', MultinomialNB(alpha=0.1)),))
    
    NBpipeline.fit(first_train[0], first_train[2])
    

    print("NB Test score:")
    print(NBpipeline.score(first_test[0], first_test[2]))
    #store the model
    
    
    predicted = NBpipeline.predict(first_test[0])
    print(classification_report(first_test[2], predicted,))
    #confusion_matrix(first_test[2], predicted)
    joblib.dump(NBpipeline, DirToDumpModels+'\\NBmodel_first.pkl')
    
    
    PApipeline = Pipeline((
    ('vec', TfidfVectorizer(min_df=1, max_df=0.8, use_idf=True)),
    ('clf', PassiveAggressiveClassifier(C=1)),))
    
    
    PApipeline.fit(first_train[0], first_train[2])
    

    print("\nPA Test score:")
    print(PApipeline.score(first_test[0], first_test[2]))
    joblib.dump(PApipeline, DirToDumpModels+'\\PAmodel_first.pkl')
    
    return

def secondLevelClassifier(second_train, second_test, DirToDumpModels, prediction_csvdump):
    
    n_samples= second_train[2].size
    print "number of training samples: "+ str(n_samples)
    #print n_features
    
    NBpipeline = Pipeline((
    ('vec', TfidfVectorizer(min_df=1)),
    ('clf', MultinomialNB(alpha=0.1)),))
    
    NBpipeline.fit(second_train[0], second_train[1])
    

    print("NB Test score:")
    print(NBpipeline.score(second_test[0], second_test[1]))
    #store the model
    
    predicted = NBpipeline.predict(second_test[0])
    print(classification_report(second_test[1], predicted,))
    #confusion_matrix(first_test[2], predicted)
    
    writeToCSV("NB", prediction_csvdump, second_test[0], second_test[1], predicted)
    
    
    
    joblib.dump(NBpipeline, DirToDumpModels+'\\NBmodel_second.pkl')
    
    
    
    PApipeline = Pipeline((
    ('vec', TfidfVectorizer(min_df=1, max_df=0.8, use_idf=True)),
    ('clf', PassiveAggressiveClassifier(C=1)),))
    
    
    PApipeline.fit(second_train[0], second_train[1])
    

    print("\nPA Test score:")
    print(PApipeline.score(second_test[0], second_test[1]))
    
    
    predicted2 = PApipeline.predict(second_test[0])
    print(classification_report(second_test[1], predicted2,))
    writeToCSV("PA", prediction_csvdump, second_test[0], second_test[1], predicted2)
    
    joblib.dump(PApipeline, DirToDumpModels+'\\PAmodel_second.pkl')
    
    return

def writeToCSV(modelName, OSpath, ticket_text, actual_path, predicted_Path):
    import csv
    c = csv.writer(open(OSpath+modelName+".csv", "wb"))    
    c.writerow(["Description","Actual Path", "PredictedPath"])
    
    for i in range(len(predicted_Path)):
        c.writerow([ticket_text[i],actual_path[i], predicted_Path[i]]) 
    
    return
    
     