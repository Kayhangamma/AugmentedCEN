from sklearn.model_selection import cross_val_score
from sklearn.datasets import make_blobs
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn import svm, linear_model
from sklearn.neural_network import MLPClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import AdaBoostClassifier
from sklearn.linear_model import LinearRegression
from os import listdir

def listFiles():
    path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/For Scikit/"
    files = listdir(path)
    

def loadData(expert):
    path = "C:/Users/sobhan/Desktop/Kayhan's Project/data/For Scikit/"
    X = []
    Y = []
    with open(path + expert + ".txt", "r") as f:
        for line in f:             
            parts = line.split('\t')[0].split(',')
            vec = []
            for p in parts:
                vec.append(float(p))
            X.append(vec)
            y = line.split('\t')[1].split('\n')[0]
            Y.append(int(y))
    return X, Y

files = listFiles()

[X, Y] = loadData(expert='CA-BPS')
# X, y = make_blobs(n_samples=100000, n_features=4000, centers=2, random_state=0)

# clf = DecisionTreeClassifier(max_depth=None, min_samples_split=2, random_state=0)
# scores = cross_val_score(clf, X, y)
# print ("Decision Tree: " + str(scores.mean()))

clf = RandomForestClassifier(n_estimators=240, max_depth=None, min_samples_split=2, random_state=0, n_jobs=4)
# clf = ExtraTreesClassifier(n_estimators=100, max_depth=None, min_samples_split=2, random_state=0, n_jobs=4)
# clf = KNeighborsClassifier(n_neighbors=10, n_jobs=4)
# clf = MLPClassifier(solver='lbfgs', alpha=1e-5, random_state=1, hidden_layer_sizes=(500, 200, 100))
# clf = AdaBoostClassifier(n_estimators=100)
# clf = linear_model.LogisticRegression(C=1e5, solver='sag', n_jobs=4, max_iter=500)

# clf.fit(X, Y)
# clf.predict(sample)

scores = cross_val_score(clf, X, Y, cv=10, scoring='f1_macro')
print ("Random Forest: " + str(scores.mean()))
# print ("Neuarl Net: " + str(scores.mean()))
# print ("KNN: " + str(scores.mean()))
# print ("AdaBoost: " + str(scores.mean()))
# print ("Logistic Regression: " + str(scores.mean()))
# print ("ExtraTreesClassifier: " + str(scores.mean()))

#list of valid scores: ['accuracy', 'adjusted_rand_score', 'average_precision', 'f1', 'f1_macro', 
#'f1_micro', 'f1_samples', 'f1_weighted', 'neg_log_loss', 'neg_mean_absolute_error', 'neg_mean_squared_error', 
#'neg_median_absolute_error', 'precision', 'precision_macro', 'precision_micro', 'precision_samples', 
#'precision_weighted', 'r2', 'recall', 'recall_macro', 'recall_micro', 'recall_samples', 'recall_weighted', 'roc_auc']
#Check here: http://scikit-learn.org/stable/modules/model_evaluation.html