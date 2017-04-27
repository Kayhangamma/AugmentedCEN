'''
Created on Jul 2, 2015

@author: mohark1
'''

def text_size(text, charset='iso-8859-1'):
    return len(text.encode(charset)) * 8 * 1e-6

def sizeOf(trainingset, testset):
    train_size_mb = sum(text_size(text) for text in trainingset)
    test_size_mb = sum(text_size(text) for text in testset) 

    print("Training set size: {0} MB".format(int(train_size_mb)))
    print("Test set size: {0} MB".format(int(test_size_mb)))
    
