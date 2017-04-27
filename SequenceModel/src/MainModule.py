'''
Created on Feb 16, 2014

@author: Kayhan
'''
import hiddenMarkov
import conditionalRandom

def main():
    hiddenMarkovModel()
    conditionalRandomfield()
    

    
def hiddenMarkovModel():
    hmm = hiddenMarkov.hiddenMarkov()
    print "1. HMM: Please wait... this will take a few minutes"
    hmm.tagTableConstructor()
    hmm.testing()
    
def conditionalRandomfield():
    crf = conditionalRandom.CRF()
    print "2. CRF: please wait ... this will take a few minutes"
    #crf.trainTransformationFile(1)
    #crf.train(1)
    crf.testTransformationFile(1)
    #crf.generatePrediction(1)
    print "a. CRF with only word as the feature prediction performed"
    crf.test(1)
    
   
    #crf.trainTransformationFile(5)
    #crf.train(5)
    crf.testTransformationFile(5)
    #crf.generatePrediction(5)
    print "b. CRF with five word related features prediction performed"
    crf.test(5)
    
    


if __name__ == '__main__':
    main()