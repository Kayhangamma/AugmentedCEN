'''
Created on Apr 29, 2015

@author: remoteuser
'''

class record:
    inc = ""
    group = ""
    page = ""
    updtime=""
    resltime =""
    clostime = ""

    def __init__(self, row):
      
        self.inc = row[0]
        self.group = row[1]
        self.page = row[2]
        #self.updtime = row[3]
        #self.resltime = row[7]
        self.updtime = row[4]
        self.resltime = row[5]
        self.clostime = row[6]