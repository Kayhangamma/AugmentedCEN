'''
Created on Nov 16, 2014

@author: US
'''

import csv
import copy
import operator

class taxonomy:
    
    uniqueGroups = []
    grp = {}

    def __init__(self, hierarchyPath):
        
        cr = csv.reader(open(hierarchyPath,"rb"))
        
        for row in cr:
            self.uniqueGroups.append(row [0])
        
        #print len(self.uniqueGroups)
    def uniqueGroupOverall (self):
        return self.uniqueGroups
    
    '''
    def Segmentation(self):
        PositionMap = {}
        for group in self.uniqueGroups:
            gParts = group.split('-')
            for i in range(len(gParts)):
                PositionMap[gParts[0]]
                
            if gParts>100
    '''        
        
    
    def FindAllGroups (self, sampleNode):
        if sampleNode.ligitTerminal==True:
            self.grp[sampleNode.fullName] = 1
        
        for child in sampleNode.children:
            self.FindAllGroups(sampleNode.children[child])
            
            
    
    def structureConstruction (self, root):
        orgTree = tree(root)
        for group in self.uniqueGroups:
            nodeSequence = self.hyphenSpliter(group)
            #print nodeSequence
            orgTree.addSequence (nodeSequence)
            
        #print orgTree.root.value
        #vs = treeVisualization(orgTree)
        #vs.visUp()
        
        cf = csv.writer(open("orgTree.csv", "wb"))
        self.FindAllGroups(orgTree.root)
        
        orgTree.printallNodes(self.grp, cf)
        self.grp = {}
        
        orgTree.combineNodes(orgTree.root)
        
        cm = csv.writer(open("modTree.csv", "wb"))
        self.FindAllGroups(orgTree.root)
        
        orgTree.printallNodes(self.grp, cm)
        
        return orgTree
                
    
    
    def hyphenSpliter (self, name):
        return name.split('-')
            
    
    
class treeNode:
    def __init__(self, name, value, parent, children, fullName):
        self.name = name
        self.value = value
        self.parent = parent
        self.children = children
        self.ligitTerminal = False 
        self.fullName = fullName
    
    def findInChildren (self, xNodeName):
        for i in self.children:
            if xNodeName == i:
                return self.children[i]
        return self
            
    
    
    
    
class tree:
    def __init__(self, rootName):
        self.root = treeNode(rootName, 0, None, {},"")
        self.GroupConvertor = {}
    
    
     
    def updateFullNamesDown (self, sampleNode):
        for child in sampleNode.children:
            sampleNode.children[child].fullName = sampleNode.fullName +"-"+child
            self.updateFullNamesDown (sampleNode.children[child])
           
        
    def combineNodes(self, SampleNode):
       
        if (len(SampleNode.children)==1) and (not (SampleNode.ligitTerminal)):
            #lift up
            oldSampleName = SampleNode.name
            onlyChildNode = SampleNode.children.itervalues().next()
            onlyChildName = SampleNode.children.iterkeys().next()
            
            #node mix
            SampleNode.name = SampleNode.name+"&#&"+ onlyChildName
            SampleNode.value = onlyChildNode.value
            SampleNode.children = copy.deepcopy(onlyChildNode.children)
            SampleNode.ligitTerminal = onlyChildNode.ligitTerminal
            
            SampleNode.fullName = SampleNode.parent.fullName + "-"+SampleNode.name
            if SampleNode.parent.fullName=="":
                SampleNode.fullName = SampleNode.fullName[1:] 
                
            del onlyChildNode
            
            #update the parent
            SampleNode.parent.children[SampleNode.name] = SampleNode
            del SampleNode.parent.children[oldSampleName]
            
            #update children
            for child in SampleNode.children:
                SampleNode.children[child].parent = SampleNode
                self.updateFullNamesDown(SampleNode) 
            
            #Recurse on the constructed Node
            self.combineNodes(SampleNode)
        
        else:
            #Recurse on Children            
            for child in SampleNode.children:
                self.combineNodes(SampleNode.children[child])
            
            
     
    def printallNodes(self, Groups, c):   
        
        
        self.GroupConvertor = {}
        for g in Groups:
            
            current = self.root
            nodeSequence = g.split('-')
            for nodeName in nodeSequence:
                potentialChild = current.findInChildren(nodeName)
                if current!=potentialChild:
                    current = potentialChild
            #Found the corresponding leaf subject to combinations.
            '''
            nodeTerminal = current
            termParent = nodeTerminal.parent 
            while termParent!=self.root:
                if (not termParent.ligitTerminal) and (len(termParent.children)==1):
                    #Colapase up 
                    tpp = termParent.parent
                    tpn = termParent.name
                    termParent = nodeTerminal
                print nodeTerminal
            '''    
            #print current.fullName
            c.writerow([current.fullName, g.replace("&#&","-")])
            self.GroupConvertor[g.replace("&#&","-")] = current.fullName
          
            
            
            
            
        
        
    def addSequence (self, nodeSeq):
        current = self.root
        current.value +=1
        seqSoFar =""
        for node in nodeSeq:
            seqSoFar+="-"+node 
            #print "    " + current.name
            if node in current.children:
                current = current.children[node]
                current.value +=1
                continue 
            else:
                current.children[node] = treeNode (node,1,current,{}, seqSoFar[1:])
                current = current.children[node]
                
        current.ligitTerminal = True
        
    
    def findGroup (self, StringGroup):
        
        transformedGroupString = self.GroupConvertor[StringGroup]
        
        current = self.root
        nodeSequence = transformedGroupString.split('-')
        for nodeName in nodeSequence:
            potentialChild = current.findInChildren(nodeName)
            if current!=potentialChild:
                current = potentialChild
        return current
    
    def parentsSetGenerator (self, sampleNode):
        current = sampleNode
        height = 0
        parents = {}
        while current!=self.root:
            parents[current.fullName] = height
            current = current.parent
            height+=1
        
        parents["ROOOOOOT"] = height
        return parents
        
    
    def distanceOnTree (self, firstString, SecondString):
        firstNode = self.findGroup(firstString)
        secondNode = self.findGroup(SecondString)
        
        firstParents = self.parentsSetGenerator(firstNode)
        secondParents = self.parentsSetGenerator(secondNode)
        
        sorted_firstParents = sorted(firstParents.items(), key=operator.itemgetter(1))
        sorted_secondParents = sorted(secondParents.items(), key=operator.itemgetter(1))
        
        for el in sorted_firstParents:
            for it in sorted_secondParents:
                if el[0] == it[0]:
                    #print el[1]
                    #print it[1]
                    return el[1]+it[1]
        
        return -1
        
        
                
            
            
            
            
    
        
        
        
    
            
    
        
    
        