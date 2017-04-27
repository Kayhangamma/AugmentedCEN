'''
Created on Apr 28, 2015

@author: mohark1
'''
import csv
import copy
from Record import record
from Filewriter import fileWriter
from copy import deepcopy



def CandidateChunker (eachFileContent, fileNum):
    indxDic = {} #Numbers each incident according to appearance
    CandDic = {} #For each incident list of all pages
    
    indx = 0     #counter of incidents    
    for i in range(fileNum):
        for j in range(len(eachFileContent[i])):
            current = record(eachFileContent[i][j])
            if current.inc not in CandDic:
                indxDic[indx] = current.inc
                CandDic[current.inc]= []
                CandDic[current.inc].append(copy.deepcopy(current)) # list of objects:[current.inc, current.group, current.page, current.updtime, current.resltime, current.clostime]
                indx+=1
            else:
                CandDic[current.inc].append(copy.deepcopy(current))
    
    return (indxDic, CandDic)        

def QTimesPerGroup (eachFileContent, fileNum):
    

    (indexDic,candidateDic) = CandidateChunker (eachFileContent, fileNum) 
    
    #print (len(candidateDic))
    #raw_input("Press Enter to continue...")
    incidentSeq = []
    
    for i in range(len(indexDic)):
        cur_inc= indexDic[i]
        cur_inc_rows = candidateDic[cur_inc]
        
        j=0
        while (j<len(cur_inc_rows)):
            current = cur_inc_rows[j]
            la = 1 #look ahead
            if j+la < len(cur_inc_rows):
                Next = cur_inc_rows[j+la]
                while (Next.group==current.group):
                    la+= 1
                    if j+la < len(cur_inc_rows):
                        Next = cur_inc_rows[j+la]
                    else:
                        Previous = cur_inc_rows[j+la-1]
                        incidentSeq.append([Previous.inc,Previous.group,current.updtime, Previous.updtime])
                        j = j+la-1
                        break
                if (Next.group!=current.group):
                    incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime])
                    j = j+la-1
                    
            else:
                incidentSeq.append([current.inc,current.group,current.updtime, current.updtime])       
                
            j+=1
    return incidentSeq
    
def main():
    #skiplist = {"IM01822155":1, "IM01855694":1, "IM01864175":1, "IM01864638":1, "IM01864650":1, "IM01864660":1, "IM01878590":1,"IM01878611":1,"IM01878636":1,"IM01878626":1,"IM01878612":1,"IM01878625":1,"IM01878607":1,"IM01884356":1,"IM01886416":1,"IM01878633":1}
    baseName = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2015\\Originaldata\\Biweekly_Page\\Full_page_data_0"
    postfix = ".csv"
    headings = ["Incident ID","Transfer Group", "Start", "End","FileIndex"]
    resultPath = "C:\\Kayhan\\OS University\\Research\\\Jay\\\TransferGroups\\Data\\2015\\Originaldata\\Qtimes.csv"
    filenames = [ baseName+str(i+1)+postfix for i in range(27)]
    eachFileContent = []
    fileNum = 0
    
    for fileObj in filenames:
        eachFileContent.append([])
        cr = csv.reader(open(fileObj,"rb"))
        for row in cr:
            if row[0] == "Incident ID":
                continue
            eachFileContent[fileNum].append(row)
        
        fileNum +=1
        
    
    incSeq = QTimesPerGroup(eachFileContent, fileNum)
    F = fileWriter()
    F.csvWrite(headings, incSeq, resultPath)

if __name__ == '__main__':
    main()