'''
Created on Jul 29, 2014

@author: mohark1
'''

import csv
from BigramModeler import languageModel
from classification import classifier
from scatterPlot import scatter 

def main ():
    #paths = "files\\PathFrequencies.csv"
    SLAs = "files\\MTTR.csv"
    transfers = "files\\TransfersOrg.csv"
    description = "files\\IncDes.csv"
    stopPath = "files\\stop.csv"
    PathThreshold = 672
    
    #routinenessPathDic= {}
    #incident_paths = {}

    incident_paths = incidentRoutifier (transfers)
    routinenessPathDic = pathRoutineEval(incident_paths)

    #build Stop words dictionary
    stopDictionary = BuildStopDic (stopPath)
    
    incident_SLAs = incidentSLAFinder(SLAs)
    incident_priority = incidentPriority(description)
    incident_Description = incidentDescriptor (description) 
    
    incident_Description_purified = redactor (incident_Description, stopDictionary)
    LanguageModelObject = languageModel (incident_Description_purified)
    inc_content_routineness = LanguageModelObject.RoutinenessIndexCalc()

    
    
    createOutputFile (incident_Description, incident_Description_purified ,incident_paths, routinenessPathDic, inc_content_routineness)
    
    
    scatterPlotObj = scatter (incident_paths, routinenessPathDic, inc_content_routineness, incident_SLAs)
    
    #scatterPlotObj.regulizer()
    scatterPlotObj.plot(scatterPlotObj.X_points_met,scatterPlotObj.Y_points_met, scatterPlotObj.X_points_breached,scatterPlotObj.Y_points_breached)
    #scatterPlotObj.plotBothSplit(PathThreshold) 
    
    #-------------------------------------------------
    
    #supervisedSet = (incident_Description_purified, incident_paths, routinenessPathDic, PathThreshold)
    #classifierObj= classifier (supervisedSet)
    #classifierObj.feature_extraction_classification()
    

    
    '''
    print routinenessPathDic[("NI-PRODUCTION-SOLUTIONS-COMMERCIAL-DSM","NI-COMMERCIAL-PAK-BA-TEAM-DSM","NI-PRODUCTION-SOLUTIONS-COMMERCIAL-DSM","NI-PRODUCTION-SOLUTIONS-CUSTOMERS-DSM")]
    print routinenessPathDic[("NSC-ITSD-AGENCY-AGENTCENTER","NI-AGENCY-TECHNOLOGY-SERVICES-DSM","NI-PROPERTY-AC-DSM","NI-AL-SERVICE-ADVANTAGE-PROPERTY-ADV","NI-AGENCY-TECHNOLOGY-SERVICES-DSM","NSC-ITSD-AGENCY-AGENTCENTER")]
    print routinenessPathDic[("NI-THI-PRODUCTION-CONTROL","NSC-ITSD-AGENCY","NI-THI-PRODUCTION-CONTROL")]        
    print routinenessPathDic[("NI-PRODUCTION-SOLUTIONS-HOME-DSM","NI-AL-SERVICE-ADVANTAGE-PROPERTY-ADV","NI-PRODUCTION-SOLUTIONS-HOME-DSM","NI-PROPERTY-MF-DSM","NI-PRODUCTION-SOLUTIONS-HOME-DSM")]            
    '''


def redactor (incident_Description, stopDictionary):
    
    distilled_incident_Description = {}
    for inc in incident_Description:
        redacted = removeStopWords(incident_Description[inc], stopDictionary)
        distilled_incident_Description[inc] = redacted
    
    
    return distilled_incident_Description     
        
    
        

def BuildStopDic (stopPath):
    stopwords = {}
        
    cr = csv.reader(open(stopPath,"rb"))
    for row in cr:
        stopwords[row[0]] = 1
        
    return stopwords

def removeStopWords (myString, stopDictionary):
    myString = myString.lower()
    redacted = ' '.join(w for w in myString.split() if w not in stopDictionary)
    #redacted = ' '.join(w for w in re.split('\s|,|.|\n|\t',myString) if w not in stopDictionary)
    return redacted

def createOutputFile (incident_Description, incident_Description_purified, incident_paths, routinenessPathDic, inc_content_routineness):
    c = csv.writer(open("correspondence.csv", "wb"))
    c.writerow(["Incident ID", "Description","Redacted_Description","path","RoutinenessOfPath","RoutinenessOfContent"])
    errorCount =0 
    error2Count = 0
    for inc in incident_Description:
        des = incident_Description[inc]
        desRedact = incident_Description_purified[inc]
        contentRoutineness = inc_content_routineness [inc]
        if inc in incident_paths:
            
            path = tuple(incident_paths[inc])
            #print path
            if path in routinenessPathDic:
                freq = routinenessPathDic[path]
                pathCleanChar = changeChar(path)
                c.writerow([inc,des,desRedact,pathCleanChar,freq, contentRoutineness])
                  
            else:
                #print str(error2Count)+"\t"+str(path)
                error2Count +=1
                continue
            
            
        else:
            errorCount +=1
            continue
   
    print errorCount
    print error2Count
  
    

def changeChar (path):
    
    returnableString = ""
    for element in path:
        returnableString = returnableString+"#"+element 
    
    if returnableString[-1] == "#":
        returnableString = returnableString[:-1] 
    if returnableString[0] == "#":
        returnableString = returnableString[1:]
    
    return returnableString
    
def incidentPriority(description):
    cr = csv.reader(open(description,"rb"))
    incident_priority = {}
    rowIndex = 0
    for row in cr:
        if rowIndex !=0 :
            currentInc = row[0]       
            
            incident_priority[currentInc]= row[2]
        rowIndex+=1
    return incident_priority
    
        
def incidentDescriptor (description) :
    cr = csv.reader(open(description,"rb"))
    incident_desc = {}
    rowIndex = 0
    for row in cr:
        if rowIndex !=0 :
            currentInc = row[0]
            
            
            incident_desc[currentInc]= row[1]
        rowIndex+=1
    return incident_desc
    
def incidentSLAFinder(SLAs):
    cr = csv.reader(open(SLAs,"rb"))
    incident_SLAs = {}
    rowIndex = 0
    for row in cr:
        if rowIndex !=0 :
            currentInc = row[0]
            incident_SLAs[currentInc]= row[2]
        rowIndex+=1
    return incident_SLAs
    
    
def incidentRoutifier (transfers):
    cr = csv.reader(open(transfers,"rb"))
    incident_paths = {}
    rowIndex = 0
    for row in cr:
        if rowIndex !=0 :
            currentInc = row[0]
            if currentInc not in incident_paths:
                incident_paths[currentInc] = []
                incident_paths[currentInc].append(row[1])
            else :
                incident_paths[currentInc].append(row[1])
        rowIndex +=1
    return incident_paths

def pathRoutineEval (incident_paths):
    
    freqPathDic = {}

    for inc in incident_paths:
        currentPath = tuple(incident_paths[inc])
        if currentPath in freqPathDic:
            freqPathDic[currentPath] +=1
        else:
            freqPathDic[currentPath] =1 
    
        
    return freqPathDic 
    
    

if __name__ == '__main__':
    main()