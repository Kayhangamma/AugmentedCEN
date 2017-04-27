'''
Created on Jan 10, 2017

@author: US
'''

def main():
    
    inputfile = "./files/quick analysis/DetailPredictionReport/DetailPredictionReport_program.csv"
    RoutineGroups = ["SG-WINS-AES","NI-ICA-RAPID-RESPONSE-TEAM","NI-OPERATIONS-FIRE","NSC-CCS-DAAS","NI-BAT-NBP","NI-THI-PRODUCTION-CONTROL","NI-PRODUCTION-SOLUTIONS-CLAIM-DSM","NSC-AGENCY-INFRASTRUCTURE","NI-COMMERCIAL-E-FILE","NSC-NET-BACKUP","NI-DATAWH-ATT","NI-CLAIMS-CLAIMCENTER-LVL2","NSC-CCS-MPS" ,"NSC-ITSD-ENTERPRISE-ECHO","CA-FINANCE-EFIN","NI-UNDERWRITING-CMH-REPORT-SERVICES","NI-AGENCY-DOCVAULT","CA-RAPID-RESPONSE","NSC-ITSD-ENTERPRISE-PSWD","CA-BPS","NI-NBPWEB-BAM","NI-AWD-DSM","NI-OPERATIONS-NAPS","NI-CLAIMS-DESKTOP","NSC-IDADMIN-AGENCY","NSC-DIR-SERVICES","NSC-DESK-RUN","NSC-VIRUS","NSC-IDADMIN-CORE","NSC-WEB-MIDDLEWARE","NI-NBPWEB-BAM-BUSINESS", "NSC-IDADMIN-RACF"]
    
    print len(RoutineGroups)
    import csv
    
    cr = csv.reader(open(inputfile,"rb"))
    
    allDict = {}
    actualLabelDict = {}
    
    
    RoutineGroupsDic = {}
    for rg in RoutineGroups:
        RoutineGroupsDic[rg] = 1
    
    
    
    index = 0
    for row in cr:
        if index!=0:
            
            if row[2] not in RoutineGroupsDic:
                continue
            
            # Cosine, LM, EN
            allDict[row[0]] = [float(row[11]), float(row[9]), float(row[14])]
            actualLabelDict[row[0]] = row[3]
        
        index+=1
    
    
    cutoff = 0.00
    
    cutoffDict = {}
    
    while cutoff<1.01:
        print cutoff
    
        #predictions = {}
        TP = [0.00, 0.00, 0.00] 
        FP = [0.00, 0.00, 0.00]
        TN = [0.00, 0.00, 0.00]
        FN = [0.00, 0.00, 0.00]
        for el in allDict:
            pred = []
            for i in range(3):
                if allDict[el][i]>cutoff:
                    pred.append("Res")
                else:
                    pred.append("Trans")
                
            #predictions[el] = pred
            
            act = actualLabelDict[el]
            for i in range(3):
                if act == pred[i]:
                    if act == "Res":
                        TP[i]+=1.00
                    else:
                        TN[i]+=1.00
                else:
                    if act == "Res":
                        FN[i]+=1.00
                    else:
                        FP[i]+=1.00
                    
        output = {1:[], 2:[], 3:[]}            
        recall = [0.00, 0.00, 0.00]
        precision = [0.00, 0.00, 0.00]
        F1 = [0.00, 0.00, 0.00]
        FPR = [0.00, 0.00, 0.00]
        for i in range(3):         
            output[i] = []
            output[i].append(cutoff)
            output[i].append(TP[i])
            output[i].append(FP[i])
            output[i].append(FN[i])
            output[i].append(TN[i])
        
        
            if (TP[i]+FP[i])!=0:
                precision[i] = TP[i]/(TP[i]+FP[i])
            else:
                precision[i] = -1.00
                
            if (TP[i]+FN[i])!=0:
                recall[i] = TP[i]/(TP[i]+FN[i])
            
            else:
                recall[i] = -1.00
            
            if precision[i]!=0.00 and recall[i]!=0.00:
                F1[i] = 2.00*(1.00/((1.00/recall[i]) + (1.00/precision[i])))
            else:
                F1[i] = -1.00
            
            
            if (FP[i]+TN[i])!=0:
                FPR[i] = FP[i]/(FP[i]+TN[i])
            else:
                FPR[i] = -1.00
            
            output[i].append(precision[i])
            output[i].append(recall[i])
            output[i].append(F1[i])
            output[i].append(FPR[i])
            
        cutoffDict[cutoff] = output
            
        
        cutoff+=0.01
         
    outputfile = ["./files/quick analysis/DetailPredictionReport/DecisionThreshold/Cosine.csv", "./files/quick analysis/DetailPredictionReport/DecisionThreshold/LM.csv", "./files/quick analysis/DetailPredictionReport/DecisionThreshold/EN.csv"]
    
   
    
    for i in range(3):  
        c = csv.writer(open(outputfile[i], "wb"))
        c.writerow(["cutoff","TP", "FP", "FN", "TN", "Precision","Recall","F1", "FPR"]) 
        
        cutoff = 0.00
        while cutoff<1.01:
            c.writerow(cutoffDict[cutoff][i])
            
            cutoff+=0.01
        
    
    
if __name__ == '__main__':
    main()