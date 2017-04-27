'''
Created on Dec 8, 2014

@author: US
'''

import csv

class taxonomy:
    
    uniqueGroups = []


    def __init__(self, hierarchyPath):
        
        cr = csv.reader(open(hierarchyPath,"rb"))
        
        for row in cr:
            self.uniqueGroups.append(row [0])
        
        print len(self.uniqueGroups)
        
    
    def structureConstruction (self, root):
        orgTree = tree(root)
        for group in self.uniqueGroups:
            nodeSequence = self.hyphenSpliter(group)
            print nodeSequence
            orgTree.addSequence (nodeSequence)
        print orgTree.root.value
                
                
            
    
    def hyphenSpliter (self, name):
        return name.split('-')
            
    
    
class treeNode:
    def __init__(self, name, value, parent, children):
        self.name = name
        self.value = value
        self.parent = parent
        self.children = children
    
    
    
    
    
    
class tree:
    def __init__(self, rootName):
        self.root = treeNode(rootName, 0, None, {})
        
    def addSequence (self, nodeSeq):
        current = self.root
        current.value +=1
        for node in nodeSeq:
            print "    " + current.name
            if node in current.children:
                current = current.children[node]
                current.value +=1
                continue 
            else:
                current.children[node] = treeNode (node,1,current,{})
                current = current.children[node]
                
            
            
            
            
    
        
        
        
    
            
    
        
    
        