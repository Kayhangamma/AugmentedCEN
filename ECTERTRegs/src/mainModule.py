'''
Created on Oct 28, 2016

@author: US
'''

from preprocess import preprocess
#Comprehensive data:

fullTransactionSet = "Input/Transfer Time Intervals 2015-03-01 to 2016-02-29.csv"
rawDataSet = "Input/Incident Details 2015-03-01 to 2016-02-29.csv"



def main():
    #Build training set based on the transaction set.
    P = preprocess(fullTransactionSet,rawDataSet)
    P.buildTransactionSet()

if __name__ == '__main__':
    main()