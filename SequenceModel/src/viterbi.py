'''
Created on Feb 17, 2014

@author: Kayhan
'''

import copy


class viterbi:
    table = []
    def __init__(self, probTagSeq, tagWordprob, wordSequence):
        self.table = []
        temp = {}
        for tag in probTagSeq:
            temp[tag] = [0,None]
        for word in wordSequence:
            self.table.append([word,copy.deepcopy(temp)])
        self.fill_in(probTagSeq, tagWordprob)

    def fill_in(self,probTagSeq, tagWordprob):
        for i in range(len(self.table)):
            for token in self.table[i][1]:
                word = self.table[i][0]
                if i == 0:
                    self.table[i][1][token][0] = tagWordprob[token][word]
                else:
                    max = None
                    guess = None
                    c = None
                    for k in self.table[i-1][1]:
                        c = self.table[i-1][1][k][0] + probTagSeq[k][token]
                        if max == None or c > max:
                            max = c
                            guess = k
                    max += tagWordprob[token][word]
                    self.table[i][1][token][0] = max
                    self.table[i][1][token][1] = guess

    def return_max(self):
        tokens = []
        token = None
        for i in range(len(self.table)-1,-1,-1):
            if token == None:
                max = None
                guess = None
                for k in self.table[i][1]:
                    if max == None or self.table[i][1][k][0] > max:
                        max = self.table[i][1][k][0]
                        token = self.table[i][1][k][1]
                        guess = k
                tokens.append(guess)
            else:
                tokens.append(token)
                token = self.table[i][1][token][1]
        tokens.reverse()
        return tokens