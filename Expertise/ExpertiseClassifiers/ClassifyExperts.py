'''
Created on Jan 6, 2017

@author: sobhan
'''

from sklearn.model_selection import cross_val_score
from sklearn.datasets import make_blobs
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import ExtraTreesClassifier
# from sklearn.tree import DecisionTreeClassifier
from sklearn import svm, linear_model
from sklearn.neural_network import MLPClassifier
from sklearn.neighbors import KNeighborsClassifier
# from sklearn.ensemble import AdaBoostClassifier
# from sklearn.linear_model import LinearRegression
from os import listdir

def loadData(path, expert):    
    X = []
    Y = []
    with open(path + expert, "r") as f:
        for line in f:             
            parts = line.split('\t')[0].split(',')
            vec = []
            for p in parts:
                vec.append(float(p))
            X.append(vec)
            y = line.split('\t')[1].split('\n')[0]
            Y.append(int(y))
    return X, Y

if __name__ == '__main__':
    path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/For Scikit/UniGram/"
    f = open("C:/Users/sobhan/Desktop/Kayhan's Project/Analysis/Classifiers/" + "NeuralNet_Results_adam.csv", 'w')
    f.write("Expert,Fold_1,Fold_2,Fold_3,Fold_4,Fold_5,Fold_6,Fold_7,Fold_8,Fold_9,Fold_10,F-Measure\n")
    experts = listdir(path)
    for exp in experts:
        [X, Y] = loadData(path, exp)
#         clf = RandomForestClassifier(n_estimators=220, max_depth=None, min_samples_split=2, random_state=0, n_jobs=4)
#         clf = ExtraTreesClassifier(n_estimators=50, max_depth=None, min_samples_split=2, random_state=0, n_jobs=4)        
#         clf = KNeighborsClassifier(n_neighbors=10, n_jobs=4)
        clf = MLPClassifier(activation='logistic', solver='adam', random_state=1, hidden_layer_sizes=(500, 200))
        scores = cross_val_score(clf, X, Y, cv=10, scoring='f1_macro')        
        listOfScores = ""
        for s in scores:
            listOfScores = (listOfScores + str(s) + ",")
        f.write(exp.replace(".txt", "") + "," + listOfScores + str(scores.mean()) + "\n")
        print ("**Neural Net** " + exp.replace(".txt", "") + ": " + str(scores.mean()) + " --> " + listOfScores)
    f.close()
    