#!/usr/bin/env python
# -*- coding: utf-8 -*- 
'''
Created on Dec 27, 2016

@author: sobhan
'''
from gensim.models.word2vec import Word2Vec
#from gensim.models.word2vec import LineSentence
import re

def preprocessInputFile(n):   
    sentences = []    
    count = 0
    with open("data/processed_input.txt", "r") as f:
        for line in f:
            count +=1
            if count == n+1:
                break                  
            parts = line.split()
            line = []
            for p in parts:
                line.append(p)
            sentences.append(line)  
    return sentences         


#Documentation: Word2Vec --> https://radimrehurek.com/gensim/models/word2vec.html
#Documentation: Doc2Vec --> https://radimrehurek.com/gensim/models/doc2vec.html


#sentences = preprocessInputFile(120000)
#print 'data is loaded!'
#model = Word2Vec(sentences, iter=100,size=100, window=5, min_count=5, workers=4)
#model.build_vocab(sentences)
#model.train(sentences)
#model.save('model/sample_Itr100_Size100_W5_mn5_2.m')


model = Word2Vec.load('model/sample_Itr100_Size100_W5_mn5_2.m')
#print model.doesnt_match("exception error warning resolve solution problem incident".split())
#print model.most_similar(positive=['customer'], negative=['agent'])
print model.most_similar('contractor')
