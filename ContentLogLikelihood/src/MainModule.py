'''
Created on Jul 29, 2014

@author: mohark1
'''

import csv
from BigramModeler import languageModel
#from classification import classifier

def main ():
    #paths = "files\\PathFrequencies.csv"
    #SLAs = "files\\MTTR.csv"
    #transfers = "files\\TransfersOrg.csv"
    description = "files\\IncDes.csv"
    stopPath = "files\\stop.csv"
    contentLogLikelihoodOutput = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Inc_Content_likelihood\\"
    #PathThreshold = 14
    
    #routinenessPathDic= {}
    #incident_paths = {}

    #incident_paths = incidentRoutifier (transfers)
    #routinenessPathDic = pathRoutineEval(incident_paths)

    #build Stop words dictionary
    stopDictionary = BuildStopDic (stopPath)
    
    #incident_SLAs = incidentSLAFinder(SLAs)
    incident_Description, incident_Resolution = incidentDescriptor (description) 
    
    incident_Description_purified = redactor (incident_Description, stopDictionary)
    incident_Resolution_purified = omitFirstLine (redactor (incident_Resolution, stopDictionary))
    
    descriptionLanguageModelObject = languageModel (incident_Description_purified)
    resolutionLanguageModelObject = languageModel (incident_Resolution_purified)
    
    
    inc_des_content_routineness = descriptionLanguageModelObject.RoutinenessIndexCalc()
    inc_res_content_routineness = resolutionLanguageModelObject.RoutinenessIndexCalc()

    
    
    createOutputFile (incident_Description, incident_Resolution, incident_Description_purified ,incident_Resolution_purified, inc_des_content_routineness, inc_res_content_routineness, contentLogLikelihoodOutput)
    
    
    #-------------------------------------------------
    #supervisedSet = (incident_Description_purified, routinenessPathDic, PathThreshold)
    #classifierObj= classifier (supervisedSet)
    
    

    
    '''
    print routinenessPathDic[("NI-PRODUCTION-SOLUTIONS-COMMERCIAL-DSM","NI-COMMERCIAL-PAK-BA-TEAM-DSM","NI-PRODUCTION-SOLUTIONS-COMMERCIAL-DSM","NI-PRODUCTION-SOLUTIONS-CUSTOMERS-DSM")]
    print routinenessPathDic[("NSC-ITSD-AGENCY-AGENTCENTER","NI-AGENCY-TECHNOLOGY-SERVICES-DSM","NI-PROPERTY-AC-DSM","NI-AL-SERVICE-ADVANTAGE-PROPERTY-ADV","NI-AGENCY-TECHNOLOGY-SERVICES-DSM","NSC-ITSD-AGENCY-AGENTCENTER")]
    print routinenessPathDic[("NI-THI-PRODUCTION-CONTROL","NSC-ITSD-AGENCY","NI-THI-PRODUCTION-CONTROL")]        
    print routinenessPathDic[("NI-PRODUCTION-SOLUTIONS-HOME-DSM","NI-AL-SERVICE-ADVANTAGE-PROPERTY-ADV","NI-PRODUCTION-SOLUTIONS-HOME-DSM","NI-PROPERTY-MF-DSM","NI-PRODUCTION-SOLUTIONS-HOME-DSM")]            
    '''
def omitFirstLine (incident_Resolution_intermidiate):
    distilled_incident_res = {}
    for inc in incident_Resolution_intermidiate:
        text = incident_Resolution_intermidiate[inc]
        indx = text.find(")")+3
        text = text[indx:]
        if len(text)<=1:
            text = "done"
        distilled_incident_res[inc] = text
    
    return distilled_incident_res
    
    

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
    
def createOutputFile (incident_Description, incident_Resolution, incident_Description_purified, incident_Resolution_purified, inc_des_content_routineness, inc_res_content_routineness, contentLogLikelihoodOutput):
    c = csv.writer(open(contentLogLikelihoodOutput+"Inc_Loglikelihood.csv", "wb"))
    c.writerow(["Incident ID", "Description","Redacted_Description", "Resolution","Redacted_Resolution", "LikelihoodOfDescription", "LikelihoodOfResolution"])
    
    for inc in incident_Description:
        des = incident_Description[inc]
        res = incident_Resolution[inc]
        desRedact = incident_Description_purified[inc]
        resRedact = incident_Resolution_purified[inc]
        desLikelihood = inc_des_content_routineness [inc]
        resLikelihood = inc_res_content_routineness [inc]
        
        c.writerow([inc,des,desRedact,res, resRedact, desLikelihood, resLikelihood])
                  
    

def changeChar (path):
    
    returnableString = ""
    for element in path:
        returnableString = returnableString+"#"+element 
    
    if returnableString[-1] == "#":
        returnableString = returnableString[:-1] 
    if returnableString[0] == "#":
        returnableString = returnableString[1:]
    
    return returnableString
    
    
def incidentDescriptor (description) :
    cr = csv.reader(open(description,"rb"))
    incident_desc = {}
    incident_res = {}
    
    rowIndex = 0
    for row in cr:
        if rowIndex !=0 :
            currentInc = row[0]
            
            
            incident_desc[currentInc]= row[1]
            incident_res[currentInc] = row[2]
        rowIndex+=1
    return incident_desc,incident_res 
    
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
    print len(incident_paths)

    for inc in incident_paths:
        currentPath = tuple(incident_paths[inc])
        if currentPath in freqPathDic:
            freqPathDic[currentPath] +=1
        else:
            freqPathDic[currentPath] =1 
    
        
    return freqPathDic 
    
    

if __name__ == '__main__':
    main()