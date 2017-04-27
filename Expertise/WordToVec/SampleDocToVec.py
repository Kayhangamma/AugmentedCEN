#!/usr/bin/env python
# -*- coding: utf-8 -*- 
'''
Created on Dec 27, 2016

@author: sobhan
'''
import gensim
from gensim.models.doc2vec import Doc2Vec
from nltk.stem.porter import PorterStemmer
from gensim.models.doc2vec import TaggedDocument
from gensim.models.word2vec import Word2Vec

def loadInputFile(n):
    incidents = []
    p_stemmer = PorterStemmer()
    count = 0
    with open("data/incidents_as_docs.txt", "r") as f:
        for line in f:
            count += 1
            if count == n:
                break
            tag = line.split('\t')[0]
            words = line.split('\t')[1].split()
#             stemmed_tokens = [p_stemmer.stem(w) for w in words]
#             td = TaggedDocument(stemmed_tokens, tag)
#             td = TaggedDocument(gensim.utils.to_unicode(str.encode(' '.join(words))).split(),tag.split())
            td = TaggedDocument(words, tag.split())
            incidents.append(td)
    return incidents


#Documentation: Word2Vec --> https://radimrehurek.com/gensim/models/word2vec.html
#Documentation: Doc2Vec --> https://radimrehurek.com/gensim/models/doc2vec.html
#Got some help from 'https://ireneli.eu/2016/07/27/nlp-05-from-word2vec-to-doc2vec-a-simple-example-with-gensim/' too!

# incidents = loadInputFile(120000)
# print 'data is loaded!'
# model = Doc2Vec(incidents, size=120, window=5, min_count=5, workers=4)
# for epoch in range(500):    
#     print ("epoch: " + str(epoch))
#     model.train(incidents)
#     model.alpha -= 0.002
#     model.min_alpha = model.alpha
# print 'model is created!'
# model.save('model/docToVec_Itr500_Size120_W5_mn5.m')

model_dv = Doc2Vec.load('model/docToVec_Itr500_Size120_W5_mn5.m')
# model_wv = Word2Vec.load('model/sample_Itr1000_Size100_W5_mn5_2.m')

# print model.docvecs[13]
print model_dv.docvecs.most_similar("IM02063023")
# print model_dv.most_similar('plaza')
# print model_wv.most_similar('plaza')
print 'done!'
