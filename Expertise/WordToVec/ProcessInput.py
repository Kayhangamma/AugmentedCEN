#!/usr/bin/env python
# -*- coding: utf-8 -*- 
'''
Created on Dec 27, 2016

@author: sobhan
'''
#import nltk
#nltk.download()
from nltk.corpus import stopwords
#from gensim.models.word2vec import LineSentence
import re


def preprocessInputFile(n):
    delim = {'\t', '"', ':', ';', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', 
             '(', ')', '-', '_', '=', '+', '\\', '|', '{', '[', '}', ']', '<', '>', ',', '.', '?', '/', '¿'}
    englishStopWords = stopwords.words('english')
#     wr = open("data/incidents_as_docs.txt", 'w')
    wr = open("data/processed_input.txt", 'w')
    count = 0
    
    with open("data/IncidentsDescriptionTrain.txt", "r") as f:
        for line in f:
            id = line.split('\t')[0]
            line = line.replace('\n', '').replace('\'', '').lower().split('\t')[1]
            for c in delim:
                line = line.replace(c, ' ')
            line = re.sub(r"[  ]+", " ", line)            
            parts = line.split()
            line = ""
            for p in parts:
                if p not in englishStopWords:
                    _p = re.sub(r"[0-9]+", "", p)
                    if p == _p: #filter just number
                        line += " " + p
                    elif _p!="":
                        line += " " + p
            if len(line) > 0:
                line = line[1:]
                wr.write(line + "\n")
#                 wr.write(id + "\t" + line + "\n")
            count+=1
            if count==n:
                break              
    wr.close()  

preprocessInputFile(200000)
print 'data is processed!'