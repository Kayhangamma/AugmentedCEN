'''
Created on Apr 28, 2015

@author: mohark1
'''
import csv
import copy
from Record import record
from Filewriter import fileWriter
import datetime
import time
from time import mktime
import SL_QTime
import os
import operator

#from copy import deepcopy

global_inc_skiplist = {}
page_lack_skiplist = {}

def timeDifference (TimeString1, TimeString2 ):
    #print "------------"
    #print TimeString1
    #print TimeString2
    first_time_object = time.strptime(TimeString1, "%m/%d/%y %H:%M")
    second_time_object = time.strptime(TimeString2, "%m/%d/%y %H:%M")
    
    dt_first = datetime.datetime.fromtimestamp(mktime(first_time_object))
    dt_second = datetime.datetime.fromtimestamp(mktime(second_time_object))
    
    dt_diff = dt_first - dt_second
    
    minutes_seconds_pair = divmod(dt_diff.days * 86400 + dt_diff.seconds, 60)
    return minutes_seconds_pair
    
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


def FindClosureResolutionPages (inc_rows): 
    cur_inc_cptClosTime = inc_rows[0].clostime
    cur_inc_cptResTime = inc_rows[0].resltime
    
    closure_page = 0
    resolution_page = 0
    for ind_row in reversed(inc_rows):
        if closure_page ==0: 
            if ind_row.updtime == cur_inc_cptClosTime:
                closure_page = ind_row.page
        else: #Closure page was found go for resolution page
            if ind_row.updtime == cur_inc_cptResTime:
                resolution_page = ind_row.page
                break
    
    if resolution_page == 0:
        for ind_row in reversed(inc_rows):
            if abs(timeDifference(ind_row.updtime, cur_inc_cptResTime)[0])<=2:
                resolution_page = ind_row.page
                #print str(inc_rows[0].inc) +"     "+inc_rows[0].resltime+ "        Resolution nearly matched"
                break            
    if resolution_page == 0 or closure_page == 0:
        #print str(inc_rows[0].inc) +"       Resolution never matched"*int(resolution_page == 0)+ "       Closure never matched"*int(closure_page == 0)
        global_inc_skiplist [inc_rows[0].inc] = 1
    return (resolution_page, closure_page)

def QTimesPerGroup (eachFileContent, fileNum):

    (indexDic,candidateDic) = CandidateChunker (eachFileContent, fileNum) 
    
    incidentSeq = []
    
    for i in range(len(indexDic)):
        cur_inc= indexDic[i]
        cur_inc_rows = candidateDic[cur_inc]
        
        #Look up the closure and resolution from the end:
        (resPage, ClosPage) = FindClosureResolutionPages (cur_inc_rows)
        
        if cur_inc_rows[0].page!="1":
            page_lack_skiplist[cur_inc] = 1
        #Integrity check
        #If incident does have a matching res and closure, or it has lacking pages
        if (cur_inc in global_inc_skiplist) or (cur_inc in page_lack_skiplist):
            continue
        
        #Check all closPages should be different from resPages
        post_closureFlag = False
        post_resolutionFlag = False
        
        j=0
        while (j<len(cur_inc_rows)):
            current = cur_inc_rows[j]
            la = 1 #look ahead
            if j+la < len(cur_inc_rows):
                Next = cur_inc_rows[j+la]
                while (Next.group==current.group and Next.page!=resPage and Next.page !=ClosPage):
                    la+= 1
                    if j+la < len(cur_inc_rows):
                        Next = cur_inc_rows[j+la]
                    else:
                        Previous = cur_inc_rows[j+la-1]
                        elapsed_mins = timeDifference (Previous.updtime, current.updtime )[0]
                        sl_mins = SL_QTime.SL_timeDifference(Previous.updtime, current.updtime )
                        incidentSeq.append([Previous.inc,Previous.group,current.updtime, Previous.updtime, elapsed_mins,sl_mins, "Post-Closure"])
                        j = j+la-1
                        break
                if Next.page == ClosPage:
                    elapsed_mins = timeDifference (Next.updtime, current.updtime )[0]
                    sl_mins = SL_QTime.SL_timeDifference(Next.updtime, current.updtime )
                    incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime,elapsed_mins,sl_mins, "Closure"])
                    post_closureFlag = True
                    j = j+la-1
                    
                elif Next.page == resPage:
                    elapsed_mins = timeDifference (Next.updtime, current.updtime)[0]
                    sl_mins = SL_QTime.SL_timeDifference(Next.updtime, current.updtime )
                    incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime,elapsed_mins,sl_mins, "Resolution"])
                    post_resolutionFlag = True
                    j = j+la-1
                    
                elif Next.group!=current.group:
                    if (post_closureFlag == False) and (post_resolutionFlag==False):
                        elapsed_mins = timeDifference (Next.updtime, current.updtime)[0]
                        sl_mins = SL_QTime.SL_timeDifference(Next.updtime, current.updtime )
                        incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime,elapsed_mins,sl_mins, "Progress"])
                    elif (post_resolutionFlag==True) and (post_closureFlag == False):
                        elapsed_mins = timeDifference (Next.updtime, current.updtime)[0]
                        sl_mins = SL_QTime.SL_timeDifference(Next.updtime, current.updtime )
                        incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime,elapsed_mins,sl_mins, "Post-resolution-Progress"]) 
                    elif (post_resolutionFlag==True) and (post_closureFlag == True):
                        elapsed_mins = timeDifference (Next.updtime, current.updtime)[0]
                        sl_mins = SL_QTime.SL_timeDifference(Next.updtime, current.updtime )
                        incidentSeq.append([current.inc,current.group,current.updtime, Next.updtime,elapsed_mins,sl_mins,"Post-Closure-Progress"])
                    else:
                        print ("Resolution time never happened but closure did")
                    j = j+la-1
            else:
                if post_closureFlag == False:
                    print ("Closure never happened")
                #incidentSeq.append([current.inc,current.group,current.updtime, current.updtime])       
                
            j+=1
    return incidentSeq
    
def creatOutputFile (data, headings, FileOutPath):
    F = fileWriter()
    F.csvWrite(headings, data, FileOutPath)
    
def UptoResolutionOnly(data):
    inc_with_resolution = {}
    for row in data:
        if row[6]=="Resolution":
            inc_with_resolution[row[0]] = 1
    
    new_data = []
    for row in data:
        if row[0] in inc_with_resolution:
            if row[6]=="Progress" or row[6]=="Resolution":
                new_data.append(row)
    return new_data

def creatExpandedOutputFile (group_headings,incident_order,incident_path, time_intevals, FileOutPath):
    F = fileWriter()
    F.expandedCsvWrite(group_headings, incident_order,incident_path, time_intevals, FileOutPath)

def tabularTrasform(trs):
    uniqueGroups = {}
    incident_path_dic = {}
    incident_et_time_dic = {}
    incident_es_time_dic = {}
    incident_order_preserve = []
    
    for row in trs:
        inc = row[0]
        transfer_group = row[1]
        #Elapsed Time
        et = row[4]
        #Effective Service Level time
        es = row[5]
        
        if transfer_group not in uniqueGroups:
            uniqueGroups[transfer_group] = 1
        else:
            uniqueGroups[transfer_group] +=1 
        
        if inc not in incident_path_dic:
            incident_order_preserve.append(inc)
            incident_path_dic[inc] = []
            incident_et_time_dic[inc] = []
            incident_es_time_dic[inc] = []
       
        incident_path_dic[inc].append(transfer_group)
        incident_et_time_dic[inc].append(et)
        incident_es_time_dic[inc].append(es)
    
    sorted_groups = sorted(uniqueGroups.items(), key=operator.itemgetter(1))
    group_headings = [el[0] for el in sorted_groups]
            
    return (group_headings,incident_order_preserve,incident_path_dic, incident_et_time_dic, incident_es_time_dic)
         

    
def main():
    baseName = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2015\\Originaldata\\Biweekly_Page\\Full_page_data_0"
    postfix = ".csv"
    headings = ["Incident ID","Transfer Group", "Start", "End","Elapsed Time","Effective SL Time", "Type"]
    resultDir = "C:\\Kayhan\\OS University\\Research\\\Jay\\\TransferGroups\\Data\\2015\\Originaldata\\QTimes_Result"
    resultPathFull=resultDir+"\\QtimesFull.csv"
    resultPathResolution = resultDir+"\\QtimesResolution.csv"
    resultElapsedMatrix = resultDir+"\\GroupsMatrix_Elapsed.csv"
    resultSLMatrix = resultDir+"\\GroupsMatrix_SL.csv"
    
    if not os.path.exists(resultDir):
        os.makedirs(resultDir)
    
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
        
    creatOutputFile (incSeq,headings, resultPathFull)
    trs = UptoResolutionOnly(incSeq)
    creatOutputFile (trs,headings, resultPathResolution)
    
    # Tabular transformation
    (group_headings,incident_order,incident_path, incident_et, incident_es) = tabularTrasform(trs)
    group_headings.insert(0, "Incident ID")
    group_headings.insert(1, "Path")
    
    creatExpandedOutputFile(group_headings,incident_order,incident_path, incident_et, resultElapsedMatrix)
    creatExpandedOutputFile(group_headings,incident_order,incident_path, incident_es, resultSLMatrix)
    


if __name__ == '__main__':
    main()