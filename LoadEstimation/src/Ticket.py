'''
Created on Jul 11, 2016

@author: moosas1
'''

class Ticket():
    id = 0
    t1 = 0
    t2 = 0
    pr = 0
    type = ""
    
    def __init__(self, param):
        parts = param.split() 
        self.id = parts[0] 
        self.t1 = int(parts[2])
        self.t2 = int(parts[3])
        self.pr = int(parts[7])
        self.type = parts[6]
        