'''
Created on May 2, 2016

@author: US
'''

import hierarchy 
import pydot

class treeVisualization:
    '''
    classdocs
    '''


    def __init__(self, orgTree):
        
        
        '''
        Constructor
        '''
        self.tree = orgTree
     
     
    def visulize (self,Node, graph):
        for child in Node.children:
            edge = pydot.Edge(Node.fullName, Node.children[child].fullName)  
            graph.add_edge(edge)
            self.visulize(Node.children[child],graph)
         
    def visUp (self):
        graph = pydot.Dot(graph_type='graph')
        self.visulize(self.tree.root, graph)
        graph.write_png('taxonom.png')